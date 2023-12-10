package com.vako.application.relationship.repository;

import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.model.RelationshipStatus;
import com.vako.application.user.model.StealthChoice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    @Query("select f from Relationship f " +
            "where f.status = :relationshipStatus and (f.userOne.email = :email or f.userTwo.email = :email)" )
    List<Relationship> getRelationshipsByStatus(@Param("email") final String email, @Param("relationshipStatus") final RelationshipStatus relationshipStatus);

    @Query("select f from Relationship f " +
            "where f.status = :relationshipStatus and (f.userTwo.email = :email)" )
    List<Relationship> getRelationshipsByFriendTwoStatus(@Param("email") final String email, @Param("relationshipStatus") final RelationshipStatus relationshipStatus);

    @Modifying
    @Transactional
    @Query("update Relationship f " +
            "set f.status = :newStatus WHERE f.userOne.id= :userOneId and f.userTwo.id = :userTwoId ")
    int updateStatus(@Param("userOneId") final Long userOneId, @Param("userTwoId") final Long userTwoId, @Param("newStatus") final RelationshipStatus newStatus);

    @Modifying
    @Transactional
    @Query("update Relationship f " +
            "set f.stealthChoiceUserOne = :stealthChoiceUserOne WHERE f.userOne.id= :userOneId and f.userTwo.id = :userTwoId")
    int updateStealthChoiceUserOne(@Param("userOneId") final Long userOneId, @Param("userTwoId") final Long userTwoId, @Param("stealthChoiceUserOne") StealthChoice stealthChoiceUserOne);

    @Modifying
    @Transactional
    @Query("update Relationship f " +
            "set f.stealthChoiceUserTwo = :stealthChoiceUserTwo WHERE f.userTwo.id= :userTwoId and f.userOne.id = :userOneId")
    int updateStealthChoiceUserTwo(@Param("userTwoId") final Long userTwoId, @Param("userOneId") final Long userOneId, @Param("stealthChoiceUserTwo") StealthChoice stealthChoiceUserTwo);

    @Modifying
    @Query("delete from Relationship f " +
            "where (f.userOne.id = :id_1 and f.userTwo.id = :id_2) or (f.userOne.id = :id_2 and f.userTwo.id = :id_1)")
    int deleteByUserIds(@Param("id_1") final Long idOne, @Param("id_2") final Long idTwo);

    @Query("select f from Relationship f " +
            "where (f.userOne.id = :id_1 and f.userTwo.id = :id_2) or (f.userOne.id = :id_2 and f.userTwo.id = :id_1)")
    Optional<Relationship> getRelationshipByUserIds(@Param("id_1") final Long idOne, @Param("id_2") final Long idTwo);
}
