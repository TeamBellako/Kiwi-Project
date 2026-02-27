package com.kiwi.features.conversations.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private Long id;
    private String name;
    private ConversationType type;
    private Long spriteId;
    private Long expresionId;
    private Long backgroundId;
    private Long fxId;
    private Boolean dark;
    private Integer delayStartMs;
    private Integer delayEndMs;
    private String dialog;
    private String dialogM;
    private String dialogW;
    private NextEventType nextEvent;
    private Long eventId;
    private List<ConversationOptionDTO> options;
}

