package com.vako.application.friend.repository;

import com.vako.application.friend.model.Friendship;
import com.vako.application.friend.model.FriendshipStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("select f from Friendship f " +
            "where f.status = :friendshipStatus and (f.friendOne.email = :email or f.friendTwo.email = :email)" )
    List<Friendship> getFriendshipsByStatus(@Param("email") final String email, @Param("friendshipStatus") final FriendshipStatus friendshipStatus);

    @Modifying
    @Query("update Friendship f " +
            "set f.status = :newStatus WHERE f.friendOne.id= :friendOneId and f.friendTwo.id = :friendTwoId ")
    int updateStatus(@Param("friendOneId") final Long friendOneId, @Param("friendTwoId") final Long friendTwoId, @Param("newStatus") final FriendshipStatus newStatus);

    @Query("select f from Friendship f " +
            "WHERE f.friendOne.id= :friendOneId and f.friendTwo.id = :friendTwoId")
    Optional<Friendship> getFriendshipByFriendIds(@Param("friendOneId") final Long friendOneId, @Param("friendTwoId") final Long friendTwoId);
}
