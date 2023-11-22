package com.vako.application.relationship.service;

import com.vako.api.user.response.BasicUserResponse;
import com.vako.api.user.response.UserStatusResponse;
import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.repository.RelationshipRepository;
import com.vako.application.user.mapper.UserMapper;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.vako.application.relationship.model.RelationshipStatus.*;

@Service
@AllArgsConstructor
@Slf4j
public class RelationshipService {

    private final UserMapper userMapper;

    private final RelationshipRepository relationshipRepository;

    private final UserService userService;

    public void sendFriendRequest(final String senderNickname, final Long id) {
        final User sender = userService.getUserByIdentifier(senderNickname);
        final User recipient = userService.getUserById(id);
        final Relationship relationship = relationshipRepository.save(Relationship.builder()
                        .userOne(sender)
                        .userTwo(recipient)
                        .status(PENDING)
                .build());
    }

    public void deleteFriendship(final String email, final Long id) {
        relationshipRepository.deleteByEmailAndId(email, id);
    }

    public List<User> getActiveFriends(final String email) {
        final List<Relationship> pendingRelationships = relationshipRepository.getRelationshipsByStatus(email, ACTIVE);
        return pendingRelationships.stream()
                .map(relationship -> {
                    if (relationship.getUserOne().getEmail().equals(email)) return relationship.getUserTwo();
                    else return relationship.getUserOne();
                })
                .collect(Collectors.toList());
    }

    public List<User> getPendingRequests(final String email) {
        final List<Relationship> pendingRelationships = relationshipRepository.getRelationshipsByStatus(email, PENDING);
        return pendingRelationships.stream()
                .map(Relationship::getUserOne)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendRequest(final String accepteeEmail, final Long senderId) {
        relationshipRepository.updateStatus(senderId, accepteeEmail, ACTIVE);
    }

    @Transactional
    public void declineFriendRequest(final String declineeEmail, final Long senderId) {
        relationshipRepository.updateStatus(senderId, declineeEmail, DECLINED);
    }

    public List<BasicUserResponse> getBasicFriendInfo(final String email) {
        return getActiveFriends(email).stream().map(userMapper::userToBasicUserResponse).toList();
    }

    public List<UserStatusResponse> getFriendStatuses(final String email) {
        return getActiveFriends(email).stream().map(userMapper::userToUserStatusResponse).toList();
    }

}
