package com.vako.application.relationship.service;

import com.vako.api.user.response.BasicUserResponse;
import com.vako.api.user.response.UserOnlineResponse;
import com.vako.api.user.response.UserStatusResponse;
import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.repository.RelationshipRepository;
import com.vako.application.user.mapper.UserMapper;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.vako.application.relationship.model.RelationshipStatus.*;

@Service
@AllArgsConstructor
@Slf4j
public class RelationshipService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final UserMapper userMapper;

    private final RelationshipRepository relationshipRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    public void sendFriendRequest(final String senderNickname, final Long id) {
        final User sender = userService.getUserByIdentifier(senderNickname);
        final User recipient = userService.getUserById(id);
        final Relationship relationship = relationshipRepository.save(new Relationship(sender, recipient));
    }

    @Transactional
    public void deleteFriendship(final String email, final Long id) {
        final User deleter = userService.getUserByEmail(email);
        final int deletes = relationshipRepository.deleteByUserIds(deleter.getId(), id);
        if (deletes == 1) log.info("Deleted friendship for users with ID's {}, {}", deleter.getId(), id);
    }

    public List<User> getActiveFriends(final String email) {
        final List<Relationship> activeFriends = relationshipRepository.getRelationshipsByStatus(email, ACTIVE);
        return activeFriends.stream()
                .map(relationship -> {
                    if (relationship.getUserOne().getEmail().equals(email)) return relationship.getUserTwo();
                    else return relationship.getUserOne();
                })
                .collect(Collectors.toList());
    }

    public List<UserOnlineResponse> getFriendsWithTheirActivityStatuses(final String email) {
        final List<UserOnlineResponse> userOnlineResponses = getActiveFriends(email).stream().map(userMapper::userToUserOnlineResponse).toList();
        return userOnlineResponses;
    }

    public List<BasicUserResponse> getPendingRequests(final String email) {
        final List<Relationship> pendingRelationships = relationshipRepository.getRelationshipsByStatus(email, PENDING);
        return pendingRelationships.stream()
                .map(Relationship::getUserOne)
                .map(userMapper::userToBasicUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendRequest(final String accepteeEmail, final Long senderId) {
        final User user = userService.getUserByEmail(accepteeEmail);
        final int updated = relationshipRepository.updateStatus(senderId, user.getId(), ACTIVE);
        if (updated == 1) log.info("Set users with ids: {}, {} to status ACTIVE", senderId, user.getId());
    }

    @Transactional
    public void declineFriendRequest(final String declineeEmail, final Long senderId) {
        final User user = userService.getUserByEmail(declineeEmail);
        relationshipRepository.updateStatus(senderId, user.getId(), DECLINED);
    }

    public List<BasicUserResponse> getBasicFriendInfo(final String email) {
        return getActiveFriends(email).stream().map(userMapper::userToBasicUserResponse).toList();
    }

    public List<UserStatusResponse> getFriendStatuses(final String email) {
        return getActiveFriends(email).stream().map(userMapper::userToUserStatusResponse).toList();
    }

    public List<BasicUserResponse> usersWithNicknameLike(final String email, final String queriedNickname, final Integer pageSize) {
        final int pageSizeToUse = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
        final String userName = userService.getUserByEmail(email).getNickname();
        List<String> usernamesToExclude = new ArrayList<>(getActiveFriends(email).stream().map(User::getNickname).toList());
        usernamesToExclude.add(userName);
        final List<User> users = userRepository.findAllByNicknameContainsAndNicknameNotIn(queriedNickname, usernamesToExclude, PageRequest.of(0, pageSizeToUse));
        log.info("Found {} users with nickname containing {}", users.size(), queriedNickname);
        return users.stream().map(userMapper::userToBasicUserResponse).toList();
    }

}
