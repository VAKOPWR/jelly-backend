package com.vako.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewGroupChatDTO {
    long groupId;
    List<ChatUserDTO> chatUserDTOS;
}
