package com.vako.util;

import lombok.Getter;

@Getter
public
class IDTokenResponse {

    private String idToken;
    private String refreshToken;
    private String expiresIn;
}