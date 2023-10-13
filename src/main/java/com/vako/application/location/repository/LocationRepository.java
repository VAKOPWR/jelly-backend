//package com.vako.application.location.repository;
//
//import com.vako.application.location.model.Location;
//import jakarta.transaction.Transactional;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//@Repository
//public interface LocationRepository extends JpaRepository<Location, Long> {
//
//    boolean existsByUserId(final String userId);
//    @Modifying
//    @Transactional
//    @Query("update Location l set l.longitude = :longitude, l.latitude = :latitude WHERE l.userId = :userId")
//    void updateUserLocation(@Param("longitude") BigDecimal longitude, @Param("latitude") BigDecimal latitude, @Param("userId") String userId);
//
//}
