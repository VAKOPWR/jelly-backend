package com.vako.api.groupUsers.controller;

import com.vako.application.groupUsers.model.GroupUsers;
import com.vako.application.groupUsers.service.GroupUsersService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{userId}/{groupId}")
    public Optional<GroupUsers> getGroupUsersById(@PathVariable Long userId,
                                                  @PathVariable Long groupId) {
        return groupUsersService.getGroupUsersById(userId, groupId);
    }

    @DeleteMapping("/{userId}/{groupId}")
    public void deleteGroupUsers(@PathVariable Long userId,
                                 @PathVariable Long groupId) {
        groupUsersService.deleteGroupUsers(userId, groupId);
    }
}
