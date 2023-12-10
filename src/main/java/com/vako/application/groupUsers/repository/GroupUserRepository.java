package com.vako.application.groupUsers.repository;

import com.vako.application.group.model.Group;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    GroupUser findByUser_IdAndGroup_Id(Long userId, Long groupId);

    void deleteByUser_IdAndGroup_Id(Long userId, Long groupId);

    List<GroupUser> findByUser_Id(Long userId);

    @Query("SELECT gu FROM GroupUser gu " +
            "WHERE gu.group IN :groups " +
            "AND gu.user.id <> :userId")
    List<GroupUser> findGroupUsersByGroupsAndUserNotIn(@Param("groups") List<Group> groups, @Param("userId") Long userId);

    @Query("SELECT groupUser " +
            "FROM GroupUser groupUser " +
            "WHERE groupUser.user.id = :userId " +
            "AND groupUser.group.id IN :groupIds")
    List<GroupUser> findGroupUsersByUserIdAndGroupIds(
            @Param("userId") Long userId,
            @Param("groupIds") List<Long> groupIds);



    List<GroupUser> findByUser(User referenceById);
}

