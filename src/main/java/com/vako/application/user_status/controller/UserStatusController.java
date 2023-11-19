package com.vako.application.user_status.controller;

import com.vako.application.user_status.model.UserStatus;
import com.vako.application.user_status.service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/user-status")
public class UserStatusController {

    @Autowired
    private UserStatusService userStatusService;

    @PostMapping
    public UserStatus createUserStatus(@RequestBody UserStatus userStatus) {
        return userStatusService.createUserStatus(userStatus);
    }

    @PutMapping("/{id}/location")
    public void updateLocation(
            @PathVariable Long id,
            @RequestParam BigDecimal positionLat,
            @RequestParam BigDecimal positionLon,
            @RequestParam float speed) {
        userStatusService.updateLocation(id, positionLat, positionLon, speed);
    }
}
