package com.kiwi.features.personality.data;

import com.kiwi.features.users.data.UsersPersistence;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "personality")
public class PersonalityPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "knight_name")
    private String knightName;

    @Column(name = "build")
    private String build;

    @Convert(converter = StringListConverter.class)
    @Column(name = "good_apps")
    private List<String> goodApps;

    @Convert(converter = StringListConverter.class)
    @Column(name = "bad_apps")
    private List<String> badApps;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private UsersPersistence user;


    @Converter
    public static class StringListConverter implements AttributeConverter<List<String>, String> {
        private static final String SPLIT_CHAR = ",";
        @Override
        public String convertToDatabaseColumn(List<String> list) {
            return list != null ? String.join(SPLIT_CHAR, list) : "";
        }
        @Override
        public List<String> convertToEntityAttribute(String joined) {
            return joined != null && !joined.isEmpty() ? Arrays.asList(joined.split(SPLIT_CHAR)) : new ArrayList<>();
        }
    }

}
