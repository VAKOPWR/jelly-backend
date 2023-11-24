package com.vako.application.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_status")
public class UserStatus {

    public UserStatus(User user) {
        this.setUser(user);
    }

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
    private int batteryLevel;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "uid", referencedColumnName = "id")
    @ToString.Exclude
    private User user;

    @Version
    private Long version;

    public UserStatus() {

    }
}
