package com.kiwi.personality;

import com.kiwi.features.personality.data.*;

import java.util.List;

public class PersonalityTestFactory {

    public static Personality validPersonality() {
        return new Personality(
                userNameRealDTO().getName(),
                userNameKnightDTO().getName(),
                buildDTO().getBuild(),
                appsDTO().getGoodApps(),
                appsDTO().getBadApps()
        );
    }

    public static PersonalityDTO validPersonalityDTO() {
        return validPersonality().toDTO();
    }

    public static UserNameDTO userNameRealDTO() {
        return new UserNameDTO("Finn");
    }

    public static UserNameDTO userNameKnightDTO() {
        return new UserNameDTO("Human");
    }

    public static BuildDTO buildDTO() {
        return new BuildDTO("BERSERKER");
    }

    public static AppsDTO appsDTO() {
        return new AppsDTO(List.of("GrowTale"), List.of("X"));
    }
}
