package com.vako.application.user;


import com.vako.application.user.model.User;
import com.vako.application.user.model.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatusTest {

    @Test
    void shouldReturnIsOnlineFalseIfLastUpdatedGreaterThanFiveMinutes() {
        //given
        final UserStatus userStatus = new UserStatus(new User());
        userStatus.setTimestamp(LocalDateTime.now().minusMinutes(6));
        //when
        final Boolean isOnline = userStatus.getIsOnline();

        //then
        assertThat(isOnline).isFalse();
    }

    @Test
    void shouldReturnIsOnlineTrueIfLastUpdatedLessThanFiveMinutes() {
        //given
        final UserStatus userStatus = new UserStatus(new User());
        userStatus.setTimestamp(LocalDateTime.now().minusMinutes(4));

        //when
        final Boolean isOnline = userStatus.getIsOnline();

        //then
        assertThat(isOnline).isTrue();
    }
}
