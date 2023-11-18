package com.vako.application.relationship.service;

import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.repository.RelationshipRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.vako.application.relationship.model.RelationshipStatus.ACTIVE;
import static com.vako.application.relationship.model.RelationshipStatus.PENDING;

@Service
@AllArgsConstructor
@Slf4j
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;

    private final UserService userService;

    public void sendFriendRequest(final String senderNickname, final String recipientIdentifier) {
        final User sender = userService.getUserByIdentifier(senderNickname);
        final User recipient = userService.getUserByIdentifier(recipientIdentifier);
        final Relationship relationship = relationshipRepository.save(Relationship.builder()
                        .userOne(sender)
                        .userTwo(recipient)
                        .status(PENDING)
                .build());
    }

    public List<User> getActiveFriends(final String email) {
        final List<Relationship> pendingRelationships = relationshipRepository.getFriendshipsByStatus(email, ACTIVE);
        return pendingRelationships.stream()
                .map(relationship -> {
                    if (relationship.getUserOne().getEmail().equals(email)) return relationship.getUserTwo();
                    else return relationship.getUserOne();
                })
                .collect(Collectors.toList());
    }

    public List<User> getPendingRequests(final String email) {
        final List<Relationship> pendingRelationships = relationshipRepository.getFriendshipsByStatus(email, PENDING);
        return pendingRelationships.stream()
                .map(Relationship::getUserOne)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendRequest(final String accepteeEmail, final Long senderId) {
        relationshipRepository.updateStatus(senderId, accepteeEmail, ACTIVE);
    }

}
