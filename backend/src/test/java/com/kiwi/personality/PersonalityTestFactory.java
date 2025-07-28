package com.kiwi.personality;

import com.kiwi.features.personality.BuildDTO;
import com.kiwi.features.personality.Personality;
import com.kiwi.features.personality.PersonalityDTO;
import com.kiwi.features.personality.UserNameDTO;

public class PersonalityTestFactory {

    public static Personality validPersonality() {
        return new Personality("Finn", "Human", "BERSERKER");
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
}
