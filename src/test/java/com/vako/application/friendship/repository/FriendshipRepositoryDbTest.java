package com.vako.application.friendship.repository;

import com.vako.DbTestBase;
import com.vako.application.friend.model.Friendship;
import com.vako.application.friend.model.FriendshipStatus;
import com.vako.application.friend.repository.FriendshipRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FriendshipRepositoryDbTest extends DbTestBase {

    private static final String MAIL_1 = "email@mail.com";
    private static final String NICKNAME_1 = "nickname1";

    private static final String MAIL_2 = "email1@mail.com";
    private static final String NICKNAME_2 = "nickname2";
    private User user1 = null;
    private User user2 = null;


    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder().email(MAIL_1).nickname(NICKNAME_1).isShaking(false).stealthChoice(0).build());
        user2 = userRepository.save(User.builder().email(MAIL_2).nickname(NICKNAME_2).isShaking(false).stealthChoice(0).build());
        friendshipRepository.save(Friendship.builder().friendOne(user1).friendTwo(user2).status(FriendshipStatus.PENDING).build());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldCreateFriendPair() {

        //then
        final List<Friendship> allFriendships = friendshipRepository.findAll();
        assertEquals(1, allFriendships.size());
    }



    @Test
    @Disabled
    @Transactional
    void shouldUpdateFriendPairStatus() throws InterruptedException {
        //given


        //when
        int asd = friendshipRepository.updateStatus(user1.getId(), user2.getId(), FriendshipStatus.ACTIVE);

        //then
        final List<Friendship> allFriendships = friendshipRepository.findAll();
        assertEquals(1, allFriendships.size());
        assertEquals(FriendshipStatus.ACTIVE, allFriendships.get(0).getStatus());
    }


    @Test
    @Transactional
    void shouldGetFriendPairsByStatus() {
        //given

        //when
        final List<Friendship> pendingFriendships = friendshipRepository.getFriendshipsByStatus(user1.getEmail(), FriendshipStatus.PENDING);

        //then
        assertEquals(1, pendingFriendships.size());
        assertEquals(FriendshipStatus.PENDING, pendingFriendships.get(0).getStatus());
        assertEquals(user2, pendingFriendships.get(0).getFriendTwo());
    }

}
