package com.vako.application.group.repository;

import com.vako.application.group.model.Group;
import com.vako.application.groupUsers.model.GroupUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByGroupUsersIn(List<GroupUser> groupUserConnected);

    @Query("select gr from Group gr " +
            "left join fetch GroupUser gru on gru.group.id = gr.id " +
            "left join fetch User u on u.id = gru.user.id " +
            "left join fetch Message m on gr.id = m.group.id ")
    List<Group> findCompleteGroupsByUserId(@Param("userId") final Long userId);

    @Modifying
    @Transactional
    @Query("update Group g set g.groupPicture = :imageUrl where g.id = :groupId")
    void updateImageUrl(@Param("groupId") final Long groupId, @Param("imageUrl") final String imageUrl);

    @Query(value = "SELECT g1.id " +
            "   FROM GroupUser gu1 " +
            "   JOIN GroupUser gu2 ON gu1.group.id = gu2.group.id " +
            "   JOIN Group g1 ON gu1.group.id = g1.id " +
            "   WHERE gu1.user.id = :userId1 AND gu2.user.id = :userId2 " +
            "     AND g1.isFriendship = true")
    Integer findGroupByUserIds(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Message g " +
            "WHERE g.group.id = :gid")
    void deleteMessagesByGroupId(
            @Param("gid") Integer groupId);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM GroupUser g " +
            "WHERE g.group.id = :gid")
    void deleteGroupUsersByGroupId(
            @Param("gid") Integer groupId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Group g " +
            "WHERE g.id = :gid")
    void deleteFriendshipGroupByGroupId(
            @Param("gid") Integer groupId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Group g " +
            "WHERE g.id = (" +
            "   SELECT g1.id " +
            "   FROM GroupUser gu1 " +
            "   JOIN GroupUser gu2 ON gu1.group.id = gu2.group.id " +
            "   JOIN Group g1 ON gu1.group.id = g1.id " +
            "   WHERE gu1.user.id = :userId1 AND gu2.user.id = :userId2 " +
            "     AND g1.isFriendship = true" +
            ")")
    void deleteFriendshipGroup(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    @Query("select gr from Group gr " +
            "left join fetch GroupUser gru on gru.group.id = gr.id " +
            "left join fetch User u on u.id = gru.user.id " +
            "left join fetch Message m on gr.id = m.group.id " +
            "where gr.id = :groupId")
    Optional<Group> findCompleteGroupById(@Param("groupId") final Long groupId);
}

