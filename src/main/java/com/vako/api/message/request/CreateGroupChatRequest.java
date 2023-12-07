package com.vako.api.message.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateGroupChatRequest {

    private String name;
    private String description;
    private List<Long> userIds;
}
