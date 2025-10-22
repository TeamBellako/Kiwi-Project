package com.kiwi.features.personality.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersonalityDomain {
    private String realName;
    private String knightName;
    private String build;
    private List<String> goodApps;
    private List<String> badApps;
    private List<String> neutralApps;

    public PersonalityDomain() {
        this.realName = "";
        this.knightName = "";
        this.build = "";
        this.goodApps = new ArrayList<>();
        this.badApps = new ArrayList<>();
        this.neutralApps = new ArrayList<>();
    }

    public PersonalityDomain(String realName, String knightName, String build, List<String> goodApps,
            List<String> badApps, List<String> neutralApps) {
        this.realName = realName;
        this.knightName = knightName;
        this.build = build;
        this.goodApps = goodApps;
        this.badApps = badApps;
        this.neutralApps = neutralApps;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getKnightName() {
        return knightName;
    }

    public void setKnightName(String knightName) {
        this.knightName = knightName;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public List<String> getGoodApps() {
        return goodApps;
    }

    public void setGoodApps(List<String> goodApps) {
        this.goodApps = goodApps;
    }

    public List<String> getBadApps() {
        return badApps;
    }

    public void setBadApps(List<String> badApps) {
        this.badApps = badApps;
    }

    public List<String> getNeutralApps() {
        return neutralApps;
    }

    public void setNeutralApps(List<String> neutralApps) {
        this.neutralApps = neutralApps;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        PersonalityDomain other = (PersonalityDomain) o;
        return Objects.equals(realName, other.realName) &&
                Objects.equals(knightName, other.knightName) &&
                Objects.equals(build, other.build) &&
                Objects.equals(goodApps, other.goodApps) &&
                Objects.equals(badApps, other.badApps) &&
                Objects.equals(neutralApps, other.neutralApps);
    }

}
