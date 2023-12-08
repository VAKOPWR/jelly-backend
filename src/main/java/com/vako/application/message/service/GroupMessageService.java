package com.vako.application.message.service;

import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.dto.ChatUserDTO;
import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.dto.NewGroupChatDTO;
import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import com.vako.application.group.service.GroupService;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.repository.GroupUserRepository;
import com.vako.application.groupUsers.service.GroupUserService;
import com.vako.application.message.mapper.MessageMapper;
import com.vako.application.message.model.Message;
import com.vako.application.message.model.MessageStatus;
import com.vako.application.message.repository.MessageRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupMessageService {

    private final MessageMapper messageMapper;
    private final GroupService groupService;
    private final GroupUserService groupUserService;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public List<GroupMessageDTO> getChats (String email){
        List<Group> groups = groupService.getCompleteGroupsByUserId(userService.getUserByEmail(email).getId());
        return groups.stream().map(messageMapper::groupToMessageDTO).toList();
    }

    public List<MessageDTO> loadMessagesPaged(Long groupId, int page){
        Pageable pageable = PageRequest.of(page, 40);
        Page<Message> messagePage = messageRepository.findMessageByGroup(groupId, pageable);
        return messagePage.stream().map(messageMapper::messageToMessageDTO).toList();
    }

    public List<MessageDTO> loadMessagesNew(LocalDateTime timeSent, List<Long> groupIds){
        List<Message> messages = messageRepository.findMessagesAfterTimeInGroups(timeSent, groupIds);
        return messages.stream().map(messageMapper::messageToMessageDTO).toList();
    }

    public void createMessage(final String email, final CreateMessageRequest createMessageRequest) {
        final User user = userService.getUserByEmail(email);
        final Group group = groupService.getGroupById(createMessageRequest.getGroupId());
        final Message message = messageMapper.createMessageRequestToMessage(createMessageRequest, LocalDateTime.now(), user, group);
        messageRepository.save(message);
    }

    public void createPersonalChat(Long userId1, Long userId2) {
        Group group = groupService.createGroup(new Group(true));
        groupService.createGroupUser(userId1, group.getId());
        groupService.createGroupUser(userId2, group.getId());
    }
}
