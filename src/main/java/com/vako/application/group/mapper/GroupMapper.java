package com.vako.application.group.mapper;

import com.vako.api.message.request.CreateGroupChatRequest;
import com.vako.application.group.model.Group;
import org.mapstruct.Mapper;

@Mapper
public interface GroupMapper {

    Group createGroupChatRequestToGroup(CreateGroupChatRequest createGroupChatRequest);
}
