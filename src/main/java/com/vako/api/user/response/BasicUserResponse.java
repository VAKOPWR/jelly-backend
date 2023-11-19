package com.vako.api.user.response;

import lombok.Builder;

@Builder
public class BasicUserResponse {

    private Long id;
    private String profilePicture;
    private String nickname;
    private Boolean isOnline;
}
