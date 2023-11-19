package com.vako.api.user.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class UserStatusResponse {

    private Long id;
    private BigDecimal positionLat;
    private BigDecimal positionLon;
    private float speed;
    private Boolean isShaking;
    private int batteryLevel;
    private boolean isOnline;

}
