package com.vako.application.groupUsers.repository;

import com.vako.application.group.model.Group;
import com.vako.application.groupUsers.model.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    GroupUser findByUser_IdAndGroup_Id(Long userId, Long groupId);

    void deleteByUser_IdAndGroup_Id(Long userId, Long groupId);

    List<GroupUser> findByUserId(Long userId);

    @Query("SELECT gu FROM GroupUser gu " +
            "WHERE gu.group IN :groups " +
            "AND gu.group.isFriendship = true " +
            "AND gu.user.id <> :userId")
    List<GroupUser> findGroupUsersByGroupsAndUserNotIn(@Param("groups") List<Group> groups, @Param("userId") Long userId);


}

