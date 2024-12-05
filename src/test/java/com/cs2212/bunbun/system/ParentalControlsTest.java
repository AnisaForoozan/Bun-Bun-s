package com.cs2212.bunbun.system;

import com.cs2212.bunbun.gameplay.GameSaveManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParentalControlsTest {

    @BeforeEach
    void setUp() {
        // Clear any existing data to ensure test isolation
        GameSaveManager.saveUpdatedData(Map.of());
    }

    @Test
    void testPasswordCreationAndValidation() {
        // Save a password using saveUpdatedData
        Map<String, String> saveData = GameSaveManager.loadSaveData();
        saveData.put("parental_password", "testPassword");
        GameSaveManager.saveUpdatedData(saveData);

        // Retrieve the saved password
        String savedPassword = GameSaveManager.loadSaveData().get("parental_password");

        // Assert the saved password matches the expected value
        assertEquals("testPassword", savedPassword);

        // Validate correct and incorrect passwords
        assertEquals("testPassword", savedPassword); // Correct password
        assertNotEquals("wrongPassword", savedPassword); // Incorrect password
    }


    @Test
    void testRevivingDeadPet() {
        // Set the health of a pet to 0 (dead state)
        GameSaveManager.savePetHealth("Slot 1", 0);

        // Revive the pet by restoring health to maximum
        GameSaveManager.savePetHealth("Slot 1", GameSaveManager.getMaxHealth());

        // Assert that the pet's health is now greater than 0
        assertEquals(GameSaveManager.getMaxHealth(), GameSaveManager.getPetHealth("Slot 1"));
    }

    @Test
    void testTimeLimitValidation() {
        // Save time limits for a specific day
        GameSaveManager.saveTimeLimits(Map.of("Monday", 120));

        // Retrieve the saved time limits
        int timeLimit = GameSaveManager.getTimeLimits().get("Monday");

        // Assert the time limit is correctly saved and within valid boundaries
        assertEquals(120, timeLimit); // Check saved value
        assertTrue(timeLimit > 0 && timeLimit <= 1440); // Check time is within 24 hours
    }

    @Test
    void testTotalPlayTimeReset() {
        // Add playtime
        GameSaveManager.addPlaytime(120); // 2 hours
        GameSaveManager.resetTotalPlayTime();

        // Assert total playtime is reset to 0
        assertEquals(0, GameSaveManager.getTotalPlayTimeInMinutes());
    }

    @Test
    void testGameplayLockToggle() {
        // Lock gameplay
        GameSaveManager.setGameplayLocked(true);
        assertTrue(GameSaveManager.isGameplayLocked());

        // Unlock gameplay
        GameSaveManager.setGameplayLocked(false);
        assertFalse(GameSaveManager.isGameplayLocked());
    }
}
