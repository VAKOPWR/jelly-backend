package com.vako.application.friend.repository;

import com.vako.application.friend.model.Friendship;
import com.vako.application.friend.model.FriendshipStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("select f from Friendship f " +
            "inner join User u on f.friendOne.id = u.id "+
            "inner join User us on f.friendTwo.id = us.id " +
            "where f.status = :friendshipStatus and (u.email = :email or us.email = :email)" )
    List<Friendship> getFriendshipsByStatus(@Param("email") final String email, @Param("identifier") final FriendshipStatus friendshipStatus);

    @Modifying
    @Transactional
    @Query("update Friendship f set f.status = :newStatus WHERE f.friendOne.id = :friendOneId and f.friendTwo.id = :friendTwoId")
    void updateStatus(@Param("friendOneId") final Long friendOneId, @Param("friendTwoId") final Long friendTwoId, @Param("newStatus") final FriendshipStatus newStatus);
}
