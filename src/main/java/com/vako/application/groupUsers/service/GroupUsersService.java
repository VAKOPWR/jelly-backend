package com.vako.application.groupUsers.service;

import com.vako.application.group.repository.GroupRepository;
import com.vako.application.groupUsers.model.GroupUsers;
import com.vako.application.groupUsers.repository.GroupUsersRepository;
import com.vako.application.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GroupUsersService {
    private final GroupUsersRepository groupUsersRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;


    @Autowired
    public GroupUsersService(GroupUsersRepository groupUsersRepository, GroupRepository groupRepository, UserRepository userRepository) {
        this.groupUsersRepository = groupUsersRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public List<GroupUsers> getAllGroupUsers() {
        return groupUsersRepository.findAll();
    }

    public Optional<GroupUsers> getGroupUsersById(Long userId, Long groupId) {
        return Optional.ofNullable(groupUsersRepository.findByUser_IdAndGroup_Id(userId, groupId));
    }

    public void createGroupUser(Long userId, Long groupId) {
        GroupUsers groupUser = new GroupUsers();
        groupUser.setUser(userRepository.getById(userId));
        groupUser.setGroup(groupRepository.getById(groupId));
        groupUser.setMuted(false);
        groupUser.setPinned(false);
        groupUsersRepository.save(groupUser);
    }


    public void deleteGroupUsers(Long userId, Long groupId) {
        groupUsersRepository.deleteByUser_IdAndGroup_Id(userId, groupId);;
    }
}

