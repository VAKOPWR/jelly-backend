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

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByGroupUsersIn(List<GroupUser> groupUserConnected);

    @Query("select gr from Group gr " +
            "left join fetch GroupUser gru on gru.user.id = :userId " +
            "left join fetch User u on u.id = gru.user.id " +
            "left join fetch Message m on gr.id = m.group.id")
    List<Group> findCompleteGroupsByUserId(@Param("userId") final Long userId);

    @Modifying
    @Transactional
    @Query("update Group g set g.groupPicture = :imageUrl where g.id = :groupId")
    void updateImageUrl(@Param("groupId") final Long groupId, @Param("imageUrl") final String imageUrl);
}

