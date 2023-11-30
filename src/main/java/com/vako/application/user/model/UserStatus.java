package com.vako.application.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    @PrePersist
    public void onInsert() {
        timestamp = LocalDateTime.now();
    }

    public String getLastOnline() {
        ChronoUnit[] units = {ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES};
        for (ChronoUnit unit : units) {
            long difference = unit.between(timestamp, LocalDateTime.now());
            if (difference != 0) {
                return "Last online " + -difference + " " + unit.toString().toLowerCase() + " ago";
            }
        }
        return "Online";
    }
}
