package com.kiwi.features.personality;

public class PersonalityDTO {
    private String realName;
    private String knightName;
    private String build;

    public PersonalityDTO(String realName, String knightName, String build) {
        this.realName = realName;
        this.knightName = knightName;
        this.build = build;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String name) {
        this.realName = name;
    }

    public String getKnightName() { return knightName; }

    public void setKnightName(String name) { this.knightName = name; }

    public String getBuild() { return build; }

    public void setBuild(String build) { this.build = build; }
}
