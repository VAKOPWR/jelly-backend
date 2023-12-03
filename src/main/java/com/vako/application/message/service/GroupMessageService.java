package com.vako.application.message.service;

import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.repository.GroupUserRepository;
import com.vako.application.message.model.Message;
import com.vako.application.message.repository.MessageRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupMessageService {

    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public GroupMessageService(GroupRepository groupRepository, GroupUserRepository groupUserRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<GroupMessageDTO> getChats (Long userId){
        List<GroupUser> groupUserConnected = groupUserRepository.findByUser(userRepository.getReferenceById(userId));
        List<Group> groups = groupRepository.findByGroupUsersIn(groupUserConnected);
        List<Message> lastMessages = messageRepository.findTopByGroupInOrderByTimeSentDesc(groups);
        List<GroupUser> groupUserNotConnected = groupUserRepository.findGroupUsersByGroupsAndUserNotIn(groups, userId);
        List<User> friends = userRepository.findByGroupUsersIn(groupUserNotConnected);
        
        List<GroupMessageDTO> chats = new ArrayList<>();
        
        for (Group group: groups) {
            GroupMessageDTO chat = new GroupMessageDTO();
            chat.setGroupId(group.getId());
            chat.setFriendship(group.isFriendship());

            for (GroupUser groupuser: groupUserConnected) {
                if (groupuser.getGroup() == group){
                    chat.setMuted(groupuser.isMuted());
                    chat.setPinned(groupuser.isPinned());
                    break;
                }
            }

            for (Message message:lastMessages){
                if (message.getGroup() == group){
                    chat.setMessage(message);
                    break;
                }
            }
            if (!group.isFriendship()){
                chat.setGroupName(group.getName());
                chat.setPicture(group.getGroupPicture());
            }
            else {
                for (GroupUser groupUser:groupUserNotConnected) {
                    if (groupUser.getGroup() == group){
                        for (User friend:friends) {
                            if (groupUser.getUser() == friend){
                                chat.setGroupName(friend.getNickname());
                                chat.setPicture(friend.getProfilePicture());
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            chats.add(chat);
        }

        return chats;
    }

    public Page<Message> loadMessages (Long groupId, int page){
        Pageable pageable = PageRequest.of(page, 40);
        Group group = groupRepository.getReferenceById(groupId);
        return messageRepository.findMessageByGroup(group, pageable);
    }
}
