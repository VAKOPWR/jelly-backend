package com.vako.api.message.controller;

import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.dto.NewGroupChatDTO;
import com.vako.application.message.model.MessageStatus;
import com.vako.application.message.service.GroupMessageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/api/v1/chats")
@RequestMapping("/chats")
public class GroupMessageController {

    private final GroupMessageService groupMessageService;

    public GroupMessageController(GroupMessageService groupMessageService) {
        this.groupMessageService = groupMessageService;
    }

    @GetMapping("/{userEmail}")
    public List<GroupMessageDTO> getChats (@PathVariable String userEmail){
        return groupMessageService.getChats(userEmail);
    }

    @GetMapping("/loadMessagesNew/{lastChecked}")
    public List<MessageDTO> getMessagesNew(@PathVariable String lastChecked, @RequestBody() List<Long> groupIds){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime lastCheckedTime = LocalDateTime.parse(lastChecked, formatter);
        return groupMessageService.loadMessagesNew(lastCheckedTime, groupIds);
    }

    @GetMapping("/loadMessagesPaged")
    public List<MessageDTO> getMessagesByGroup(
            @RequestParam Long groupId,
            @RequestParam int page) {
        return groupMessageService.loadMessagesPaged(groupId, page);
    }

    @PutMapping(
            value = "/sendMessage",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createMessage(@RequestBody Map<String, Object> requestPayload) {
        LocalDateTime time = LocalDateTime.parse((String) requestPayload.get("timeSent"));

        groupMessageService.createMessage(
                parseLong(requestPayload.get("senderId")),
                parseLong(requestPayload.get("groupId")),
                (String) requestPayload.get("text"),
                MessageStatus.SENT,
                time,
                (String) requestPayload.get("attachedPhoto"));

        return ResponseEntity.ok("Message sent successfully");
    }

    @PutMapping(
            value = "/createGroupChat",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public NewGroupChatDTO createGroupChat(
            @RequestBody Map<String, Object> requestPayload) {
        String chatName = (String) requestPayload.get("chatName");
        String description = (String) requestPayload.get("description");
        List<Integer> userIds = (List<Integer>) requestPayload.get("userIds");

        List<Long> userIdsLong = userIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());
        if (description == null || description.isBlank()) {
            return groupMessageService.createGroup(userIdsLong, chatName, null);
        }
        return groupMessageService.createGroup(userIdsLong, chatName, description);
    }

    private Long parseLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            return Long.valueOf((String) value);
        } else {
            throw new IllegalArgumentException("Invalid value for Long parsing");
        }
    }

}

