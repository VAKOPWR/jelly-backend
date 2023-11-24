package com.vako.api.user.request;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserStatusUpdateRequest {

    private BigDecimal longitude;
    private BigDecimal latitude;
    private float speed;
}
