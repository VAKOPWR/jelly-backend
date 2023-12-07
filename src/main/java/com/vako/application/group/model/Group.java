package com.vako.application.group.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.message.model.Message;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Data
@Table(name = "[Group]")
public class Group {

    public Group() {

    }

    public Group(final boolean isFriendship) {
        this.isFriendship = isFriendship;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_friendship", nullable = false)
    private boolean isFriendship = false;

    @Column(name = "description")
    private String description;

    @Column(name = "group_picture")
    private String groupPicture;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Message> messages;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<GroupUser> groupUsers;

}
