package com.vako.application.location.service;

import com.vako.application.location.model.GroupUsers;
import com.vako.application.location.repository.GroupUsersRepository;
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

    public Optional<GroupUsers> getGroupUsersById(Long uid) {
        return groupUsersRepository.findById(uid);
    }

    public GroupUsers createGroupUsers(GroupUsers groupUsers) {
        return groupUsersRepository.save(groupUsers);
    }

    public GroupUsers updateGroupUsers(Long uid, GroupUsers updatedGroupUsers) throws ChangeSetPersister.NotFoundException {
        Optional<GroupUsers> existingGroupUsers = groupUsersRepository.findById(uid);

        if (existingGroupUsers.isPresent()) {
            updatedGroupUsers.setUid(uid);
            return groupUsersRepository.save(updatedGroupUsers);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    public void deleteGroupUsers(Long uid) {
        groupUsersRepository.deleteById(uid);
    }
}

