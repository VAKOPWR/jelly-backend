package com.vako.application.dto;

import com.vako.application.message.model.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long messageId;
    private Long senderId;
    private Long groupId;
    private String text;
    private MessageStatus messageStatus;
    private LocalDateTime timeSent;
    private String attachedPhoto;
}
