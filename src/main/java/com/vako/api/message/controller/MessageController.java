package com.vako.api.message.controller;

import com.vako.application.message.model.Message;
import com.vako.application.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @PostMapping
    public Message createMessage(@RequestBody Message message) {
        return messageService.createMessage(message);
    }

    @DeleteMapping("/{senderId}/{groupId}")
    public void deleteMessage(
            @PathVariable Long senderId,
            @PathVariable Long groupId
    ) {
        messageService.deleteMessage(senderId, groupId);
    }
}
