package com.vako.application.message.repository;

import com.vako.application.group.model.Group;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.message.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByGroup_Id(Long groupId);

    void deleteByUserIdAndGroupId(Long senderId, Long groupId);


    @Query("SELECT DISTINCT m1 FROM Message m1 " +
            "WHERE m1.timeSent = (SELECT MAX(m2.timeSent) FROM Message m2 WHERE m2.group = m1.group) " +
            "AND m1.group IN :groups")
    List<Message> findTopByGroupInOrderByTimeSentDesc(@Param("groups") List<Group> groups);

    @Query("SELECT m FROM Message m WHERE m.group.id = :groupId ORDER BY m.timeSent DESC")
    Page<Message> findMessageByGroup(@Param("groupId") Long groupId, Pageable pageable);

    @Query("SELECT message " +
            "FROM Message message " +
            "WHERE message.group.id IN :groupIds " +
            "AND message.timeSent > (" +
            "   SELECT groupUser.lastChecked " +
            "   FROM GroupUser groupUser " +
            "   WHERE groupUser.group.id = message.group.id " +
            ") " +
            "ORDER BY message.group.id, message.timeSent ASC")
    List<Message> findMessagesAfterTimeInGroups(
            @Param("groupIds") List<Long> groupIds);



    @Modifying
    @Transactional
    @Query("update Message m set m.attachedPhoto = :imageUrl where m.id = :messageId")
    void updateImageUrl(@Param("messageId") final Long messageId, @Param("imageUrl") final String imageUrl);


}


