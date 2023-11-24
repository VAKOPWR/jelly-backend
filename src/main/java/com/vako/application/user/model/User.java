package com.vako.application.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vako.application.relationship.model.Relationship;
import com.vako.application.groupUsers.model.GroupUsers;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@Table(name = "[user]")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "stealth_choice")
    private StealthChoice stealthChoice = StealthChoice.PRECISE;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "registration_token")
    private String registrationToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<GroupUsers> groupUsers;

    @OneToOne(mappedBy = "user")
    private UserStatus userStatus;
}
