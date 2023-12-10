package com.vako.application.message.mapper;

import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.dto.ChatUserDTO;
import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.group.model.Group;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.message.model.Message;
import com.vako.application.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper
public interface MessageMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "group", target = "group")
    @Mapping(source = "timeSent", target = "timeSent")
    @Mapping(target = "id", ignore = true)
    Message createMessageRequestToMessage(final CreateMessageRequest createMessageRequest,
                                          final LocalDateTime timeSent,
                                          final User user,
                                          final Group group);

    @Mapping(source = "user.id", target = "senderId")
    @Mapping(source = "group.id", target = "groupId")
    MessageDTO messageToMessageDTO(Message message);

    @Mapping(source = "id", target = "groupId")
    @Mapping(source = "groupPicture", target = "picture")
    @Mapping(source = "lastSentMessage.id", target = "lastMessageSenderId")
    @Mapping(source = "lastSentMessage.text", target = "lastMessageText")
    @Mapping(source = "lastSentMessage.timeSent", target = "lastMessageTimeSent")
    @Mapping(source = "lastSentMessage.messageStatus", target = "lastMessageMessagesStatus")
    @Mapping(source = "lastSentMessage.attachedPhoto", target = "lastMessageAttachedPhoto")
    @Mapping(expression = "java(userListToChatUserDTOList(group.getGroupUsers()))", target = "groupUsers")
    GroupMessageDTO groupToMessageDTO(Group group);

    @Mapping(source = "profilePicture", target = "picture")
    ChatUserDTO userToChatUserDTO(User user);

    default List<ChatUserDTO> userListToChatUserDTOList(Set<GroupUser> groupUserList) {
        return groupUserList.stream().map(groupUser -> userToChatUserDTO(groupUser.getUser())).toList();
    }

}
