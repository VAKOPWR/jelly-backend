package com.vako.application.user.service;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.request.UserStatusUpdateRequest;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.application.user.mapper.UserMapper;
import com.vako.application.user.model.User;
import com.vako.application.user.model.UserStatus;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.repository.UserStatusRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {



    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final UserStatusRepository userStatusRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public BasicUserResponse getBasicUserByIdentifier(String identifier) {
        return userMapper.userToBasicUserResponse(getUserByIdentifier(identifier));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier).orElseThrow();
    }

    public User createUserIfDoesntExist(FirebaseToken token) {
        if (userRepository.existsByEmail(token.getEmail()))
            return null;
        return createUser(token);
    }

    private User createUser(FirebaseToken token) {
        final User user = userMapper.firebaseTokenToUserMapper(token);
        return userStatusRepository.save(new UserStatus(userMapper.firebaseTokenToUserMapper(token))).getUser();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateLocation(String email, UserStatusUpdateRequest userStatusUpdateRequest) {
        final User user = getUserByEmail(email);
        final int updates = userStatusRepository.updateLocation(user.getId(), userStatusUpdateRequest.getLongitude(), userStatusUpdateRequest.getLatitude(), userStatusUpdateRequest.getSpeed());
        if (updates == 1) log.info("Updated user status for user with email {}", email);
    }



}
