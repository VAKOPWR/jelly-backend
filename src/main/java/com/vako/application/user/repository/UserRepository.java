package com.vako.application.user.repository;

import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("update User u set u.stealthChoice = :stealthChoice WHERE u.id = :id ")
    int updateStealthChoice(@Param("id") Long id, @Param("stealthChoice") StealthChoice stealthChoice);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("update User u set u.registrationToken = :registrationToken WHERE u.email = :email ")
    int updateRegistrationToken(@Param("email") String email, @Param("registrationToken") String registrationToken);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("update User u set u.profilePicture = :profilePicture WHERE u.email = :email ")
    int updateAvatarId(@Param("email") String email, @Param("profilePicture") String pictureId);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("update User u set u.nickname = :nickname WHERE u.email = :email ")
    int updateNickname(@Param("email") String email, @Param("nickname") String nickname);

    List<User> findAllByNicknameContainsAndNicknameNotIn(final String nickname, final List<String> exclusions, Pageable pageable);

    List<User> findByGroupUsersIn(List<GroupUser> groupUserNotConnected);
}

