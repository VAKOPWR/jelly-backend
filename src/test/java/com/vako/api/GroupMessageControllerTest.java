package com.vako.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.vako.DbTestBase;
import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.dto.NewGroupChatDTO;
import com.vako.application.fcm.FirebaseCloudMessagingService;
import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import com.vako.application.group.service.GroupService;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.repository.GroupUserRepository;
import com.vako.application.message.model.Message;
import com.vako.application.message.repository.MessageRepository;
import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.repository.RelationshipRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import com.vako.util.IDTokenRequest;
import com.vako.util.IDTokenResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.vako.application.relationship.model.RelationshipStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class GroupMessageControllerTest extends DbTestBase {
    private String idTokenFriendOne;
    private String idTokenFriendTwo;

    private User friendOne;

    private User friendTwo;

    @MockBean
    private FirebaseCloudMessagingService firebaseMessaging;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupUserRepository groupUserRepository;

    @Autowired
    private MessageRepository messageRepository;

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
        Mockito.when(firebaseMessaging.sendMessage(any(), any())).thenReturn("");
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
        groupRepository.deleteAll();
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
    void shouldCreateNewMessageInPersonalChatBetweenTwoFriends() throws Exception {
        //given
        relationshipRepository.save(new Relationship(friendOne, friendTwo));
        CreateGroupChatRequest createGroupChatRequest = new CreateGroupChatRequest("TestGroup", "Descr", List.of(friendOne.getId(), friendTwo.getId()));
        NewGroupChatDTO newGroupChat = groupService.createGroup(createGroupChatRequest);
        final CreateMessageRequest createMessageRequest = new CreateMessageRequest(friendOne.getId(), groupService.getGroupById(newGroupChat.getGroupId()).getId(), "test");

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post(API_PATH + "/chats/message")
                        .header(HttpHeaders.AUTHORIZATION, idTokenFriendOne)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(createMessageRequest)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final Group group = groupRepository.findAll().get(0);
        final List<GroupUser> groupUsers = groupUserRepository.findAll();
        final Message message = messageRepository.findAll().get(0);
        assertThat(group.getId()).isEqualTo(newGroupChat.getGroupId());
        assertThat(group.getName()).isEqualTo(createGroupChatRequest.getName());
        assertThat(group.getDescription()).isEqualTo(createGroupChatRequest.getDescription());
        assertThat(groupUsers.stream().map(groupUser -> groupUser.getUser().getId()).toList()).isEqualTo(createGroupChatRequest.getUserIds());
        assertThat(message.getGroup()).isEqualTo(group);
        assertThat(message.getUser()).isEqualTo(friendOne);
        assertThat(message.getText()).isEqualTo(createMessageRequest.getText());
    }
}
