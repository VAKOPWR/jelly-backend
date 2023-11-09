package com.vako.application.friend.service;

import com.vako.application.friend.model.Friendship;
import com.vako.application.friend.model.FriendshipStatus;
import com.vako.application.friend.repository.FriendshipRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.vako.application.friend.model.FriendshipStatus.ACTIVE;
import static com.vako.application.friend.model.FriendshipStatus.PENDING;

@Service
@AllArgsConstructor
@Slf4j
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    private final UserService userService;

    public void sendFriendRequest(final String senderNickname, final String recipientIdentifier) {
        final User sender = userService.getUserByIdentifier(senderNickname);
        final User recipient = userService.getUserByIdentifier(recipientIdentifier);
        final Friendship friendship = friendshipRepository.save(Friendship.builder()
                        .friendOne(sender)
                        .friendTwo(recipient)
                        .status(PENDING)
                .build());
    }

    public List<User> getActiveFriends(final String email) {
        final List<Friendship> pendingFriendships = friendshipRepository.getFriendshipsByStatus(email, ACTIVE);
        return pendingFriendships.stream()
                .map(friendship -> {
                    if (friendship.getFriendOne().getEmail().equals(email)) return friendship.getFriendTwo();
                    else return friendship.getFriendOne();
                })
                .collect(Collectors.toList());
    }

    public List<User> getPendingRequests(final String email) {
        final List<Friendship> pendingFriendships = friendshipRepository.getFriendshipsByStatus(email, PENDING);
        return pendingFriendships.stream()
                .map(Friendship::getFriendOne)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendRequest(final String accepteeEmail, final Long senderId) {
        final User acceptee = userService.getUserByEmail(accepteeEmail);
        friendshipRepository.updateStatus(senderId, acceptee.getId(), ACTIVE);
    }

}
