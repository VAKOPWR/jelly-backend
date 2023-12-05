package com.vako.api.message.controller;

import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.message.model.MessageStatus;
import com.vako.application.message.service.GroupMessageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping("/{userId}")
    public List<GroupMessageDTO> getChats (@PathVariable Long userId){
        return groupMessageService.getChats(userId);
    }

    @GetMapping("/loadMessagesNew")
    public List<MessageDTO> getMessagesNew(@RequestParam LocalDateTime lastChecked, @RequestBody List<Long> groupIds){
        return groupMessageService.loadMessagesNew(lastChecked, groupIds);
    }

    @GetMapping("/loadMessagesPaged")
    public List<MessageDTO> getMessagesByGroup(
            @RequestParam Long groupId,
            @RequestParam int page) {
        return groupMessageService.loadMessagesPaged(groupId, page);
    }

    @PutMapping("/sendMessage")
    public ResponseEntity<String> createMessage(
            @RequestParam Long groupId,
            @RequestParam Long senderId,
            @RequestParam String text,
            @RequestParam String timeSent,
            @RequestParam String messageStatus,
            @RequestParam(required = false) String attachedPhoto) {

        LocalDateTime time = LocalDateTime.parse(timeSent);

        groupMessageService.createMessage(senderId, groupId, text, MessageStatus.valueOf(messageStatus), time, attachedPhoto);

        return ResponseEntity.ok("Message sent successfully");
    }

    @PutMapping(
            value = "/createGroupChat",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createGroupChat(
            @RequestBody Map<String, Object> requestPayload) {
        String chatName = (String) requestPayload.get("chatName");
        String description = (String) requestPayload.get("description");
        List<Integer> userIds = (List<Integer>) requestPayload.get("userIds");

        List<Long> userIdsLong = userIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());
        if (description == null || description.isBlank()) {
            groupMessageService.createGroup(userIdsLong, chatName, null);
        } else {
            groupMessageService.createGroup(userIdsLong, chatName, description);
        }

        return ResponseEntity.ok("Group created successfully");
    }

}

