package com.vako.application.group.service;

import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
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

    public Optional<Group> getGroupById(Long id) {
        return groupRepository.findById(id);
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

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}
