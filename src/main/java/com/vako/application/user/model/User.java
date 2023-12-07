package com.vako.application.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.relationship.model.Relationship;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Entity
@Data
@Table(name = "[user]")
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "stealth_choice")
    private StealthChoice stealthChoice = StealthChoice.PRECISE;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "registration_token")
    private String registrationToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<GroupUser> groupUsers = Collections.emptySet();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude

    private UserStatus userStatus;

    @OneToMany(mappedBy = "userOne", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Relationship> sentRelationships = Collections.emptyList();

    @OneToMany(mappedBy = "userTwo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Relationship> incomingRelationships = Collections.emptyList();

    @JsonIgnore
    public List<User> getRelatedUsers() {
        return Stream.concat(sentRelationships.stream(), incomingRelationships.stream())
                .map(relationship -> {
                    if (relationship.getUserOne().equals(this)) return relationship.getUserTwo();
                    else return relationship.getUserOne();
                } )
                .toList();
    }
}
