package com.vako.api.message.controller;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.dto.NewGroupChatDTO;
import com.vako.application.message.model.MessageStatus;
import com.vako.application.message.service.GroupMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chats")
@AllArgsConstructor
public class GroupMessageController {

    private final GroupMessageService groupMessageService;

    @GetMapping
    public List<GroupMessageDTO> getChats(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken){
        return groupMessageService.getChats(decodedToken.getEmail());
    }

    @GetMapping("/message/new/{lastChecked}")
    public List<MessageDTO> getMessagesNew(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                           @PathVariable("lastChecked") String lastChecked,
                                           @RequestBody List<Long> groupIds){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime lastCheckedTime = LocalDateTime.parse(lastChecked, formatter);
        return groupMessageService.loadMessagesNew(lastCheckedTime, groupIds);
    }



    @PostMapping("/message")
    public ResponseEntity<Void> createMessage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                                @RequestBody final CreateMessageRequest createMessageRequest) {
        groupMessageService.createMessage(decodedToken.getEmail(), createMessageRequest);
        return ResponseEntity.ok().build();
    }



}

