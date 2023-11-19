package com.vako.application.user.service;

import com.google.firebase.auth.FirebaseToken;
import com.vako.application.user.controller.LocationUpdateRequest;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public User getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier).orElseThrow();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
