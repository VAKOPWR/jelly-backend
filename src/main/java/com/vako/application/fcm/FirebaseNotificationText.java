package com.vako.application.fcm;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

public enum FirebaseNotificationText {

    SENT_FRIEND_REQUEST("New friend request!", "%s has sent you a friend request!"),
    ACCEPTED_FRIEND_REQUEST("New friend! Yay!", "%s has accepted your friend request!");

    private final String title;
    private final String message;

    private FirebaseNotificationText(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public Message.Builder getMessageWithParams(String sender) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(String.format(message, sender))
                        .build())
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setNotification(AndroidNotification.builder()
                                        .setClickAction(name())
                                        .build())
                                .build()
                );
    }
}
