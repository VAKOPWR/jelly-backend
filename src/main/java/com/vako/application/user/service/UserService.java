package com.vako.application.user.service;

import com.vako.application.user.controller.LocationUpdateRequest;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) throws ChangeSetPersister.NotFoundException {
        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isPresent()) {
            updatedUser.setId(id);
            return userRepository.save(updatedUser);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    @Transactional
    public void storeLocation(final LocationUpdateRequest location, final String userName) {
        if (userRepository.existsByNickname(userName))
            userRepository.updateUserLocation(location.getLongitude(), location.getLatitude(), userName);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
