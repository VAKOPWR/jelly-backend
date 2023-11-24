package com.vako.application.relationship.repository;

import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.model.RelationshipStatus;
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

    @Modifying
    @Query("update Relationship f " +
            "set f.status = :newStatus WHERE f.userOne.id= :userOneId and f.userTwo.id = :userTwoId ")
    int updateStatus(@Param("userOneId") final Long userOneId, @Param("userTwoId") final Long userTwoId, @Param("newStatus") final RelationshipStatus newStatus);

    @Query("select f from Relationship f " +
            "WHERE f.userOne.id= :userOneId and f.userTwo.id = :userTwoId")
    Optional<Relationship> getRelationshipByUserIds(@Param("userOneId") final Long userOneId, @Param("userTwoId") final Long userTwoId);

    @Query("delete from Relationship f " +
            "where (f.userOne.email = :email and f.userTwo.id = :id) or (f.userTwo.email = :email and f.userOne.id = :id)")
    void deleteByEmailAndId(@Param("email") final String email, @Param("id") final Long id);
}
