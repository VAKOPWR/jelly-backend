package com.vako.api.groupUsers.controller;

import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.service.GroupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/group-users")
public class GroupUsersController {
    private final GroupUserService groupUserService;

    @Autowired
    public GroupUsersController(GroupUserService groupUserService) {
        this.groupUserService = groupUserService;
    }

    @GetMapping
    public List<GroupUser> getAllGroupUsers() {
        return groupUserService.getAllGroupUsers();
    }

    @GetMapping("/{userId}/{groupId}")
    public Optional<GroupUser> getGroupUsersById(@PathVariable Long userId,
                                                 @PathVariable Long groupId) {
        return groupUserService.getGroupUsersById(userId, groupId);
    }

    @DeleteMapping("/{userId}/{groupId}")
    public void deleteGroupUsers(@PathVariable Long userId,
                                 @PathVariable Long groupId) {
        groupUserService.deleteGroupUsers(userId, groupId);
    }
}
