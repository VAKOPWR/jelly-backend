package com.vako.application.location.controller;

import com.vako.application.location.model.GroupUsers;
import com.vako.application.location.service.GroupUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/group-users")
public class GroupUsersController {
    private final GroupUsersService groupUsersService;

    @Autowired
    public GroupUsersController(GroupUsersService groupUsersService) {
        this.groupUsersService = groupUsersService;
    }

    @GetMapping
    public List<GroupUsers> getAllGroupUsers() {
        return groupUsersService.getAllGroupUsers();
    }

    @GetMapping("/{uid}")
    public Optional<GroupUsers> getGroupUsersById(@PathVariable Long uid) {
        return groupUsersService.getGroupUsersById(uid);
    }

    @PostMapping
    public GroupUsers createGroupUsers(@RequestBody GroupUsers groupUsers) {
        return groupUsersService.createGroupUsers(groupUsers);
    }

    @PutMapping("/{uid}")
    public GroupUsers updateGroupUsers(@PathVariable Long uid, @RequestBody GroupUsers updatedGroupUsers) throws ChangeSetPersister.NotFoundException {
        return groupUsersService.updateGroupUsers(uid, updatedGroupUsers);
    }

    @DeleteMapping("/{uid}")
    public void deleteGroupUsers(@PathVariable Long uid) {
        groupUsersService.deleteGroupUsers(uid);
    }
}
