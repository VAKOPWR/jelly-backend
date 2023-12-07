package com.vako.application.message.mapper;

import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.group.model.Group;
import com.vako.application.message.model.Message;
import com.vako.application.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper
public interface MessageMapper {

    @Mapping(source = "user", target = "message.user")
    @Mapping(source = "group", target = "message.group")
    @Mapping(source = "timeSent", target = "message.timeSent")
    Message createMessageRequestToMessage(final CreateMessageRequest createMessageRequest,
                                          final LocalDateTime timeSent,
                                          final User user,
                                          final Group group);

}
