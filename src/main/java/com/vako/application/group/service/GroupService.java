package com.vako.application.group.service;

import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.application.dto.ChatUserDTO;
import com.vako.application.dto.NewGroupChatDTO;
import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import com.vako.application.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow();
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group updateGroup(Long id, Group updatedGroup) throws ChangeSetPersister.NotFoundException {
        Optional<Group> existingGroup = groupRepository.findById(id);

        if (existingGroup.isPresent()) {
            updatedGroup.setId(id);
            return groupRepository.save(updatedGroup);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

//    public NewGroupChatDTO createGroup(final CreateGroupChatRequest createGroupChatRequest){
//        Group group = new Group();
//        group.setFriendship(false);
//        group.setName(groupName);
//        group.setDescription(description);
//        groupRepository.save(group);
//        NewGroupChatDTO newGroupChatDTO = new NewGroupChatDTO();
//        newGroupChatDTO.setGroupId(group.getId());
//        List<ChatUserDTO> chatUsers = new ArrayList<>();
//        for (Long userId:userIds) {
//            groupUserService.createGroupUser(userId, group.getId());
//            User user = userRepository.getReferenceById(userId);
//            ChatUserDTO chatUserDTO = new ChatUserDTO(user.getId(), user.getNickname(), user.getProfilePicture());
//            chatUsers.add(chatUserDTO);
//        }
//        newGroupChatDTO.setChatUserDTOS(chatUsers);
//        return newGroupChatDTO;
//    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}
