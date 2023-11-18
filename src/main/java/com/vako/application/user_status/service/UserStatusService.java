package com.vako.application.user_status.service;

import com.vako.application.user_status.model.UserStatus;
import com.vako.application.user_status.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserStatusService {

    @Autowired
    private UserStatusRepository userStatusRepository;

    public UserStatus createUserStatus(UserStatus userStatus) {
        return userStatusRepository.save(userStatus);
    }

    public UserStatus updateLocation(Long userStatusId, BigDecimal positionLat, BigDecimal positionLon, float speed) {
        UserStatus existingUserStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new RuntimeException("UserStatus not found"));

        existingUserStatus.setPositionLat(positionLat);
        existingUserStatus.setPositionLon(positionLon);
        existingUserStatus.setSpeed(speed);

        return userStatusRepository.save(existingUserStatus);
    }
}
