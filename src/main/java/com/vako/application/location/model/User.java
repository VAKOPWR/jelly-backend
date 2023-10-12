package com.vako.application.location.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "position_lat", nullable = false)
    private Double positionLat;

    @Column(name = "position_lon", nullable = false)
    private Double positionLon;

    @Column(name = "is_shaking", nullable = false)
    private Boolean isShaking;

    @Column(name = "stealth_choice", nullable = false)
    private Integer stealthChoice;

    @Column(name = "profile_picture", nullable = false)
    private String profilePicture;
}
