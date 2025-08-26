package com.kiwi.features.metrics.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class MetricsDTO {
    private String date;
    private Integer maxGoodTimeSeconds;
    private Integer currentGoodTimeSeconds;
    private Integer maxBadTimeSeconds;
    private Integer currentBadTimeSeconds;
}