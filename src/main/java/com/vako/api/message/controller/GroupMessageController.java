package com.vako.api.message.controller;

import com.google.api.client.util.DateTime;
import com.google.firebase.auth.FirebaseToken;
import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.message.service.GroupMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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

    @PostMapping("/message/new/{userId}")
    public ResponseEntity<List<MessageDTO>> getMessagesNew(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                           @PathVariable Long userId,
                                           @RequestBody List<Long> groupIds){
        final List<MessageDTO> messageDTOS = groupMessageService.loadMessagesNew(groupIds, userId);
        return ResponseEntity.ok(messageDTOS);
    }

    @PostMapping("/new")
    public ResponseEntity<List<GroupMessageDTO>> getNewChats(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                             @RequestBody List<Long> groupIds) {
        final List<GroupMessageDTO> groupMessageDTOS = groupMessageService.getNewChats(decodedToken.getEmail(), new HashSet<>(groupIds));
        return ResponseEntity.ok(groupMessageDTOS);
    }



    @PostMapping("/message")
    public ResponseEntity<String> createMessage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                                @RequestBody final CreateMessageRequest createMessageRequest) {
        final String sentTime = groupMessageService.createMessage(decodedToken.getEmail(), createMessageRequest);
        return ResponseEntity.ok(sentTime);
    }

    @PostMapping("/message/image/{messageId}")
    public ResponseEntity<Void> attachImage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                            @RequestParam("image") final MultipartFile file,
                                            @PathVariable("messageId") final Long messageId) throws IOException {
        groupMessageService.attachImage(decodedToken.getEmail(), messageId, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/message/{groupId}")
    public List<MessageDTO> getMessagesByGroup(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
            @PathVariable("groupId") Long groupId,
            @QueryParam("pageToLoad") final Integer pageToLoad) {
        return groupMessageService.loadMessagesPaged(groupId, pageToLoad);
    }
}

