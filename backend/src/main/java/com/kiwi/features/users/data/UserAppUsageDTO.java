package com.kiwi.features.users.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class UserAppUsageDTO {
    private Long avgGoodDailyUsageMs;
    private Long avgBadDailyUsageMs;
}
