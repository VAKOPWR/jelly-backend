package com.vako.api.user.controller;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.request.UserStatusUpdateRequest;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.application.image.BlobStorageService;
import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<String> pingUser(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/nickname/{nickname}")
    public ResponseEntity<String> editNickname(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                               @PathVariable("nickname") final String newNickname) {
        userService.updateNickname(decodedToken.getEmail(), newNickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        final User wasCreated = userService.createUserIfDoesntExist(decodedToken);
        return ResponseEntity.ok(wasCreated);
    }

    @PostMapping(value = "/avatars", consumes = {"multipart/form-data"})
    public ResponseEntity<String> storeImage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @RequestParam("image") final MultipartFile file) throws IOException {
        userService.updateAvatar(decodedToken.getEmail(), file);
        return ResponseEntity.ok("");
    }

    @PutMapping("/status/update")
    public ResponseEntity<Void> updateLocation(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @RequestBody final UserStatusUpdateRequest userStatusUpdateRequest) {
        userService.updateLocation(decodedToken.getEmail(), userStatusUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/basic/{identifier}")
    public ResponseEntity<BasicUserResponse> getUserByIdentifier(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @PathVariable("identifier") final String identifier) {
        final BasicUserResponse basicUserResponse = userService.getBasicUserByIdentifier(identifier);
        return ResponseEntity.ok(basicUserResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @PathVariable("id") final Long id) {
        final User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<User> deleteUserById(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @PathVariable("id") final Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<BasicUserResponse>> getNearbyUsers(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        final List<BasicUserResponse> nearbyUsers = userService.findUsersNearLocation(decodedToken.getEmail());
        return ResponseEntity.ok(nearbyUsers);
    }

    @PutMapping("/shaking/update/{isShaking}")
    public ResponseEntity<Void> updateShakingStatus(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
            @PathVariable("isShaking") final Boolean shakingStatus
    ) {
        userService.updateShakingStatus(decodedToken.getEmail(), shakingStatus);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ghost/update/{stealthChoice}")
    public ResponseEntity<Void> updateStealthChoice(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
            @PathVariable("stealthChoice") final StealthChoice stealthChoice
    ) {
        userService.updateStealthChoice(decodedToken.getEmail(), stealthChoice);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/fcm/update/{token}")
    public ResponseEntity<Void> updateFcmToken(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken,
                                               @PathVariable("token") String token) {
        userService.updateRegistrationToken(decodedToken.getEmail(), token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getId/{email}")
    public ResponseEntity<Integer> getIdByEmail(@PathVariable("email") String email){
        final int id = userService.getUserByEmail(email).getId().intValue();
        return ResponseEntity.ok(id);
    }
}

