package com.vako.application.user.repository;

import com.vako.application.user.model.UserStatus;
import jakarta.transaction.Transactional;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

    @Modifying
    @Transactional
    @Query("update UserStatus us set us.positionLon = :longitude, us.positionLat = :latitude, us.speed = :speed, us.batteryLevel = :batteryLevel, " +
            "us.timestamp = :time"+
            " WHERE us.user.id = :id ")
    int updateLocation(@Param("id") Long id,
                       @Param("longitude") BigDecimal longitude,
                       @Param("latitude") BigDecimal latitude,
                       @Param("speed") float speed,
                       @Param("batteryLevel") int batteryLevel,
                       @Param("time")LocalDateTime localDateTime);

    @Query("SELECT us FROM UserStatus us WHERE us.isShaking = true and us.user.id != :id ")
    List<UserStatus> findAllUsersWhoAreShaking(@Param("id") Long id);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("update UserStatus us set us.isShaking = :isShaking WHERE us.user.id = :id ")
    int updateIsShaking(@Param("id") Long id, @Param("isShaking") Boolean isShaking);

}
