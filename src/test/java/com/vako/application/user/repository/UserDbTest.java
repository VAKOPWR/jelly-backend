package com.vako.application.user.repository;

import com.vako.DbTestBase;
import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import com.vako.application.user.model.UserStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDbTest extends DbTestBase {

    private static final String MAIL_1 = "email@mail.com";
    private static final String NICKNAME_1 = "nickname1";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @Transactional
    void shouldUpdateUserStatusGivenUserWasCreated() {
        //given
        final var user = userRepository.save(User.builder().email(MAIL_1).nickname(NICKNAME_1).stealthChoice(StealthChoice.PRECISE)
                        .userStatus(UserStatus.builder()
                                .isShaking(false)
                                .isOnline(false)
                                .version(1L)
                                .build())
                .build());
        //when
        userStatusRepository.updateLocation(user.getId(), BigDecimal.ONE, BigDecimal.ONE, 23.4f);

        //then
        final List<UserStatus> userStatuses = userStatusRepository.findAll();
        assertEquals(1, userStatuses.size());
    }

}
