package com.vako.api.message.controller;

import com.vako.application.message.model.Message;
import com.vako.application.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/{groupId}")
    public List<Message> getMessagesBySenderAndGroup(
            @PathVariable Long groupId
    ) {
        return messageService.getMessagesByGroup(groupId);
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
