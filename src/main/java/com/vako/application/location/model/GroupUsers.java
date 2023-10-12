package com.vako.application.location.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Group_Users")
public class GroupUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "stealth_choice", nullable = false)
    private Integer stealthChoice;

}

