package com.vako.api.user.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.request.UserStatusUpdateRequest;
import com.vako.application.image.BlobStorageService;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final BlobStorageService blobStorageService;

    @PostMapping
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
    public void updateLocation(
            @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, final UserStatusUpdateRequest userStatusUpdateRequest) {
        userService.updateLocation(decodedToken.getEmail(), userStatusUpdateRequest);
    }

}

