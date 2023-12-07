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

//    public List<GroupMessageDTO> getChats (String email){
//        Optional<User> tempUser1 = userRepository.findByEmail(email);
//        Long userId = tempUser1.get().getId();
//        List<GroupUser> groupUserConnected = groupUserRepository.findByUser(userRepository.getReferenceById(userId));
//        List<Group> groups = groupRepository.findByGroupUsersIn(groupUserConnected);
//        List<Message> lastMessages = messageRepository.findTopByGroupInOrderByTimeSentDesc(groups);
//        List<GroupUser> groupUserNotConnected = groupUserRepository.findGroupUsersByGroupsAndUserNotIn(groups, userId);
//        List<User> chatUsers = userRepository.findByGroupUsersIn(groupUserNotConnected);
//        List<GroupMessageDTO> chats = new ArrayList<>();
//        for (Group group: groups) {
//            GroupMessageDTO chat = new GroupMessageDTO();
//            chat.setGroupId(group.getId());
//            chat.setFriendship(group.isFriendship());
//            for (GroupUser groupuser: groupUserConnected) {
//                if (groupuser.getGroup() == group){
//                    chat.setMuted(groupuser.isMuted());
//                    chat.setPinned(groupuser.isPinned());
//                    break;
//                }
//            }
//            for (Message message:lastMessages){
//                if (message.getGroup() == group){
//                    chat.setLastMessageText(message.getText());
//                    chat.setLastMessageMessagesStatus(message.getMessageStatus());
//                    chat.setLastMessageSenderId(message.getUser().getId());
//                    chat.setLastMessageTimeSent(message.getTimeSent());
//                    chat.setLastMessageAttachedPhoto(message.getAttachedPhoto());
//                    break;
//                }
//            }
//            if (!group.isFriendship()){
//                chat.setGroupName(group.getName());
//                chat.setPicture(group.getGroupPicture());
//                chat.setDescription(group.getDescription());
//                List<ChatUserDTO> chatUserDTOS = new ArrayList<>();
//                for (GroupUser groupUser:groupUserNotConnected){
//                    if (groupUser.getGroup() == group){
//                        for (User chatUser:chatUsers){
//                            User tempUser = groupUser.getUser();
//                            if (tempUser == chatUser){
//                                chatUserDTOS.add(new ChatUserDTO(tempUser.getId(), tempUser.getNickname(), tempUser.getProfilePicture()));
//                                break;
//                            }
//                        }
//                        break;
//                    }
//                }
//                chat.setGroupUsers(chatUserDTOS);
//            }
//            else {
//                for (GroupUser groupUser:groupUserNotConnected) {
//                    if (groupUser.getGroup() == group){
//                        for (User friend:chatUsers) {
//                            if (groupUser.getUser() == friend){
//                                chat.setGroupName(friend.getNickname());
//                                chat.setPicture(friend.getProfilePicture());
//                                chat.setFriendId(friend.getId());
//                                break;
//                            }
//                        }
//                        break;
//                    }
//                }
//            }
//            chats.add(chat);
//        }
//        return chats;
//    }

    public List<MessageDTO> loadMessagesPaged(Long groupId, int page){
        Pageable pageable = PageRequest.of(page, 40);
        Page<Message> messagePage = messageRepository.findMessageByGroup(groupId, pageable);
        return messagePage.stream().map(messageMapper::messageToMessageDTO).toList();
    }

//    public List<MessageDTO> loadMessagesNew(LocalDateTime timeSent, List<Long> groupIds){
//        List<Message> messages = messageRepository.findMessagesAfterTimeInGroups(timeSent, groupIds);
//        List<MessageDTO> messageDTOS = new ArrayList<MessageDTO>();
//        for (Message message:messages){
//            messageDTOS.add(new MessageDTO(message.getUser().getId(), message.getGroup().getId(), message.getText(), message.getMessageStatus(), message.getTimeSent(), message.getAttachedPhoto()));
//        }
//        return messageDTOS;
//    }

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
