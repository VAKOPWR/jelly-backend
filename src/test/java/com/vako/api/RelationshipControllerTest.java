package com.vako.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.vako.DbTestBase;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.api.user.response.UserOnlineResponse;
import com.vako.api.user.response.UserStatusResponse;
import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.repository.RelationshipRepository;
import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.repository.UserStatusRepository;
import com.vako.util.IDTokenRequest;
import com.vako.util.IDTokenResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.vako.application.relationship.model.RelationshipStatus.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
public class RelationshipControllerTest extends DbTestBase {

    private String idTokenFriendOne;
    private String idTokenFriendTwo;

    private User friendOne;

    private User friendTwo;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() throws FirebaseAuthException, IOException, InterruptedException {
        idTokenFriendOne = getIdTokenForUid(UID_1);
        idTokenFriendTwo = getIdTokenForUid(UID_2);
    }

    @BeforeEach
    void setUpUser() throws Exception {
        MvcResult user1Response = mockMvc.perform(MockMvcRequestBuilders
                        .post(API_PATH + "/user/create")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendOne))
                .andExpect(status().isOk())
                .andReturn();
        friendOne = mapper.readValue(user1Response.getResponse().getContentAsString(), User.class);
        MvcResult user2Response = mockMvc.perform(MockMvcRequestBuilders
                        .post(API_PATH + "/user/create")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();
        friendTwo = mapper.readValue(user2Response.getResponse().getContentAsString(), User.class);
    }

    @AfterEach
    void tearDownUser() {
        userRepository.deleteAll();
    }

    private String getIdTokenForUid(String uid) throws FirebaseAuthException, IOException, InterruptedException {
        token = FirebaseAuth.getInstance().createCustomToken(uid);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=AIzaSyAWBlj670EKh5kNYE8-kg7k7cFNYQFfIPQ"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(new IDTokenRequest(token))))
                .build();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), IDTokenResponse.class).getIdToken();
    }

    @Test
    void shouldCreateNewEntityInRelationshipTableWhenInvitingUser() throws Exception {
        //given

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post(API_PATH + "/friend/invite/" + friendTwo.getId())
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendOne))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final Relationship relationship = relationshipRepository.findAll().get(0);
        assertThat(relationship.getUserOne().getId()).isEqualTo(friendOne.getId());
        assertThat(relationship.getUserTwo().getId()).isEqualTo(friendTwo.getId());
        assertThat(relationship.getStatus()).isEqualTo(PENDING);
    }


    @Test
    void shouldUpdateEntityInRelationshipTableWithStatusActiveWhenAcceptingRequest() throws Exception {
        //given
        relationshipRepository.save(new Relationship(friendOne, friendTwo));

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/friend/accept/" + friendOne.getId())
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final Relationship relationship = relationshipRepository.findAll().get(0);
        assertThat(relationship.getUserOne().getId()).isEqualTo(friendOne.getId());
        assertThat(relationship.getUserTwo().getId()).isEqualTo(friendTwo.getId());
        assertThat(relationship.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void shouldUpdateEntityInRelationshipTableWithStatusActiveWhenDecliningRequest() throws Exception {
        //given
        relationshipRepository.save(new Relationship(friendOne, friendTwo));

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/friend/decline/" + friendOne.getId())
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final Relationship relationship = relationshipRepository.findAll().get(0);
        assertThat(relationship.getUserOne().getId()).isEqualTo(friendOne.getId());
        assertThat(relationship.getUserTwo().getId()).isEqualTo(friendTwo.getId());
        assertThat(relationship.getStatus()).isEqualTo(DECLINED);
    }

    @Test
    void shouldDeleteEntityInRelationshipWhenDeletingFriend() throws Exception {
        //given
        relationshipRepository.save(new Relationship(friendOne, friendTwo));

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(API_PATH + "/friend/delete/" + friendOne.getId())
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final List<Relationship> relationship = relationshipRepository.findAll();
        final List<User> users = userRepository.findAll();
        assertThat(relationship).hasSize(0);
        assertThat(users).hasSize(2);
    }

    @Test
    void shouldReturnListOfBasicUserResponsesWhenGettingPendingRequests() throws Exception {
        //given
        relationshipRepository.save(new Relationship(friendOne, friendTwo));

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PATH + "/friend/pending")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final List<BasicUserResponse> basicUserResponses = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(basicUserResponses).hasSize(1);
        assertThat(basicUserResponses.get(0).getNickname()).isEqualTo(friendOne.getNickname());
        assertThat(basicUserResponses.get(0).getIsOnline()).isTrue();
        assertThat(basicUserResponses.get(0).getProfilePicture()).isEqualTo(friendOne.getProfilePicture());
    }

    @Test
    void shouldReturnListOfBasicUserResponsesWhenGettingBasicFriendInfo() throws Exception {
        //given
        relationshipRepository.save(new Relationship(friendOne, friendTwo));
        relationshipRepository.updateStatus(friendOne.getId(), friendTwo.getId(), ACTIVE);

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PATH + "/friend/basic")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final List<BasicUserResponse> basicUserResponses = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(basicUserResponses).hasSize(1);
        assertThat(basicUserResponses.get(0).getNickname()).isEqualTo(friendOne.getNickname());
        assertThat(basicUserResponses.get(0).getIsOnline()).isTrue();
        assertThat(basicUserResponses.get(0).getProfilePicture()).isEqualTo(friendOne.getProfilePicture());
    }

    @Test
    void shouldReturnListOfOnlineUserResponsesWhenGettingBasicFriendInfo() throws Exception {
        //given
        relationshipRepository.save(new Relationship(friendOne, friendTwo));
        relationshipRepository.updateStatus(friendOne.getId(), friendTwo.getId(), ACTIVE);

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PATH + "/friend/online")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final List<UserOnlineResponse> userOnlineResponses = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(userOnlineResponses).hasSize(1);
        assertThat(userOnlineResponses.get(0).getIsOnline()).isTrue();
        assertThat(userOnlineResponses.get(0).getProfilePicture()).isEqualTo(friendOne.getProfilePicture());
        assertThat(userOnlineResponses.get(0).getLastOnline()).isNotEmpty();
    }


    @Test
    void shouldReturnListOfUserStatusResponsesWhenGettingBasicFriendInfo() throws Exception {
        //given
        var lon = BigDecimal.valueOf(2.5);
        var lat = BigDecimal.valueOf(6.5);
        var speed = 23;
        var battery = 32;
        relationshipRepository.save(new Relationship(friendOne, friendTwo));
        relationshipRepository.updateStatus(friendOne.getId(), friendTwo.getId(), ACTIVE);
        userStatusRepository.updateLocation(friendOne.getId(), lon, lat, speed, battery);

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PATH + "/friend/statuses")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final List<UserStatusResponse> userStatusResponses = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(userStatusResponses).hasSize(1);
        assertThat(userStatusResponses.get(0).getId()).isEqualTo(friendOne.getId());
        assertThat(userStatusResponses.get(0).getNickname()).isEqualTo(friendOne.getNickname());
        assertThat(userStatusResponses.get(0).getSpeed()).isEqualTo(speed);
        assertThat(userStatusResponses.get(0).getBatteryLevel()).isEqualTo(battery);
        assertThat(userStatusResponses.get(0).getPositionLon()).isEqualTo(lon);
        assertThat(userStatusResponses.get(0).getPositionLat()).isEqualTo(lat);
    }

    @Test
    void shouldReturnBasicUserResponsesWhenQueryingByNickName() throws Exception {
        //given

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PATH + "/friend/search/" + "or?pageSize=20")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //when
        final List<BasicUserResponse> basicUserResponses = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(basicUserResponses).hasSize(1);
        assertThat(basicUserResponses.get(0).getNickname()).isEqualTo(friendOne.getNickname());
        assertThat(basicUserResponses.get(0).getIsOnline()).isTrue();
        assertThat(basicUserResponses.get(0).getProfilePicture()).isEqualTo(friendOne.getProfilePicture());
    }

    @Test
    void shouldReturnListOfNearbyUsers() throws Exception {
        //given
        var lon1 = BigDecimal.valueOf(2.5);
        var lat1 = BigDecimal.valueOf(6.5);
        var speed1 = 23;

        var lon2 = BigDecimal.valueOf(2.5);
        var lat2 = BigDecimal.valueOf(6.5);
        var speed2 = 23;
        userStatusRepository.updateLocation(friendOne.getId(), lon1, lat1, speed1);
        userStatusRepository.updateIsShaking(friendOne.getId(), true);
        userStatusRepository.updateLocation(friendTwo.getId(), lon2, lat2, speed2);
        userStatusRepository.updateIsShaking(friendTwo.getId(), true);

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PATH + "/user/nearby")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final List<BasicUserResponse> nearbyUsers = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(nearbyUsers).hasSize(1);
        assertThat(nearbyUsers.get(0).getId()).isEqualTo(friendOne.getId());
    }

    @Test
    void shouldUpdateStealthChoiceForUserTwoInRelationship() throws Exception {
        //given
        relationshipRepository.save(new Relationship(friendOne, friendTwo));
        relationshipRepository.updateStatus(friendOne.getId(), friendTwo.getId(), ACTIVE);
        final StealthChoice stealthChoiceForUserTwo = StealthChoice.HIDE;

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/friend/ghost/update/" + friendOne.getId() + "/" + stealthChoiceForUserTwo)
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        Relationship relationship = relationshipRepository.getRelationshipByUserIds(friendOne.getId(), friendTwo.getId());
        assertThat(relationship.getStealthChoiceUserTwo()).isEqualTo(StealthChoice.HIDE);
        assertThat(relationship.getStealthChoiceUserOne()).isEqualTo(StealthChoice.PRECISE);
    }

    @Test
    void shouldReturnListOfActiveFriends() throws Exception {
        //given
        var lon1 = BigDecimal.valueOf(2.5);
        var lat1 = BigDecimal.valueOf(6.5);

        var lon2 = BigDecimal.valueOf(2.5);
        var lat2 = BigDecimal.valueOf(6.5);
        relationshipRepository.save(new Relationship(friendOne, friendTwo));
        relationshipRepository.updateStatus(friendOne.getId(), friendTwo.getId(), ACTIVE);
        userStatusRepository.updateLocation(friendOne.getId(), lon1, lat1, 23);
        userStatusRepository.updateLocation(friendTwo.getId(), lon2, lat2, 23);


        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PATH + "/friend/statuses")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendTwo))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final List<UserStatusResponse> friends = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(friendOne.getId());
    }
}
