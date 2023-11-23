package com.vako.application.user.repository;

import com.vako.application.user.model.UserStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("update UserStatus us set us.positionLon = :longitude, us.positionLat = :latitude, us.speed = :speed WHERE us.user.id = :id ")
    int updateLocation(@Param("id") Long id, @Param("longitude") BigDecimal longitude, @Param("latitude") BigDecimal latitude, @Param("speed") float speed);

}
