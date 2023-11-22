package com.vako.api.user.controller;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.request.UserStatusUpdateRequest;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.application.image.BlobStorageService;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final BlobStorageService blobStorageService;

    @PostMapping("/create")
    public ResponseEntity<Boolean> createUser(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken) {
        final boolean wasCreated = userService.createUserIfDoesntExist(decodedToken);
        return ResponseEntity.ok(wasCreated);
    }

    @PostMapping(value = "/avatars", consumes = {"multipart/form-data"})
    public ResponseEntity<String> storeImage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @RequestParam("image") final MultipartFile file) throws IOException {
        blobStorageService.saveImage(file, decodedToken.getEmail());
        return ResponseEntity.ok("");
    }

    @PutMapping("/status/update")
    public ResponseEntity<Void> updateLocation(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, final UserStatusUpdateRequest userStatusUpdateRequest) {
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

    @GetMapping("/search/{nickname}")
    public ResponseEntity<List<BasicUserResponse>> getUsersWithNicknameLike(@PathVariable("nickname") final String nickname, @QueryParam("pagesize") final Integer pageSize) {
        final List<BasicUserResponse> usersWithNicknameLike = userService.usersWithNicknameLike(nickname, pageSize);
        return ResponseEntity.ok(usersWithNicknameLike);
    }

}

