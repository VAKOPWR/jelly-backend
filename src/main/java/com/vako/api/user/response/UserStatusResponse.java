package com.vako.api.user.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class UserStatusResponse {

    private Long id;
    private BigDecimal positionLat;
    private BigDecimal positionLon;
    private float speed;
    private int batteryLevel;

}
