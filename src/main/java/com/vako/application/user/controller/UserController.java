package com.vako.application.user.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.vako.application.image.BlobStorageService;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final BlobStorageService blobStorageService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping(value = "/avatars", consumes = {"multipart/form-data"})
    public ResponseEntity<String> storeImage(@RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken, @RequestParam("image") final MultipartFile file) throws IOException {
        blobStorageService.saveImage(file, decodedToken.getEmail());
        return ResponseEntity.ok("");
    }

}

