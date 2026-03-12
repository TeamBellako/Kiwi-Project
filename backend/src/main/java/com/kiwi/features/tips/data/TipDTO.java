package com.kiwi.features.tips.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode @ToString
public class TipDTO {
    private String title;
    private String text;
    private String readMoreURL;
}
