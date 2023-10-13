package com.vako.application.groupUsers.repository;

import com.vako.application.groupUsers.model.GroupUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupUsersRepository extends JpaRepository<GroupUsers, Long> {
    GroupUsers findByUser_IdAndGroup_Id(Long userId, Long groupId);

    void deleteByUser_IdAndGroup_Id(Long userId, Long groupId);
}

