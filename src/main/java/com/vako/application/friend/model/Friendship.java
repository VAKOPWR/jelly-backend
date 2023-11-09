package com.vako.application.friend.model;

import com.vako.application.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "friendship")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "friend_one", referencedColumnName = "id")
    private User friendOne;

    @OneToOne
    @JoinColumn(name = "friend_two", referencedColumnName = "id")
    private User friendTwo;

    @Column(name = "status")
    private FriendshipStatus status;
}
