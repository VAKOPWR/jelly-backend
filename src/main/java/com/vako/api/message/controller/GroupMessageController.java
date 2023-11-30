package com.vako.api.message.controller;

import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.message.service.GroupMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
