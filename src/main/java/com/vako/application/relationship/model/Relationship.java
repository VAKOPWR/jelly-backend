package com.vako.application.relationship.model;

import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@Table(name = "relationship")
@EqualsAndHashCode
public class Relationship {

    public Relationship(User userOne, User userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_one", referencedColumnName = "id")
    @ToString.Exclude
    private User userOne;

    @ManyToOne
    @JoinColumn(name = "user_two", referencedColumnName = "id")
    @ToString.Exclude
    private User userTwo;

    @Column(name = "status")
    private RelationshipStatus status = RelationshipStatus.PENDING;

    @Column(name = "stealth_choice_user_one")
    private StealthChoice stealthChoiceUserOne = StealthChoice.PRECISE;

    @Column(name = "stealth_choice_user_two")
    private StealthChoice stealthChoiceUserTwo = StealthChoice.PRECISE;
}
