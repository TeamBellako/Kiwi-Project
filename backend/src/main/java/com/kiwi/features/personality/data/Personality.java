package com.kiwi.features.personality.data;

import com.kiwi.features.personality.exceptions.PersonalityInvalidException;
import com.kiwi.features.users.data.UsersPersistence;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "personality")
public class Personality {
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


    public Personality() {
    }

    public Personality(Integer id, String realName, String knightName, String build, List<String> goodApps, List<String> badApps) {
        setId(id);
        setRealName(realName);
        setKnightName(knightName);
        setBuild(build);
        setGoodApps(goodApps);
        setBadApps(badApps);
    }

    public Personality(String realName, String knightName, String build, List<String> goodApps, List<String> badApps) {
        setRealName(realName);
        setKnightName(knightName);
        setBuild(build);
        setGoodApps(goodApps);
        setBadApps(badApps);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id <= 0) throw new PersonalityInvalidException("Settings Id's must be bigger than zero");
        this.id = id;
    }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getKnightName() { return knightName; }
    public void setKnightName(String knightName) { this.knightName = knightName; }

    public String getBuild() { return build; }
    public void setBuild(String build) { this.build = build; }

    public List<String> getGoodApps() { return goodApps; }
    public void setGoodApps(List<String> goodApps) { this.goodApps = goodApps; }

    public List<String> getBadApps() { return badApps; }
    public void setBadApps(List<String> badApps) { this.badApps = badApps; }

    public UsersPersistence getUser() {
        return user;
    }
    public void setUser(UsersPersistence user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", knightName=" + knightName +
                ", build=" + build +
                ", goodApps=" + goodApps +
                ", badApps=" + badApps +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Personality that = (Personality) o;
        return realName.equals(that.realName) && knightName.equals(that.knightName) && build.equals(that.build) && goodApps.equals(that.goodApps) && badApps.equals(that.badApps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(realName, knightName, build);
    }

    public PersonalityDTO toDTO() {
        return new PersonalityDTO(
                getRealName(),
                getKnightName(),
                getBuild(),
                getGoodApps(),
                getBadApps()
        );
    }

    public void mergeFromDTO(PersonalityDTO dto) {
        this.realName = dto.getRealName();
        this.knightName = dto.getKnightName();
        this.build = dto.getBuild();
        this.goodApps = dto.getGoodApps();
        this.badApps = dto.getBadApps();
    }

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
