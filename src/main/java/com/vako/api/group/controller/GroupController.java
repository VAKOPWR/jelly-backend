package com.vako.api.group.controller;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.application.dto.MessageDTO;
import com.vako.application.group.service.GroupService;
import com.vako.application.message.service.GroupMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
@RequestMapping("/api/v1/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupMessageService groupMessageService;

    @PostMapping( "/create")
    public ResponseEntity<Void> createGroupChat(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                                @RequestBody final CreateGroupChatRequest createGroupChatRequest) {
        groupService.createGroup(createGroupChatRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/message/{groupId}")
    public List<MessageDTO> getMessagesByGroup(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
            @PathVariable("groupId") Long groupId,
            @QueryParam("pageSize") final Integer pageSize) {
        return groupMessageService.loadMessagesPaged(groupId, pageSize);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
    }
}

