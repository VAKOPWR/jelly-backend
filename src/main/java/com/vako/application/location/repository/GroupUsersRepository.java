package com.vako.application.location.repository;

import com.vako.application.location.model.GroupUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupUsersRepository extends JpaRepository<GroupUsers, Long> {
}

