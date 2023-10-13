package com.vako.application.group.model;

import com.vako.application.groupUsers.model.GroupUsers;
import com.vako.application.message.model.Message;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "Group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_friendship", nullable = false)
    private Boolean isFriendship;

    @Column(name = "description")
    private String description;

    @Column(name = "group_picture", nullable = false)
    private String groupPicture;

    @OneToMany(mappedBy = "group")
    private Set<Message> messages;

    @OneToMany(mappedBy = "group")
    private Set<GroupUsers> groupUsers;
}
