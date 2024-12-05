package com.cs2212.bunbun.gameplay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code GameSaveManager} class handles saving and loading game data,
 * such as playtime, session counts, pet information, time limits, and volume settings.
 * It provides static methods to interact with the save data stored in a JSON file.
 * @author Janreve Salubre
 * @version 1.0
 * @since 1.0
 */
public class GameSaveManager {

    /** Total playtime in minutes. */
    private static int totalPlayTime = 0; // Total playtime in minutes

    /** Number of sessions played. */
    private static int sessionCount = 0;

    /** Start time of the current session in milliseconds. */
    private static long sessionStartTime = System.currentTimeMillis();

    /** File path to the save file. */
    private static final String SAVE_FILE = "saves/game_save.json";

    /** ObjectMapper instance for JSON serialization/deserialization. */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** Maximum health value for the pet. */
    private static final int MAX_HEALTH = 20;

    static {
        // Load playtime and session count at startup
        Map<String, String> saveData = loadSaveData();
        totalPlayTime = Integer.parseInt(saveData.getOrDefault("total_play_time", "0"));
        sessionCount = Integer.parseInt(saveData.getOrDefault("session_count", "0"));
        sessionStartTime = System.currentTimeMillis();
    }

    /**
     * Loads save data from the JSON file.
     *
     * @return A map containing the save data.
     */
    public static Map<String, String> loadSaveData() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return new HashMap<>(); // Return empty save data if file doesn't exist
        }

        try {
            return objectMapper.readValue(file, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Saves pet data to the JSON file.
     *
     * @param slot        The save slot identifier.
     * @param bunnyName   The name of the bunny.
     * @param selectedPet The type of the selected pet.
     */
    public static void saveData(String slot, String bunnyName, String selectedPet) {
        Map<String, String> saveData = loadSaveData();
        saveData.put(slot, bunnyName + ":" + selectedPet); // Save in the "name:type" format

        saveUpdatedData(saveData); // Delegate saving logic to saveUpdatedData
    }

    /**
     * Saves the updated save data map to the JSON file.
     *
     * @param saveData The map containing save data to be written.
     */
    public static void saveUpdatedData(Map<String, String> saveData) {
        File saveFile = new File(SAVE_FILE);
        saveFile.getParentFile().mkdirs(); // Ensure directories exist

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(saveFile, saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the pet's name from the save data.
     *
     * @param slotKey The key identifying the save slot.
     * @return The pet's name, or {@code null} if not found.
     */
    public static String getPetName(String slotKey) {
        Map<String, String> saveData = loadSaveData();
        String petData = saveData.get(slotKey);
        if (petData != null && petData.contains(":")) {
            return petData.split(":")[0]; // Extract the name before the ":"
        }
        return null; // Return null if not found or invalid format
    }

    /**
     * Retrieves the pet's type from the save data.
     *
     * @param slotKey The key identifying the save slot.
     * @return The pet's type, or {@code null} if not found.
     */
    public static String getPetType(String slotKey) {
        Map<String, String> saveData = loadSaveData();
        String petData = saveData.get(slotKey);
        if (petData != null && petData.contains(":")) {
            return petData.split(":")[1]; // Extract the type after the ":"
        }
        return null; // Return null if not found or invalid format
    }

    /**
     * Saves time limits for various days.
     *
     * @param timeLimits A map containing time limits with days as keys.
     */
    public static void saveTimeLimits(Map<String, Integer> timeLimits) {
        try {
            // Normalize keys to title case
            Map<String, Integer> normalizedTimeLimits = new HashMap<>();
            for (Map.Entry<String, Integer> entry : timeLimits.entrySet()) {
                String normalizedKey = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1).toLowerCase();
                normalizedTimeLimits.put(normalizedKey, entry.getValue());
            }

            Map<String, String> saveData = loadSaveData();
            saveData.put("time_limits", objectMapper.writeValueAsString(normalizedTimeLimits)); // Save normalized keys
            saveUpdatedData(saveData);

            System.out.println("Time Limits Saved to File: " + normalizedTimeLimits);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the time limits from the save data.
     *
     * @return A map containing time limits with days as keys.
     */
    public static Map<String, Integer> getTimeLimits() {
        Map<String, String> saveData = loadSaveData();
        String data = saveData.get("time_limits");
        if (data != null) {
            try {
                Map<String, Integer> timeLimits = new ObjectMapper().readValue(data, new TypeReference<Map<String, Integer>>() {});

                // Normalize keys to title case
                Map<String, Integer> normalizedTimeLimits = new HashMap<>();
                for (Map.Entry<String, Integer> entry : timeLimits.entrySet()) {
                    String normalizedKey = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1).toLowerCase();
                    normalizedTimeLimits.put(normalizedKey, entry.getValue());
                }

                return normalizedTimeLimits;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>(); // Return empty map if no time limits are set
    }

    /**
     * Sets whether the gameplay is locked or not.
     *
     * @param locked {@code true} to lock the gameplay; {@code false} otherwise.
     */
    public static void setGameplayLocked(boolean locked) {
        Map<String, String> saveData = loadSaveData();
        saveData.put("gameplay_locked", locked ? "true" : "false");
        saveUpdatedData(saveData);
    }

    /**
     * Checks if the gameplay is currently locked.
     *
     * @return {@code true} if gameplay is locked; {@code false} otherwise.
     */
    public static boolean isGameplayLocked() {
        Map<String, String> saveData = loadSaveData();
        return Boolean.parseBoolean(saveData.getOrDefault("gameplay_locked", "false"));
    }

    /**
     * Resets the time limit for the current day.
     */
    public static void resetTimeLimitForToday() {
        Map<String, Integer> timeLimits = getTimeLimits();
        String todayKey = LocalDate.now().getDayOfWeek().toString();
        timeLimits.put(todayKey, 0); // Reset timer for the current day
        saveTimeLimits(timeLimits); // Use saveTimeLimits to save the updated map
    }

    /**
     * Saves playtime and session count data.
     */
    public static void savePlaytimeData() {
        Map<String, String> saveData = loadSaveData();
        saveData.put("total_play_time", String.valueOf(totalPlayTime));
        saveData.put("session_count", String.valueOf(sessionCount));
        saveUpdatedData(saveData);
        System.out.println("Saving playtime data: " + totalPlayTime + " minutes, " + sessionCount + " sessions");

    }

    /**
     * Adds playtime in minutes and increments session count.
     *
     * @param minutes The number of minutes to add to the total playtime.
     */
    public static void addPlaytime(int minutes) {
        totalPlayTime += minutes;
        sessionCount++;
        System.out.println("Adding playtime: " + minutes + " minutes, Total: " + totalPlayTime + " minutes, Sessions: " + sessionCount);
        savePlaytimeData();
    }

    /**
     * Resets the total playtime to zero.
     */
    public static void resetTotalPlayTime() {
        totalPlayTime = 0;
        savePlaytimeData(); // Save updated values
    }

    /**
     * Resets the session count to zero.
     */
    public static void resetAveragePlayTime() {
        sessionCount = 0;
        savePlaytimeData(); // Save updated values
    }

    /**
     * Enables or disables time restriction.
     *
     * @param enabled {@code true} to enable; {@code false} to disable.
     */
    public static void setTimeRestrictionEnabled(boolean enabled) {
        Map<String, String> saveData = loadSaveData();
        saveData.put("time_restriction_enabled", String.valueOf(enabled));
        saveUpdatedData(saveData);
    }

    /**
     * Checks if time restriction is enabled.
     *
     * @return {@code true} if enabled; {@code false} otherwise.
     */
    public static boolean isTimeRestrictionEnabled() {
        Map<String, String> saveData = loadSaveData();
        return Boolean.parseBoolean(saveData.getOrDefault("time_restriction_enabled", "false"));
    }

    /**
     * Retrieves the total playtime in minutes.
     *
     * @return Total playtime in minutes.
     */
    public static int getTotalPlayTimeInMinutes() {
        return totalPlayTime;
    }

    /**
     * Retrieves the number of sessions played.
     *
     * @return The session count.
     */
    public static int getSessionCount() {
        return sessionCount;
    }

    /**
     * Saves the duration of the current session.
     */
    public static void saveSessionDuration() {
        System.out.println("saveSessionDuration called...");
        long sessionEndTime = System.currentTimeMillis();
        int sessionMinutes = (int) ((sessionEndTime - sessionStartTime) / (1000 * 60)); // Convert milliseconds to minutes
        addPlaytime(sessionMinutes);
        System.out.println("Session duration saved: " + sessionMinutes + " minutes");
    }

    /**
     * Saves a volume setting.
     *
     * @param key   The key identifying the volume setting.
     * @param value The volume value to save.
     */
    public static void saveVolumeSetting(String key, float value) {
        Map<String, String> saveData = loadSaveData();
        saveData.put(key, String.valueOf(value));
        saveUpdatedData(saveData);
    }

    /**
     * Loads a volume setting.
     *
     * @param key The key identifying the volume setting.
     * @return The volume value.
     */
    public static float loadVolumeSetting(String key) {
        Map<String, String> saveData = loadSaveData();
        return Float.parseFloat(saveData.getOrDefault(key, "0")); // Default to muted if not found
    }

    /**
     * Retrieves the maximum health value for the pet.
     *
     * @return The maximum health value.
     */
    public static int getMaxHealth() {
        return MAX_HEALTH;
    }

    /**
     * Retrieves the pet's health from the save data.
     *
     * @param slotKey The key identifying the save slot.
     * @return The pet's health value.
     */
    public static int getPetHealth(String slotKey) {
        Map<String, String> saveData = loadSaveData();
        try {
            return Integer.parseInt(saveData.getOrDefault(slotKey + "_health", "20")); // Default health is 20
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 20; // Default to 20 if invalid
        }
    }

    /**
     * Saves the pet's health to the save data.
     *
     * @param slotKey The key identifying the save slot.
     * @param health  The health value to save.
     */
    public static void savePetHealth(String slotKey, int health) {
        Map<String, String> saveData = loadSaveData();
        saveData.put(slotKey + "_health", String.valueOf(health));
        saveUpdatedData(saveData);
    }

    /**
     * Retrieves a saved stat value.
     *
     * @param key          The key identifying the stat.
     * @param defaultValue The default value to return if the stat is not found.
     * @return The stat value.
     */
    public static int getStat(String key, int defaultValue) {
        Map<String, String> saveData = loadSaveData();
        try {
            return Integer.parseInt(saveData.getOrDefault(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue; // Return default if parsing fails
        }
    }

    /**
     * Saves an individual stat.
     *
     * @param key   The key identifying the stat.
     * @param value The value to save.
     */
    public static void saveStat(String key, int value) {
        Map<String, String> saveData = loadSaveData();
        saveData.put(key, String.valueOf(value));
        saveUpdatedData(saveData);
    }

}