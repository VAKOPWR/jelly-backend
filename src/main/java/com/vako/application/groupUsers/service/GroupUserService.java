package com.vako.application.groupUsers.service;

import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.repository.GroupUserRepository;
import com.vako.application.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GroupUserService {
    private final GroupUserRepository groupUserRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;


    @Autowired
    public GroupUserService(GroupUserRepository groupUserRepository, GroupRepository groupRepository, UserRepository userRepository) {
        this.groupUserRepository = groupUserRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public List<GroupUser> getAllGroupUsers() {
        return groupUserRepository.findAll();
    }

    public Optional<GroupUser> getGroupUsersById(Long userId, Long groupId) {
        return Optional.ofNullable(groupUserRepository.findByUser_IdAndGroup_Id(userId, groupId));
    }

    public void createGroupUser(Long userId, Long groupId) {
        GroupUser groupUser = new GroupUser();
        groupUser.setUser(userRepository.getById(userId));
        groupUser.setGroup(groupRepository.getById(groupId));
        groupUser.setMuted(false);
        groupUser.setPinned(false);
        groupUserRepository.save(groupUser);
    }

    //TODO: use this when adding a new friend
    public void createPersonalChat(Long userId1, Long userId2) {
        Group group = new Group();
        group.setFriendship(true);
        groupRepository.save(group);
        createGroupUser(userId1, group.getId());
        createGroupUser(userId2, group.getId());
    }

    public void createGroup(List<Long> userIds, String groupName){
        Group group = new Group();
        group.setFriendship(false);
        group.setName(groupName);
        groupRepository.save(group);
        for (Long userId:userIds) {
            createGroupUser(userId, group.getId());
        }
    }

    public void deleteGroupUsers(Long userId, Long groupId) {
        groupUserRepository.deleteByUser_IdAndGroup_Id(userId, groupId);;
    }
}

