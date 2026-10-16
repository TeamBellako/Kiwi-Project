package com.kiwi.features.tips.data;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "tips")
public class TipPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title_en", nullable = false)
    private String title;

    @Column(name = "text_en", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "read_more_url")
    private String readMoreURL;
}
