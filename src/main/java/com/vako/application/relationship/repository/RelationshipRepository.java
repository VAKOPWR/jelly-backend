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
            "where f.status = :friendshipStatus and (f.userOne.email = :email or f.userTwo.email = :email)" )
    List<Relationship> getFriendshipsByStatus(@Param("email") final String email, @Param("friendshipStatus") final RelationshipStatus relationshipStatus);

    @Modifying
    @Query("update Relationship f " +
            "set f.status = :newStatus WHERE f.userOne.id= :friendOneId and f.userTwo.email = :friendTwoEmail ")
    int updateStatus(@Param("friendOneId") final Long friendOneId, @Param("friendTwoEmail") final String friendTwoEmail, @Param("newStatus") final RelationshipStatus newStatus);

    @Query("select f from Relationship f " +
            "WHERE f.userOne.id= :friendOneId and f.userTwo.id = :friendTwoId")
    Optional<Relationship> getFriendshipByFriendIds(@Param("friendOneId") final Long friendOneId, @Param("friendTwoId") final Long friendTwoId);
}
