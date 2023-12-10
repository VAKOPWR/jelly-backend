package com.vako.api.group.controller;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.application.dto.MessageDTO;
import com.vako.application.dto.NewGroupChatDTO;
import com.vako.application.group.service.GroupService;
import com.vako.application.message.service.GroupMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupMessageService groupMessageService;

    @PostMapping( "/create")
    public ResponseEntity<NewGroupChatDTO> createGroupChat(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                                @RequestBody final CreateGroupChatRequest createGroupChatRequest) {
        final NewGroupChatDTO newGroupChatDTO = groupService.createGroup(createGroupChatRequest);
        return ResponseEntity.ok(newGroupChatDTO);
    }

    @PostMapping(value = "/avatar/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<String> storeImage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                             @RequestParam("image") final MultipartFile file,
                                             @PathVariable("id") final Long groupId) throws IOException {
        groupService.updateAvatar(decodedToken.getEmail(), groupId, file);
        return ResponseEntity.ok("");
    }

    @GetMapping("/message/{groupId}")
    public List<MessageDTO> getMessagesByGroup(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
            @PathVariable("groupId") Long groupId,
            @QueryParam("pageNo") final Integer pageNo) {
        return groupMessageService.loadMessagesPaged(groupId, pageNo);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
    }
}

