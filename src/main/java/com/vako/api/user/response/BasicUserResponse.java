package com.vako.api.user.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BasicUserResponse {

    private Long id;
    private String profilePicture;
    private String nickname;
    private Boolean isOnline;
}
