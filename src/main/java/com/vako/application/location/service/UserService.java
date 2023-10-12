package com.vako.application.location.service;

import com.vako.application.location.model.User;
import com.vako.application.location.repository.UserRepository;
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

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
