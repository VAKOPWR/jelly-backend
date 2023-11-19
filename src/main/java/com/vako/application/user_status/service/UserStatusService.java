package com.vako.application.user_status.service;

import com.vako.application.user_status.model.UserStatus;
import com.vako.application.user_status.repository.UserStatusRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void updateLocation(Long userId, BigDecimal positionLat, BigDecimal positionLon, float speed) {
        userStatusRepository.updateLocation(userId, positionLat, positionLon, speed);
    }
}
