package com.kiwi.features.personality;

import com.kiwi.features.users.UsersPersistence;
import jakarta.persistence.*;
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

    @OneToOne()
    @JoinColumn(name = "user_id")
    private UsersPersistence user;


    public Personality() {
    }

    public Personality(Integer id, String realName, String knightName, String build) {
        setId(id);
        setRealName(realName);
        setKnightName(knightName);
        setBuild(build);
    }

    public Personality(String realName, String knightName, String build) {
        setRealName(realName);
        setKnightName(knightName);
        setBuild(build);
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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Personality that = (Personality) o;
        return realName.equals(that.realName) && knightName.equals(that.knightName) && build.equals(that.build);
    }

    @Override
    public int hashCode() {
        return Objects.hash(realName, knightName, build);
    }

    public PersonalityDTO toDTO() {
        return new PersonalityDTO(
                getRealName(),
                getKnightName(),
                getBuild()
        );
    }

    public void mergeFromDTO(PersonalityDTO dto) {
        this.realName = dto.getRealName();
        this.knightName = dto.getKnightName();
        this.build = dto.getBuild();
    }
}