package com.vako.application.location.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_friendship", nullable = false)
    private Boolean isFriendship;

    @Column(name = "description")
    private String description;

    @Column(name = "group_picture")
    private String groupPicture;

    @OneToMany(mappedBy = "group")
    private List<Message> messages;

}
