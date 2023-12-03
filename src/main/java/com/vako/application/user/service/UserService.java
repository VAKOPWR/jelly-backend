package com.vako.application.user.service;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.request.UserStatusUpdateRequest;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.application.user.mapper.UserMapper;
import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import com.vako.application.user.model.UserStatus;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.repository.UserStatusRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double ACCEPTABLE_RADIUS_KM = 1.0;

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
        return userStatusRepository.save(new UserStatus(userMapper.firebaseTokenToUserMapper(token))).getUser();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateLocation(String email, UserStatusUpdateRequest userStatusUpdateRequest) {
        final User user = getUserByEmail(email);
        final int updates = userStatusRepository.updateLocation(user.getId(), userStatusUpdateRequest.getLongitude(), userStatusUpdateRequest.getLatitude(), userStatusUpdateRequest.getSpeed(), userStatusUpdateRequest.getBatteryLevel(), LocalDateTime.now());
        if (updates == 1) log.debug("Updated user status for user with email {}", email);
    }

    public List<BasicUserResponse> findUsersNearLocation(String email) {
        final User user = getUserByEmail(email);
        List<UserStatus> allUsersWhoAreShaking = userStatusRepository.findAllUsersWhoAreShaking(user.getId());

        return allUsersWhoAreShaking.stream()
                .filter(us -> calculateDistance(
                        user.getUserStatus().getPositionLat(),
                        user.getUserStatus().getPositionLon(),
                        us.getPositionLat(),
                        us.getPositionLon()
                ) <= ACCEPTABLE_RADIUS_KM)
                .map(UserStatus::getUser)
                .map(userMapper::userToBasicUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateShakingStatus(String email, Boolean shakingStatus) {
        final User user = getUserByEmail(email);
        final int updates = userStatusRepository.updateIsShaking(user.getId(), shakingStatus);
        if (updates == 1) log.debug("Updated user shaking status for user with email {}", email);
    }

    @Transactional
    public void updateStealthChoice(String email, StealthChoice stealthChoice) {
        final User user = getUserByEmail(email);
        final int updates = userRepository.updateStealthChoice(user.getId(), stealthChoice);
        if (updates == 1) log.info("Updated user stealth status for user with email {}", email);
    }

    private double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }


}
