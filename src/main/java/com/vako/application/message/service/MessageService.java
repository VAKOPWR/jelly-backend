package com.vako.application.message.service;

import com.vako.application.message.model.Message;
import com.vako.application.message.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public List<Message> getMessagesByGroup(Long groupId) {
        return messageRepository.findByGroup_Id(groupId);
    }

    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    public void deleteMessage(Long senderId, Long groupId) {
        messageRepository.deleteByUserIdAndGroupId(senderId, groupId);
    }
}

