package com.cs2212.bunbun.gameplay;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameSaveManagerTest {

    @Test
    void testSavePetNameAndType() {
        GameSaveManager.saveData("Slot 1", "Fluffy", "Brown Bunny");
        assertEquals("Fluffy:Brown Bunny", GameSaveManager.loadSaveData().get("Slot 1"));
    }

    @Test
    void testSavePetStatistics() {
        GameSaveManager.saveStat("Slot 1_health", 100);
        assertEquals(100, GameSaveManager.getPetHealth("Slot 1"));
    }

    @Test
    void testSaveAudioSettings() {
        GameSaveManager.saveVolumeSetting("master_volume", -20.0f);
        assertEquals(-20.0f, GameSaveManager.loadVolumeSetting("master_volume"));
    }

    @Test
    void testSaveTimeLimits() {
        GameSaveManager.saveTimeLimits(Map.of("Monday", 120));
        assertEquals(120, GameSaveManager.getTimeLimits().get("Monday"));
    }

    @Test
    void testSavePlaytimeData() {
        GameSaveManager.addPlaytime(60);
        assertTrue(GameSaveManager.getTotalPlayTimeInMinutes() >= 60);
    }
}