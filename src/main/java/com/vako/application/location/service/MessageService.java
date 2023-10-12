package com.vako.application.location.service;

import com.vako.application.location.model.Message;
import com.vako.application.location.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageBySenderId(Long senderId) {
        return messageRepository.findById(senderId);
    }

    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    public Message updateMessage(Long senderId, Message updatedMessage) throws ChangeSetPersister.NotFoundException {
        Optional<Message> existingMessage = messageRepository.findById(senderId);

        if (existingMessage.isPresent()) {
            updatedMessage.setSenderId(senderId);
            return messageRepository.save(updatedMessage);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    public void deleteMessage(Long senderId) {
        messageRepository.deleteById(senderId);
    }
}

