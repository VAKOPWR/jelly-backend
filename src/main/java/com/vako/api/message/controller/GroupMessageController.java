package com.vako.api.message.controller;

import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.message.model.Message;
import com.vako.application.message.model.MessageStatus;
import com.vako.application.message.service.GroupMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v4/chats")
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

    @PutMapping("/createGroupChat")
    public ResponseEntity<String> createGroupChat(
            @RequestParam String chatName,
            @RequestParam String description,
            @RequestBody List<Long> userIds){
        if (description.isBlank()){
            groupMessageService.createGroup(userIds, chatName, null);
        }
        else {
            groupMessageService.createGroup(userIds, chatName, description);
        }
        return null;
    }
}
