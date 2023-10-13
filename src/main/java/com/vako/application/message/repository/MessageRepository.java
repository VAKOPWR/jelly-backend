package com.vako.application.message.repository;

import com.vako.application.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByGroup_Id(Long groupId);

    void deleteByUserIdAndGroupId(Long senderId, Long groupId);
}

