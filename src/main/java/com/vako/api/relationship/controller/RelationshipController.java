package com.vako.api.relationship.controller;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.api.user.response.UserOnlineResponse;
import com.vako.api.user.response.UserStatusResponse;
import com.vako.application.relationship.service.RelationshipService;
import com.vako.application.user.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
@RequestMapping("/api/v1/friend")
@AllArgsConstructor
@Slf4j
public class RelationshipController {

    private final RelationshipService relationshipService;
    @PostMapping("/invite/{id}")
    public ResponseEntity<Void> sendFriendRequestById(@PathVariable("id") final Long id, @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        relationshipService.sendFriendRequest(decodedToken.getEmail(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFriendRequestById(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @PathVariable("id") final Long id){
        relationshipService.deleteFriendship(decodedToken.getEmail(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BasicUserResponse>> getPendingRequests(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        final List<BasicUserResponse> pendingRequests = relationshipService.getPendingRequests(decodedToken.getEmail());
        return ResponseEntity.ok(pendingRequests);
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveFriends(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        final List<User> activeFriends = relationshipService.getActiveFriends(decodedToken.getEmail());
        return ResponseEntity.ok(activeFriends);
    }

    @GetMapping("/online")
    public ResponseEntity<List<UserOnlineResponse>> getFriendsActivity(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        final List<UserOnlineResponse> friendsActivity = relationshipService.getFriendsWithTheirActivityStatuses(decodedToken.getEmail());
        return ResponseEntity.ok(friendsActivity);
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<Void> acceptFriendRequests(@PathVariable("id") final Long id, @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        relationshipService.acceptFriendRequest(decodedToken.getEmail(), id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/decline/{id}")
    public ResponseEntity<Void> declineFriendRequests(@PathVariable("id") final Long id, @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        relationshipService.declineFriendRequest(decodedToken.getEmail(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/basic")
    public ResponseEntity<List<BasicUserResponse>> getBasicFriendInfo(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        final List<BasicUserResponse> basicFriendInfo = relationshipService.getBasicFriendInfo(decodedToken.getEmail());
        return ResponseEntity.ok(basicFriendInfo);
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<UserStatusResponse>> getFriendStatuses(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        final List<UserStatusResponse> friendStatuses = relationshipService.getFriendStatuses(decodedToken.getEmail());
        return ResponseEntity.ok(friendStatuses);
    }

    @GetMapping("/search/{nickname}")
    public ResponseEntity<List<BasicUserResponse>> getUsersWithNicknameLike(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @PathVariable("nickname") final String nickname, @QueryParam("pageSize") final Integer pageSize) {
        log.info("Received request to find usernames containing {}", nickname);
        final List<BasicUserResponse> usersWithNicknameLike = relationshipService.usersWithNicknameLike(decodedToken.getEmail(), nickname, pageSize);
        return ResponseEntity.ok(usersWithNicknameLike);
    }
}
