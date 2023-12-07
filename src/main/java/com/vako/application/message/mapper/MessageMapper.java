package com.vako.application.message.mapper;

import com.vako.api.message.request.CreateMessageRequest;
import com.vako.application.message.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper
public interface MessageMapper {

    @Mapping(source = "createMessageRequest.groupId", target = "message.group.id")
    @Mapping(source = "timeSent", target = "message.timeSent")
    Message createMessageRequestToMessage(final CreateMessageRequest createMessageRequest,
                                          final LocalDateTime timeSent);

}
