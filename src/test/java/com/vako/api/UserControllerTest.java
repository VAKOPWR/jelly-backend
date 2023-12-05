package com.vako.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.vako.DbTestBase;
import com.vako.api.user.request.UserStatusUpdateRequest;
import com.vako.api.user.response.BasicUserResponse;
import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import com.vako.application.user.model.UserStatus;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.repository.UserStatusRepository;
import com.vako.util.IDTokenRequest;
import com.vako.util.IDTokenResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.vako.application.user.model.StealthChoice.PRECISE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.LocalDateTime;

@AutoConfigureMockMvc
public class UserControllerTest extends DbTestBase {

    public static final String EMAIL = "oresto101@gmail.com";
    private String idToken;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() throws FirebaseAuthException, IOException, InterruptedException {
        token = FirebaseAuth.getInstance().createCustomToken(UID_1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=AIzaSyAWBlj670EKh5kNYE8-kg7k7cFNYQFfIPQ"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(new IDTokenRequest(token))))
                .build();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        idToken = mapper.readValue(response.body(), IDTokenResponse.class).getIdToken();
    }

    @BeforeEach
    void setUpUser() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(API_PATH + "/user/create")
                .header(HttpHeaders.AUTHORIZATION, idToken))
                .andExpect(status().isOk())
                .andReturn();
    }

    @AfterEach
    void tearDownUser() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser() throws Exception {
        //given

        //when

        //then
        final User user = userRepository.findAll().get(0);
        final UserStatus userStatus = userStatusRepository.findAll().get(0);
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getStealthChoice()).isEqualTo(PRECISE);
        assertThat(user.getUserStatus().getSpeed()).isEqualTo(0.0f);
        assertThat(user.getUserStatus().getBatteryLevel()).isEqualTo(0);
        assertThat(user.getUserStatus().getIsOnline()).isTrue();
        assertThat(userStatus.getSpeed()).isEqualTo(0.0f);
        assertThat(userStatus.getBatteryLevel()).isEqualTo(0);
        assertThat(userStatus.getIsOnline()).isTrue();
    }

    @Test
    void shouldUpdateUserStatus() throws Exception {
        //given
        var lon = BigDecimal.valueOf(2.5);
        var lat = BigDecimal.valueOf(6.5);
        var battery = 32;
        final UserStatusUpdateRequest userStatusUpdateRequest = new UserStatusUpdateRequest(lon, lat, 23, battery);

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put(API_PATH + "/user/status/update")
                .header(HttpHeaders.AUTHORIZATION, idToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userStatusUpdateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final User user = userRepository.findAll().get(0);
        assertThat(user.getUserStatus().getPositionLon()).isEqualTo(lon);
        assertThat(user.getUserStatus().getPositionLat()).isEqualTo(lat);
        assertThat(user.getUserStatus().getBatteryLevel()).isEqualTo(battery);
        assertThat(user.getUserStatus().getSpeed()).isEqualTo(23);
    }

    @Test
    @Disabled
    void shouldUpdateTimestampWhenUpdatingUserStatus() throws Exception {
        //given
        var lon = BigDecimal.valueOf(2.5);
        var lat = BigDecimal.valueOf(6.5);
        var battery = 32;
        final UserStatusUpdateRequest userStatusUpdateRequest = new UserStatusUpdateRequest(lon, lat, 23, battery);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/user/status/update")
                        .header(HttpHeaders.AUTHORIZATION, idToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userStatusUpdateRequest)))
                .andExpect(status().isOk())
                .andReturn();
        final LocalDateTime timestamp_1 = userRepository.findAll().get(0).getUserStatus().getTimestamp();
        Thread.sleep(61_000L);
        mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/user/status/update")
                        .header(HttpHeaders.AUTHORIZATION, idToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userStatusUpdateRequest)))
                .andExpect(status().isOk())
                .andReturn();
        //then
        final LocalDateTime timestamp_2 = userRepository.findAll().get(0).getUserStatus().getTimestamp();
        assertThat(timestamp_1).isNotEqualTo(timestamp_2);
    }

    @Test
    @Disabled
    void shouldStoreAvatarOnAzureAndInBdWhenSavingAvatar() throws Exception {
        //given
        File file = new File("/Users/oresthaman/IdeaProjects/jelly-backend/src/test/resources/images/wp5182992.jpg");
        byte[] content = Files.readAllBytes(file.toPath());
        MockMultipartFile fi = new MockMultipartFile("image", content);
        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_PATH + "/user/avatars")
                        .file(fi)
                        .header(HttpHeaders.AUTHORIZATION, idToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final User user = userRepository.findAll().get(0);
        assertThat(user.getProfilePicture()).startsWith("https://jellyimagestore.blob.core.windows.net/avatars/");
    }

    @Test
    void shouldReturnBasicUserByEmail() throws Exception {
        //given

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(API_PATH + "/user/basic/" + EMAIL)
                .header(HttpHeaders.AUTHORIZATION, idToken))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final BasicUserResponse basicUserResponse = mapper.readValue(result.getResponse().getContentAsString(), BasicUserResponse.class);
        assertThat(basicUserResponse.getNickname()).isEqualTo("oresto101");
        assertThat(basicUserResponse.getProfilePicture()).isNotEmpty();
        assertThat(basicUserResponse.getIsOnline()).isTrue();
    }

    @Test
    void shouldUpdateUserNickname() throws Exception {
        //given
        var nickname = "JOEMAMA";

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/user/nickname/" + nickname)
                        .header(HttpHeaders.AUTHORIZATION, idToken))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final User user = userRepository.findAll().get(0);
        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @Test
    void shouldUpdateUserShakingStatus() throws Exception {
        //given
        var shakingStatus = true;

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/user/shaking/update/" + shakingStatus)
                        .header(HttpHeaders.AUTHORIZATION, idToken))
                        .andExpect(status().isOk())
                        .andReturn();

        //then
        final User user = userRepository.findAll().get(0);
        assertThat(user.getUserStatus().getIsShaking()).isEqualTo(true);
    }

    @Test
    void shouldUpdateUserStealthChoice() throws Exception {
        //given
        var stealthChoice = StealthChoice.HIDE;

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/user/ghost/update/" + stealthChoice)
                        .header(HttpHeaders.AUTHORIZATION, idToken))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final User user = userRepository.findAll().get(0);
        assertThat(user.getStealthChoice()).isEqualTo(StealthChoice.HIDE);
    }

    @Test
    void shouldUpdateUserRegistrationToken() throws Exception {
        //given
        var registrationToken = "abc";

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(API_PATH + "/user/fcm/update/" + registrationToken)
                        .header(HttpHeaders.AUTHORIZATION, idToken))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final User user = userRepository.findAll().get(0);
        assertThat(user.getRegistrationToken()).isEqualTo(registrationToken);
    }


}
