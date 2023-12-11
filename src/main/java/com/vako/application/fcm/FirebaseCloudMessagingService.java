package com.vako.application.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class FirebaseCloudMessagingService {

    private final FirebaseMessaging firebaseMessagingService;

    public String sendMessage(final Message.Builder messageBuilder, final String registrationToken) {
        final Message message = messageBuilder.setToken(registrationToken).build();
        try {
            return firebaseMessagingService.send(message);
        }
        catch (Exception ex) {
            log.error("Error sending message: {}", ex.getMessage());
        }
        return "";
    }

}
