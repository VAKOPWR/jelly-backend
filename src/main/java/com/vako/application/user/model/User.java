package com.vako.application.user.model;

import com.vako.application.friend.model.Friendship;
import com.vako.application.groupUsers.model.GroupUsers;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "position_lat")
    private BigDecimal positionLat;

    @Column(name = "position_lon")
    private BigDecimal positionLon;

    @Column(name = "is_shaking")
    private Boolean isShaking;

    @Column(name = "stealth_choice")
    private Integer stealthChoice;

    @Column(name = "profile_picture")
    private String profilePicture;

    @OneToMany(mappedBy = "user")
    private Set<GroupUsers> groupUsers;

    @OneToMany
    private Set<Friendship> friendships;
}
