package com.vako.api.user.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserStatusResponse {

    private Long id;
    private String nickname;
    private BigDecimal positionLat;
    private BigDecimal positionLon;
    private float speed;
    private int batteryLevel;

    public UserStatusResponse(Long id) {
        this.id = id;
        this.positionLat = BigDecimal.ZERO;
        this.positionLon = BigDecimal.ZERO;
        this.speed = 0.0f;
        this.batteryLevel = 0;
    }
}
