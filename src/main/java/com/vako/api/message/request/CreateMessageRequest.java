package com.vako.api.message.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateMessageRequest {

    private Long senderId;
    private Long groupId;
    private String text;
}
