package com.kiwi.features.conversations.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationOptionDTO {
	private Long id;
	private Long conversationId;
	private String text;
	private String textM;
	private String textW;
	private Long nextEventId;
	private Integer cost;
}
