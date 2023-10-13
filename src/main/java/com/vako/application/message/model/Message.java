package com.vako.application.message.model;

import com.vako.application.group.model.Group;
import com.vako.application.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "Messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "time_sent", nullable = false)
    private Date timeSent;

}

