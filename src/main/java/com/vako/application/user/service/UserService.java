package com.vako.application.user.service;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.request.UserStatusUpdateRequest;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.application.image.BlobStorageService;
import com.vako.application.user.mapper.UserMapper;
import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import com.vako.application.user.model.UserStatus;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.repository.UserStatusRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double ACCEPTABLE_RADIUS_KM = 1.0;

    private final AtomicLong kyryloCounter = new AtomicLong(0L);

    private final String avatarUrl;

    private final BlobStorageService blobStorageService;

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final UserStatusRepository userStatusRepository;

    @Autowired
    public UserService(@Value("${azure.blob.url.avatars}")final String avatarUrl,
                       final BlobStorageService blobStorageService,
                       final UserMapper userMapper,
                       final UserRepository userRepository,
                       final UserStatusRepository userStatusRepository){
        this.avatarUrl = avatarUrl;
        this.blobStorageService = blobStorageService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public BasicUserResponse getBasicUserByIdentifier(String identifier) {
        return userMapper.userToBasicUserResponse(getUserByIdentifier(identifier));
    }

    public BasicUserResponse getBasicUserByUserEmail(String email) {
        return userMapper.userToBasicUserResponse(getUserByEmail(email));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier).orElseThrow();
    }

    public User createUserIfDoesntExist(FirebaseToken token) {
        final Optional<User> user = userRepository.findByEmail(token.getEmail());
        return user.orElseGet(() -> createUser(token));
    }

    public void updateRegistrationToken(final String email, final String token) {
        userRepository.updateRegistrationToken(email, token);
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
        if (updates == 1 && email.equals("k.kostakov2002@gmail.com"))
            log.debug("Request to update location by Kyrylo: {}", kyryloCounter.incrementAndGet());
    }

    @Transactional
    public void updateNickname(String email, String newNickname) {
        userRepository.updateNickname(email, newNickname);
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
    public String updateAvatar(final String email, final MultipartFile file) throws IOException {
        final String uuid = UUID.randomUUID().toString();
        blobStorageService.saveAvatar(file, uuid);
        final String link = avatarUrl + uuid;
        userRepository.updateAvatarId(email, link);
        return link;
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
