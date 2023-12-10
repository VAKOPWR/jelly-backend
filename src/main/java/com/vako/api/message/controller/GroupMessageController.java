package com.vako.api.message.controller;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.message.service.GroupMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/chats")
@AllArgsConstructor
public class GroupMessageController {

    private final GroupMessageService groupMessageService;

    @GetMapping
    public ResponseEntity<List<GroupMessageDTO>> getChats(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken){
        final List<GroupMessageDTO> groupMessageDTOS = groupMessageService.getChats(decodedToken.getEmail());
        return ResponseEntity.ok(groupMessageDTOS);
    }

    @GetMapping("/message/new/{lastChecked}")
    public ResponseEntity<List<MessageDTO>> getMessagesNew(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                           @PathVariable("lastChecked") String lastChecked,
                                           @RequestBody List<Long> groupIds){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime lastCheckedTime = LocalDateTime.parse(lastChecked, formatter);
        final List<MessageDTO> messageDTOS = groupMessageService.loadMessagesNew(lastCheckedTime, groupIds);
        return ResponseEntity.ok(messageDTOS);
    }

    @GetMapping("/new")
    public ResponseEntity<List<GroupMessageDTO>> getNewChats(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                             @RequestBody Set<Long> groupIds) {
        final List<GroupMessageDTO> groupMessageDTOS = groupMessageService.getNewChats(decodedToken.getEmail(), groupIds);
        return ResponseEntity.ok(groupMessageDTOS);
    }



    @PostMapping("/message")
    public ResponseEntity<Long> createMessage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                                @RequestBody final CreateMessageRequest createMessageRequest) {
        final Long messageId = groupMessageService.createMessage(decodedToken.getEmail(), createMessageRequest);
        return ResponseEntity.ok(messageId);
    }

    @PostMapping("/message/image/{messageId}")
    public ResponseEntity<Void> attachImage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                            @RequestParam("image") final MultipartFile file,
                                            @PathVariable("messageId") final Long messageId) throws IOException {
        groupMessageService.attachImage(decodedToken.getEmail(), messageId, file);
        return ResponseEntity.ok().build();
    }
}

