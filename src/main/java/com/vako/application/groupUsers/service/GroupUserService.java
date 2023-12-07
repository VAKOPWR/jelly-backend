package com.vako.application.groupUsers.service;

import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import com.vako.application.group.service.GroupService;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.repository.GroupUserRepository;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupUserService {
    private final GroupUserRepository groupUserRepository;
    private final GroupService groupService;
    private final UserService userService;



    public List<GroupUser> getAllGroupUsers() {
        return groupUserRepository.findAll();
    }

    public Optional<GroupUser> getGroupUsersById(Long userId, Long groupId) {
        return Optional.ofNullable(groupUserRepository.findByUser_IdAndGroup_Id(userId, groupId));
    }


    public void createGroupUser(Long userId, Long groupId) {
        groupUserRepository.save(new GroupUser(userService.getUserById(userId), groupService.getGroupById(groupId)));
    }


    public void deleteGroupUsers(Long userId, Long groupId) {
        groupUserRepository.deleteByUser_IdAndGroup_Id(userId, groupId);;
    }


}

