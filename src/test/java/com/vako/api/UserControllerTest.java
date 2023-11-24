package com.vako.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.vako.DbTestBase;
import com.vako.application.user.model.User;
import com.vako.application.user.model.UserStatus;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.repository.UserStatusRepository;
import com.vako.util.IDTokenRequest;
import com.vako.util.IDTokenResponse;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@AutoConfigureMockMvc
public class UserControllerTest extends DbTestBase {

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
        token = FirebaseAuth.getInstance().createCustomToken(UID);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=AIzaSyAWBlj670EKh5kNYE8-kg7k7cFNYQFfIPQ"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(new IDTokenRequest(token))))
                .build();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        idToken = mapper.readValue(response.body(), IDTokenResponse.class).getIdToken();
    }

    @Test
    void shouldCreateUser() throws Exception {
        //given

        //when
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(API_PATH + "/user/create")
                .header(HttpHeaders.AUTHORIZATION, idToken)).andReturn();

        //then
        final User user = userRepository.findAll().get(0);
        final UserStatus userStatus = userStatusRepository.findAll().get(0);
        assertThat(user).isEqualTo(null);
        assertThat(userStatus).isEqualTo(null);
    }




}
