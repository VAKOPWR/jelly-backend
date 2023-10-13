package com.vako.application.groupUsers.model;

import com.vako.application.group.model.Group;
import com.vako.application.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Group_Users")
public class GroupUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "stealth_choice", nullable = false)
    private Integer stealthChoice;

}

