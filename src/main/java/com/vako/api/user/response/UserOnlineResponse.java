package com.vako.api.user.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserOnlineResponse {

    private Long id;
    private String profilePicture;
    private Boolean isOnline;
    private String lastOnline;

}
