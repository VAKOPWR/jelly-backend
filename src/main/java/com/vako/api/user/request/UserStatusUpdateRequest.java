package com.vako.api.user.request;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserStatusUpdateRequest {

    private BigDecimal latitude;

    private BigDecimal longitude;

    private float speed;
}
