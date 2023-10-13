package com.vako.application.user.model;

import com.vako.application.groupUsers.model.GroupUsers;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "position_lat")
    private Double positionLat;

    @Column(name = "position_lon")
    private Double positionLon;

    @Column(name = "is_shaking", nullable = false)
    private Boolean isShaking;

    @Column(name = "stealth_choice", nullable = false)
    private Integer stealthChoice;

    @Column(name = "profile_picture", nullable = false)
    private String profilePicture;

    @OneToMany(mappedBy = "user")
    private Set<GroupUsers> groupUsers;
}
