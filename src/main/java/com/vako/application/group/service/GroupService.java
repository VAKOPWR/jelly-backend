package com.vako.application.group.service;

import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.application.dto.ChatUserDTO;
import com.vako.application.dto.NewGroupChatDTO;
import com.vako.application.group.mapper.GroupMapper;
import com.vako.application.group.model.Group;
import com.vako.application.group.repository.GroupRepository;
import com.vako.application.groupUsers.model.GroupUser;
import com.vako.application.groupUsers.repository.GroupUserRepository;
import com.vako.application.groupUsers.service.GroupUserService;
import com.vako.application.image.BlobStorageService;
import com.vako.application.user.model.User;
import com.vako.application.user.service.UserService;
import com.vako.exception.JellyException;
import com.vako.exception.JellyExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupService {

    private final String groupImageUrl;

    private final GroupMapper groupMapper;
    private final GroupUserRepository groupUserRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final BlobStorageService blobStorageService;

    @Autowired
    public GroupService(
            @Value("${azure.blob.url.chat}")final String groupImageUrl,
            GroupMapper groupMapper, GroupUserRepository groupUserRepository, UserService userService, GroupRepository groupRepository, BlobStorageService blobStorageService) {
        this.groupImageUrl = groupImageUrl;
        this.groupMapper = groupMapper;
        this.groupUserRepository = groupUserRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.blobStorageService = blobStorageService;
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow();
    }

    public Group getCompleteGroupById(Long id){
        return groupRepository.findCompleteGroupById(id).orElseThrow();
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public void createGroupUser(Long userId, Long groupId) {
        groupUserRepository.save(new GroupUser(userService.getUserById(userId), getGroupById(groupId)));
    }

    public NewGroupChatDTO createGroup(final CreateGroupChatRequest createGroupChatRequest){
        final Group group = groupRepository.save(groupMapper.createGroupChatRequestToGroup(createGroupChatRequest));
        final List<ChatUserDTO> chatUserDTOS = createGroupChatRequest.getUserIds().stream().map(userId -> {
            createGroupUser(userId, group.getId());
            User user = userService.getUserById(userId);
            return new ChatUserDTO(user.getId(), user.getNickname(), user.getProfilePicture());
        }).toList();
        return new NewGroupChatDTO(group.getId(), chatUserDTOS);
    }

    public String updateAvatar(final String email, final Long groupId, final MultipartFile file) throws IOException {
        final User user = userService.getUserByEmail(email);
        if (getCompleteGroupsByUserId(user.getId()).isEmpty())
            throw new JellyException(JellyExceptionType.NOT_AUTHORIZED);
        final String uuid = UUID.randomUUID().toString();
        blobStorageService.saveChatImage(file, uuid);
        final String link = groupImageUrl + uuid;
        groupRepository.updateImageUrl(groupId, link);
        return link;
    }

    public List<Group> getCompleteGroupsByUserId(final Long userId) {
        return groupRepository.findCompleteGroupsByUserId(userId).stream()
                .filter(group -> group.getGroupUsers().stream()
                        .map(groupUser -> groupUser.getUser().getId()).toList().contains(userId))
                .toList();
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    public void deleteGroupFriendship(Long userId1, Long userId2){
        Integer groupIdToDelete = groupRepository.findGroupByUserIds(userId1, userId2);
        groupRepository.deleteMessagesByGroupId(groupIdToDelete);
        groupRepository.deleteGroupUsersByGroupId(groupIdToDelete);
        groupRepository.deleteFriendshipGroupByGroupId(groupIdToDelete);
    }
}
