package com.vako.application.user.service;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.request.UserStatusUpdateRequest;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.application.user.model.User;
import com.vako.application.user.model.UserStatus;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.repository.UserStatusRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserStatusRepository userStatusRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public BasicUserResponse getBasicUserByIdentifier(String identifier) {
        final User user = getUserByIdentifier(identifier);
        return BasicUserResponse.builder()
                .id(user.getId())
                .isOnline(user.getUserStatus().isOnline())
                .profilePicture(user.getProfilePicture())
                .nickname(user.getNickname())
                .build();
    }

    public User getUserById(Long id) {
        final User user = userRepository.findById(id).orElseThrow();
        return user;
    }

    public User getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier).orElseThrow();
    }

    public boolean createUserIfDoesntExist(FirebaseToken token) {
        if (userRepository.existsByEmail(token.getEmail()))
            return false;
        final User userToSave = User.builder()
                .email(token.getEmail())
                .nickname(token.getName())
                .profilePicture(token.getPicture())
                .userStatus(UserStatus.builder()
                        .isShaking(false)
                        .isOnline(false)
                        .version(1L)
                        .build())
                .build();
        userRepository.save(userToSave);
        return true;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateLocation(String email, UserStatusUpdateRequest userStatusUpdateRequest) {
        final User user = getUserByEmail(email);
        userStatusRepository.updateLocation(user.getId(), userStatusUpdateRequest.getLatitude(), userStatusUpdateRequest.getLongitude(), userStatusUpdateRequest.getSpeed());
    }
}
