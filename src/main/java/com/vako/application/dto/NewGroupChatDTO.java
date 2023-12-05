package com.vako.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class NewGroupChatDTO {
    long groupId;
    List<ChatUserDTO> chatUserDTOS;
}
