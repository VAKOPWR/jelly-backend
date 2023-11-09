package com.vako.application.friendship.repository;

import com.vako.DbTestBase;
import com.vako.application.friend.model.Friendship;
import com.vako.application.friend.model.FriendshipStatus;
import com.vako.application.friend.repository.FriendshipRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FriendshipRepositoryDbTest extends DbTestBase {

    public static final String MAIL_1 = "email@mail.com";
    public static final String NICKNAME_1 = "nickname1";

    public static final String MAIL_2 = "email1@mail.com";
    public static final String NICKNAME_2 = "nickname2";

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void shouldCreateFriendPair() {
        //given
        final User user1 = userRepository.save(User.builder().email(MAIL_1).nickname(NICKNAME_1).build());
        final User user2 = userRepository.save(User.builder().email(MAIL_2).nickname(NICKNAME_2).build());

        //when
        friendshipRepository.save(Friendship.builder().friendOne(user1).friendTwo(user2).status(FriendshipStatus.PENDING).build());

        //then
        final List<Friendship> allFriendships = friendshipRepository.findAll();
        assertEquals(allFriendships.size(), 1);
    }

}
