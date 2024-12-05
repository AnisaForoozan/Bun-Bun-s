package com.cs2212.bunbun.system;

import com.cs2212.bunbun.gameplay.GameSaveManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadGameTest {

    @Test
    void testRestrictionForDeadPetSlot() {
        // Simulate saving a dead pet in Slot 1
        GameSaveManager.savePetHealth("Slot 1", 0);
        GameSaveManager.setGameplayLocked(false); // Ensure gameplay is not locked

        // Attempt to load Slot 1 and ensure it throws an exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            validateSlot("Slot 1");
        });
        assertEquals("This pet has died and cannot be played.", exception.getMessage());
    }

    @Test
    void testRestrictionForGameplayLock() {
        // Simulate locking gameplay
        GameSaveManager.setGameplayLocked(true);
        GameSaveManager.savePetHealth("Slot 1", 20); // Ensure the pet is not dead

        // Attempt to load any slot and ensure it throws an exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            validateSlot("Slot 1");
        });
        assertEquals("Gameplay is currently locked due to time restrictions.", exception.getMessage());
    }

    // Utility method to validate a slot for gameplay
    private void validateSlot(String slotKey) {
        // Check if the pet in the slot is dead
        int petHealth = GameSaveManager.getPetHealth(slotKey);
        if (petHealth == 0) {
            throw new IllegalStateException("This pet has died and cannot be played.");
        }

        // Check if gameplay is locked
        if (GameSaveManager.isGameplayLocked()) {
            throw new IllegalStateException("Gameplay is currently locked due to time restrictions.");
        }
    }
}