package com.vako.application.groupUsers.service;

import com.vako.application.groupUsers.model.GroupUsers;
import com.vako.application.groupUsers.repository.GroupUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GroupUsersService {
    private final GroupUsersRepository groupUsersRepository;

    @Autowired
    public GroupUsersService(GroupUsersRepository groupUsersRepository) {
        this.groupUsersRepository = groupUsersRepository;
    }

    public List<GroupUsers> getAllGroupUsers() {
        return groupUsersRepository.findAll();
    }

    public Optional<GroupUsers> getGroupUsersById(Long userId, Long groupId) {
        return Optional.ofNullable(groupUsersRepository.findByUser_IdAndGroup_Id(userId, groupId));
    }

    public GroupUsers createGroupUsers(GroupUsers groupUsers) {
        return groupUsersRepository.save(groupUsers);
    }

    public GroupUsers updateStealthChoiceByUserAndGroup(Long userId, Long groupId, Integer newStealthChoice) throws ChangeSetPersister.NotFoundException {
        GroupUsers groupUsers = groupUsersRepository.findByUser_IdAndGroup_Id(userId, groupId);

        if (groupUsers != null) {
            groupUsers.setStealthChoice(newStealthChoice);
            return groupUsersRepository.save(groupUsers);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    public void deleteGroupUsers(Long userId, Long groupId) {
        groupUsersRepository.deleteByUser_IdAndGroup_Id(userId, groupId);;
    }
}

