package com.vako.application.user.controller;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationUpdateRequest {

    private BigDecimal latitude;

    private BigDecimal longitude;
}
