package com.kiwi.features.skills.exceptions;

import jakarta.validation.constraints.NotNull;

public class DeckSlotAlreadyOccupiedException extends RuntimeException {
    public DeckSlotAlreadyOccupiedException(@NotNull Long skillId, @NotNull int deckSlot) {
        super(String.format("Cannot equip skill with id %s because deck slot %s is already occupied", skillId, deckSlot));
    }
}
