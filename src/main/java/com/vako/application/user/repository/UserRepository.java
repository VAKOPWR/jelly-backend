package com.vako.application.user.repository;

import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(final String email);

    Optional<User> findByEmail(final String email);

    @Query("select u from User u where u.email = :identifier or u.nickname = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") final String identifier);

    List<User> findAllByNicknameContainsAndNicknameNotIn(final String nickname, final List<String> exclusions, Pageable pageable);

    List<User> findByGroupUsersIn(List<GroupUser> groupUserNotConnected);
}

