package com.vako.application.message.service;

import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.dto.GroupMessageDTO;
import com.vako.application.dto.MessageDTO;
import com.vako.application.fcm.FirebaseCloudMessagingService;
import com.vako.application.group.model.Group;
import com.vako.application.group.service.GroupService;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.repository.GroupUserRepository;
import com.vako.application.image.BlobStorageService;
import com.vako.application.message.mapper.MessageMapper;
import com.vako.application.message.model.Message;
import com.vako.application.message.repository.MessageRepository;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import com.vako.exception.JellyException;
import com.vako.exception.JellyExceptionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.vako.application.fcm.FirebaseNotificationText.NEW_MESSAGE_IN_CHAT;

@Service
public class GroupMessageService {

    private static final int DEFAULT_PAGE_SIZE = 40;

    private final String messageImageUrl;
    private final MessageMapper messageMapper;
    private final GroupService groupService;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final BlobStorageService blobStorageService;
    private final GroupUserRepository groupUserRepository;
    private final FirebaseCloudMessagingService firebaseCloudMessagingService;

    @Autowired
    public GroupMessageService(@Value("${azure.blob.url.message}")String messageImageUrl,
                               MessageMapper messageMapper,
                               GroupService groupService,
                               GroupUserRepository groupUserRepository, MessageRepository messageRepository,
                               UserService userService,
                               BlobStorageService blobStorageService,
                               FirebaseCloudMessagingService firebaseCloudMessagingService) {
        this.messageImageUrl = messageImageUrl;
        this.messageMapper = messageMapper;
        this.groupService = groupService;
        this.groupUserRepository = groupUserRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.blobStorageService = blobStorageService;
        this.firebaseCloudMessagingService = firebaseCloudMessagingService;
    }



    public List<GroupMessageDTO> getChats (String email){
        final User user = userService.getUserByEmail(email);
        final List<Group> groups = groupService.getCompleteGroupsByUserId(user.getId());
        return groups.stream().map(group -> {
            final GroupMessageDTO groupMessageDTO = messageMapper.groupToMessageDTO(group);
            groupMessageDTO.setGroupName(getChatName(group, user.getNickname()));
            group.getGroupUsers().forEach(groupUser -> {
                if (groupUser.getUser().getId().equals(user.getId())) {
                    groupMessageDTO.setMuted(groupUser.isMuted());
                    groupMessageDTO.setPinned(groupUser.isPinned());
                }
                if (group.isFriendship() && !groupUser.getUser().getId().equals(user.getId())) groupMessageDTO.setFriendId(groupUser.getUser().getId());
            });
            return groupMessageDTO;
            }
        ).toList();
    }

    public List<GroupMessageDTO> getNewChats(String email, Set<Long> ids) {
        List<GroupMessageDTO> existingChats = getChats(email);
        if (existingChats == null || existingChats.isEmpty()) {
            return Collections.emptyList();
        }
        List<GroupMessageDTO> newAndDeletedChats = existingChats.stream()
                .filter(chat -> !ids.contains(chat.getGroupId()))
                .collect(Collectors.toList());
        ids.stream()
                .filter(id -> newAndDeletedChats.stream().noneMatch(chat -> chat.getGroupId().equals(id)))
                .forEach(id -> {
                    GroupMessageDTO mockedChat = createDeletedChat(id);
                    newAndDeletedChats.add(mockedChat);
                });
        return newAndDeletedChats;
    }

    public List<MessageDTO> loadMessagesPaged(Long groupId, Integer pageToLoad){
        Page<Message> messagePage = messageRepository.findMessageByGroup(groupId, PageRequest.of(pageToLoad, 400));
        return messagePage.stream().map(messageMapper::messageToMessageDTO).toList();
    }

    public List<MessageDTO> loadMessagesNew(List<Long> groupIds, Long userId){
        List<GroupUser> groupUsers = groupUserRepository.findGroupUsersByUserIdAndGroupIds(userId, groupIds);
        List<Message> messages = messageRepository.findMessagesAfterTimeInGroups(groupUsers, groupIds);
        for (GroupUser groupUser: groupUsers){
            groupUser.setLastChecked(LocalDateTime.now());
            groupUserRepository.save(groupUser);
        }
        return messages.stream().map(messageMapper::messageToMessageDTO).toList();
    }


    @Transactional
    public String createMessage(final String email, final CreateMessageRequest createMessageRequest) {
        final User user = userService.getUserByEmail(email);
        final Group group = groupService.getCompleteGroupById(createMessageRequest.getGroupId());
        final Message message = messageMapper.createMessageRequestToMessage(createMessageRequest, LocalDateTime.now(), user, group);
        group.getGroupUsers().stream().map(groupUser -> groupUser.getUser().getRegistrationToken())
                .filter(Objects::nonNull)
                .forEach(registrationToken -> firebaseCloudMessagingService.sendMessage(NEW_MESSAGE_IN_CHAT.getMessageWithTwoParams(user.getNickname(), createMessageRequest.getText()) ,registrationToken));
        messageRepository.save(message);
        return message.getTimeSent().toString();
    }

    public String attachImage(final String email, final Long messageId, final MultipartFile file) throws IOException {
        final User user = userService.getUserByEmail(email);
        if (getChats(email).isEmpty())
            throw new JellyException(JellyExceptionType.NOT_AUTHORIZED);
        final String uuid = UUID.randomUUID().toString();
        blobStorageService.saveMessageImage(file, uuid);
        final String link = messageImageUrl + uuid;
        messageRepository.updateImageUrl(messageId, link);
        return link;
    }

    public void createPersonalChat(Long userId1, Long userId2) {
        Group group = groupService.createGroup(new Group(true));
        groupService.createGroupUser(userId1, group.getId());
        groupService.createGroupUser(userId2, group.getId());
    }

    private String getChatName(final Group group, final String username) {
        if (!group.isFriendship()) return group.getName();
        return group.getGroupUsers().stream()
                .filter(groupUser -> !groupUser.getUser().getNickname().equals(username))
                .map(groupUser -> groupUser.getUser().getNickname())
                .findFirst().get();
    }

    public GroupMessageDTO createDeletedChat(Long id) {
        GroupMessageDTO deletedChat = new GroupMessageDTO();
        deletedChat.setGroupId(id);
        deletedChat.setGroupName("deletedChat");
        deletedChat.setFriendship(false);
        deletedChat.setPinned(false);
        deletedChat.setMuted(false);
        return deletedChat;
    }
}
