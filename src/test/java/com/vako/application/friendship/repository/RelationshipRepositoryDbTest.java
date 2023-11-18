package com.vako.application.friendship.repository;

import com.vako.DbTestBase;
import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.model.RelationshipStatus;
import com.vako.application.relationship.repository.RelationshipRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelationshipRepositoryDbTest extends DbTestBase {

    private static final String MAIL_1 = "email@mail.com";
    private static final String NICKNAME_1 = "nickname1";

    private static final String MAIL_2 = "email1@mail.com";
    private static final String NICKNAME_2 = "nickname2";
    private User user1 = null;
    private User user2 = null;


    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder().email(MAIL_1).nickname(NICKNAME_1).isShaking(false).stealthChoice(0).build());
        user2 = userRepository.save(User.builder().email(MAIL_2).nickname(NICKNAME_2).isShaking(false).stealthChoice(0).build());
        relationshipRepository.save(Relationship.builder().userOne(user1).userTwo(user2).status(RelationshipStatus.PENDING).build());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldCreateFriendPair() {

        //then
        final List<Relationship> allRelationships = relationshipRepository.findAll();
        assertEquals(1, allRelationships.size());
    }



    @Test
    @Disabled
    @Transactional
    void shouldUpdateFriendPairStatus() throws InterruptedException {
        //given


        //when
        int asd = relationshipRepository.updateStatus(user1.getId(), user2.getEmail(), RelationshipStatus.ACTIVE);

        //then
        final List<Relationship> allRelationships = relationshipRepository.findAll();
        assertEquals(1, allRelationships.size());
        assertEquals(RelationshipStatus.ACTIVE, allRelationships.get(0).getStatus());
    }


    @Test
    @Transactional
    void shouldGetFriendPairsByStatus() {
        //given

        //when
        final List<Relationship> pendingRelationships = relationshipRepository.getFriendshipsByStatus(user1.getEmail(), RelationshipStatus.PENDING);

        //then
        assertEquals(1, pendingRelationships.size());
        assertEquals(RelationshipStatus.PENDING, pendingRelationships.get(0).getStatus());
        assertEquals(user2, pendingRelationships.get(0).getUserTwo());
    }

}
