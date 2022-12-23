package com.company.entity;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.Message;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageData {
    private Message message;
    private String customerChatId;
    private Integer messageId;
}
