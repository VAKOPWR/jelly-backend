package com.vako.application.user_status.model;

import com.vako.application.relationship.model.Relationship;
import com.vako.application.groupUsers.model.GroupUsers;
import com.vako.application.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "user_status")
public class UserStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position_lat")
    private BigDecimal positionLat;

    @Column(name = "position_lon")
    private BigDecimal positionLon;

    @Column(name = "speed")
    private float speed;

    @Column(name = "is_shaking")
    private Boolean isShaking = false;

    @Column(name = "battery_level")
    private int battery_level;

    @Column(name = "is_online")
    private boolean isOnline = false;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Version
    private Long version;
}
