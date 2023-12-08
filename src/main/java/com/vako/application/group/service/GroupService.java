package com.vako.application.group.service;

import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.application.dto.ChatUserDTO;
import com.vako.application.dto.NewGroupChatDTO;
import com.vako.application.group.mapper.GroupMapper;
import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.repository.GroupUserRepository;
import com.vako.application.groupUsers.service.GroupUserService;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupMapper groupMapper;
    private final GroupUserRepository groupUserRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow();
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public void createGroupUser(Long userId, Long groupId) {
        groupUserRepository.save(new GroupUser(userService.getUserById(userId), getGroupById(groupId)));
    }

    public NewGroupChatDTO createGroup(final CreateGroupChatRequest createGroupChatRequest){
        final Group group = groupRepository.save(groupMapper.createGroupChatRequestToGroup(createGroupChatRequest));
        final List<ChatUserDTO> chatUserDTOS = createGroupChatRequest.getUserIds().stream().map(userId -> {
            createGroupUser(userId, group.getId());
            User user = userService.getUserById(userId);
            return new ChatUserDTO(user.getId(), user.getNickname(), user.getProfilePicture());
        }).toList();
        return new NewGroupChatDTO(group.getId(), chatUserDTOS);
    }

    public List<Group> getCompleteGroupsByUserId(final Long userId) {
        return groupRepository.findCompleteGroupsByUserId(userId);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}
