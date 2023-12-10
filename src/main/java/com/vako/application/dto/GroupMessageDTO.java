package com.vako.application.dto;

import com.vako.application.message.model.MessageStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GroupMessageDTO {

    private Long groupId;
    private String groupName;

    private boolean isPinned;
    private boolean isMuted;

    private String picture;
    private String description;

    private boolean isFriendship;
    private Long friendId;
    private Long lastMessageSenderId;
    private String lastMessageText;
    private LocalDateTime lastMessageTimeSent;
    private MessageStatus lastMessageMessagesStatus;
    private String lastMessageAttachedPhoto;
    private List<ChatUserDTO> groupUsers;
}
