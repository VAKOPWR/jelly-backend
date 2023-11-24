package com.vako.util;

import lombok.Getter;

@Getter
public class IDTokenRequest {

    public IDTokenRequest(String customToken) {
        this.token = customToken;
    }
    private String token;
    private boolean returnSecureToken = true;
}
