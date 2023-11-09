package com.vako.application.user.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

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

    @PutMapping("/location/update")
    public ResponseEntity updateLocation(@RequestBody final LocationUpdateRequest location,
                                         @RequestAttribute(name = "FirebaseToken") final FirebaseToken decodedToken)
            throws FirebaseAuthException {
        userService.storeLocation(location, decodedToken);
        return ResponseEntity.ok().build();
    }
}

