package com.vako.api.admin;

import com.vako.application.fcm.FirebaseCloudMessagingService;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.vako.application.fcm.FirebaseNotificationText.ACCEPTED_FRIEND_REQUEST;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminTestController {

    private final FirebaseCloudMessagingService firebaseCloudMessagingService;

    private final UserService userService;

    @PostMapping("/fcm/{id}")
    void sendNotificationToUser(@PathVariable("id") Long id) {
        final User user = userService.getUserById(id);
        firebaseCloudMessagingService.sendMessage(ACCEPTED_FRIEND_REQUEST.getMessageWithParams(user.getNickname()), user.getRegistrationToken());
    }

}
