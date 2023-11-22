package com.vako.application.relationship.model;

import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "relationship")
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_one", referencedColumnName = "id")
    private User userOne;

    @OneToOne
    @JoinColumn(name = "user_two", referencedColumnName = "id")
    private User userTwo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RelationshipStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "stealth_choice_user_one")
    private StealthChoice stealthChoiceUserOne = StealthChoice.PRECISE;

    @Enumerated(EnumType.STRING)
    @Column(name = "stealth_choice_user_two")
    private StealthChoice stealthChoiceUserTwo = StealthChoice.PRECISE;
}
