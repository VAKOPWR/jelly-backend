package com.vako.application.location.controller;

import com.vako.application.location.model.Message;
import com.vako.application.location.service.MessageService;
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

    @GetMapping("/{senderId}")
    public Optional<Message> getMessageBySenderId(@PathVariable Long senderId) {
        return messageService.getMessageBySenderId(senderId);
    }

    @PostMapping
    public Message createMessage(@RequestBody Message message) {
        return messageService.createMessage(message);
    }

    @PutMapping("/{senderId}")
    public Message updateMessage(@PathVariable Long senderId, @RequestBody Message updatedMessage) throws ChangeSetPersister.NotFoundException {
        return messageService.updateMessage(senderId, updatedMessage);
    }

    @DeleteMapping("/{senderId}")
    public void deleteMessage(@PathVariable Long senderId) {
        messageService.deleteMessage(senderId);
    }
}
