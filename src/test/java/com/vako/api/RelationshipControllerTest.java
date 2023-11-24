package com.vako.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.vako.DbTestBase;
import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.repository.RelationshipRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.repository.UserStatusRepository;
import com.vako.util.IDTokenRequest;
import com.vako.util.IDTokenResponse;
import jakarta.transaction.Transactional;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.vako.application.relationship.model.RelationshipStatus.ACTIVE;
import static com.vako.application.relationship.model.RelationshipStatus.PENDING;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
public class RelationshipControllerTest extends DbTestBase {

    public static final String EMAIL = "oresto101@gmail.com";
    private String idTokenFriendOne;
    private String idTokenFriendTwo;

    private User friendOne;

    private User friendTwo;

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
}
