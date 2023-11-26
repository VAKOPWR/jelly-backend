package com.vako.application.user.mapper;

import com.google.firebase.auth.FirebaseToken;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.api.user.response.UserOnlineResponse;
import com.vako.api.user.response.UserStatusResponse;
import com.vako.application.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(source = "name", target = "nickname")
    @Mapping(source = "picture", target = "profilePicture")
    User firebaseTokenToUserMapper(FirebaseToken firebaseToken);

    @Mapping(source = "userStatus.isOnline", target = "isOnline")
    BasicUserResponse userToBasicUserResponse(User user);

    @Mapping(source = "userStatus.positionLon", target = "positionLon")
    @Mapping(source = "userStatus.positionLat", target = "positionLat")
    @Mapping(source = "userStatus.speed", target = "speed")
    @Mapping(source = "userStatus.batteryLevel", target = "batteryLevel")
    UserStatusResponse userToUserStatusResponse(User user);

    @Mapping(source = "userStatus.isOnline", target = "isOnline")
    @Mapping(source = "userStatus.lastOnline", target = "lastOnline")
    UserOnlineResponse userToUserOnlineResponse(User user);
}
