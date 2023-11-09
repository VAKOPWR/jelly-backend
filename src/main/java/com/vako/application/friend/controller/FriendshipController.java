package com.vako.application.friend.controller;

import com.google.firebase.auth.FirebaseToken;
import com.vako.application.friend.service.FriendshipService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friend")
@AllArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    @PostMapping("/invite/{nickname}")
    public ResponseEntity<Void> sendFriendRequest(@PathVariable("nickname") final String nickname, @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        friendshipService.sendFriendRequest(decodedToken.getEmail(), nickname);
        return ResponseEntity.ok().build();
    }
}
