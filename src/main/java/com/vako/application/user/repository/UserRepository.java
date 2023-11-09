package com.vako.application.user.repository;

import com.vako.application.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(final String email);

    Optional<User> findByEmail(final String email);

    @Query("select u from User u where u.email = :identifier or u.nickname = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") final String identifier);

    @Modifying
    @Transactional
    @Query("update User u set u.positionLon = :longitude, u.positionLat = :latitude WHERE u.nickname = :nickname")
    void updateUserLocation(@Param("longitude") BigDecimal longitude, @Param("latitude") BigDecimal latitude, @Param("nickname") String nickname);

}

