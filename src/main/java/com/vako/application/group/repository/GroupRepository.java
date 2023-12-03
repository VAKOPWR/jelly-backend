package com.vako.application.group.repository;

import com.vako.application.group.model.Group;
import com.vako.application.groupUsers.model.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByGroupUsersIn(List<GroupUser> groupUserConnected);
}

