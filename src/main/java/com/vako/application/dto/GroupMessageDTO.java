package com.vako.application.dto;

import com.vako.application.message.model.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupMessageDTO {

    private Long groupId;
    private String groupName;
    private Message message;
    private boolean isPinned;
    private boolean isMuted;
    private String picture;
    private boolean isFriendship;

}
