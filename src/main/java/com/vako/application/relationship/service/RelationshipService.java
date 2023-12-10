package com.vako.application.relationship.service;

import com.vako.api.user.response.BasicUserResponse;
import com.vako.api.user.response.UserOnlineResponse;
import com.vako.api.user.response.UserStatusResponse;
import com.vako.application.message.service.GroupMessageService;
import com.vako.application.fcm.FirebaseCloudMessagingService;
import com.vako.application.relationship.model.Relationship;
import com.vako.application.relationship.repository.RelationshipRepository;
import com.vako.application.user.mapper.UserMapper;
import com.vako.application.user.model.StealthChoice;
import com.vako.application.user.model.StealthChoiceUpdater;
import com.vako.application.user.model.User;
import com.vako.application.user.repository.UserRepository;
import com.vako.application.user.service.UserService;
import com.vako.exception.JellyException;
import com.vako.exception.JellyExceptionType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.vako.application.fcm.FirebaseNotificationText.ACCEPTED_FRIEND_REQUEST;
import static com.vako.application.fcm.FirebaseNotificationText.SENT_FRIEND_REQUEST;
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

    private final GroupMessageService groupMessageService;

    private final FirebaseCloudMessagingService firebaseCloudMessagingService;

    @Transactional
    public void sendFriendRequest(final String senderEmail, final Long id) {
        final User sender = userService.getUserByIdentifier(senderEmail);
        if (relationshipRepository.getRelationshipByUserIds(sender.getId(), id).isPresent())
            throw new JellyException(JellyExceptionType.RELATIONSHIP_ALREADY_EXISTS);
        final User recipient = userService.getUserById(id);
        relationshipRepository.save(new Relationship(sender, recipient));
        firebaseCloudMessagingService.sendMessage(SENT_FRIEND_REQUEST.getMessageWithParams(sender.getNickname()), recipient.getRegistrationToken());
    }

    @Transactional
    public void deleteFriendship(final String email, final Long id) {
        final User deleter = userService.getUserByEmail(email);
        final int deletes = relationshipRepository.deleteByUserIds(deleter.getId(), id);
        if (deletes == 1) {
            log.info("Deleted friendship for users with ID's {}, {}", deleter.getId(), id);
        }
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
        return getActiveFriends(email).stream().map(userMapper::userToUserOnlineResponse).toList();
    }

    public List<BasicUserResponse> getPendingRequests(final String email) {
        final List<Relationship> pendingRelationships = relationshipRepository.getRelationshipsByFriendTwoStatus(email, PENDING);
        return pendingRelationships.stream()
                .map(Relationship::getUserOne)
                .map(userMapper::userToBasicUserResponse)
                .toList();
    }

    @Transactional
    public void acceptFriendRequest(final String accepteeEmail, final Long senderId) {
        final User acceptee = userService.getUserByEmail(accepteeEmail);
        groupMessageService.createPersonalChat(acceptee.getId(), senderId);
        final int updated = relationshipRepository.updateStatus(senderId, acceptee.getId(), ACTIVE);
        final User sender = userService.getUserById(senderId);
        if (updated == 1) {
            log.info("Set users with ids: {}, {} to status ACTIVE", senderId, acceptee.getId());
            firebaseCloudMessagingService.sendMessage(ACCEPTED_FRIEND_REQUEST.getMessageWithParams(acceptee.getNickname()), sender.getRegistrationToken());
        }

    }

    @Transactional
    public void declineFriendRequest(final String declineeEmail, final Long senderId) {
        final User user = userService.getUserByEmail(declineeEmail);
        relationshipRepository.updateStatus(senderId, user.getId(), DECLINED);
    }

    public List<BasicUserResponse> getBasicFriendInfo(final String email) {
        return getActiveFriends(email).stream().map(userMapper::userToBasicUserResponse).toList();
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

    @Transactional
    public void updateStealthChoice(String email, final Long id, StealthChoice stealthChoice) {
        final User updater = userService.getUserByEmail(email);
        Relationship relationship = relationshipRepository.getRelationshipByUserIds(updater.getId(), id).orElseThrow();

        StealthChoiceUpdater updaterMethod = updater.getId().equals(relationship.getUserOne().getId()) ?
                relationshipRepository::updateStealthChoiceUserOne :
                relationshipRepository::updateStealthChoiceUserTwo;


        int updated = updaterMethod.update(updater.getId(), id, stealthChoice);

        if (updated == 1)
            log.info("Set users with ids: {}, {} to StealthChoice: {}", updater.getId(), id, stealthChoice);
    }

    public List<UserStatusResponse> getFriendStatuses(final String email) {
        final User requester = userService.getUserByEmail(email);
        return getActiveFriends(email).stream()
                .map(user -> createStatusResponseBasedOnStealthChoice(requester, user))
                .collect(Collectors.toList());
    }

    private UserStatusResponse createStatusResponseBasedOnStealthChoice(User requester, User friend) {
        Relationship relationship = relationshipRepository.getRelationshipByUserIds(requester.getId(), friend.getId()).orElseThrow();
        return determineResponseBasedOnStealth(relationship, requester, friend);
    }


    private UserStatusResponse determineResponseBasedOnStealth(Relationship relationship, User requester, User friend) {
        boolean isRequesterUserOne = relationship.getUserOne().getId().equals(requester.getId());
        StealthChoice stealthChoice = isRequesterUserOne ? relationship.getStealthChoiceUserTwo() : relationship.getStealthChoiceUserOne();

        if (stealthChoice == StealthChoice.HIDE || friend.getStealthChoice().equals(StealthChoice.HIDE)) {
            return UserStatusResponse.builder()
                    .id(friend.getId())
                    .nickname(friend.getNickname())
                    .positionLat(BigDecimal.ZERO)
                    .positionLon(BigDecimal.ZERO)
                    .speed(0)
                    .batteryLevel(0)
                    .build();
        } else {
            return userMapper.userToUserStatusResponse(friend);
        }
    }
}
