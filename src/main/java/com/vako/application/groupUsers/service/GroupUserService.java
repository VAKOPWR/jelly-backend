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


    public void deleteGroupUsers(Long userId, Long groupId) {
        groupUserRepository.deleteByUser_IdAndGroup_Id(userId, groupId);;
    }


}

