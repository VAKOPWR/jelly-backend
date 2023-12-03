package com.vako.api.message.controller;

import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.message.model.Message;
import com.vako.application.message.service.GroupMessageService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    @GetMapping("/loadMessages")
    public Page<Message> getMessagesByGroup(
            @RequestParam Long groupId,
            @RequestParam int page) {
        return groupMessageService.loadMessages(groupId, page);
    }
}
