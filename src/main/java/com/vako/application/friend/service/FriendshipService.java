package com.vako.application.friend.service;

import com.vako.application.friend.model.Friendship;
import com.vako.application.friend.model.FriendshipStatus;
import com.vako.application.friend.repository.FriendshipRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    private final UserService userService;

    public void sendFriendRequest(final String senderNickname, final String recipientNickname) {
        final User sender = userService.getUserByNickname(senderNickname);
        final User recipient = userService.getUserByNickname(recipientNickname);
        final Friendship friendship = friendshipRepository.save(Friendship.builder()
                        .friendOne(sender)
                        .friendTwo(recipient)
                        .status(FriendshipStatus.INVITED)
                .build());
    }

}
