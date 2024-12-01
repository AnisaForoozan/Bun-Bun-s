package com.cs2212.bunbun.gameplay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class GameSaveManager {

    private static int totalPlayTime = 0; // Total playtime in minutes
    private static int sessionCount = 0;
    private static long sessionStartTime = System.currentTimeMillis();
    private static final String SAVE_FILE = "saves/game_save.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Load playtime and session count at startup
        Map<String, String> saveData = loadSaveData();
        totalPlayTime = Integer.parseInt(saveData.getOrDefault("total_play_time", "0"));
        sessionCount = Integer.parseInt(saveData.getOrDefault("session_count", "0"));
        sessionStartTime = System.currentTimeMillis();
    }

    // Load save data from the JSON file
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

    // Save data to the JSON file
    public static void saveData(String slot, String bunnyName, String selectedPet) {
        Map<String, String> saveData = loadSaveData();
        saveData.put(slot, bunnyName + ":" + selectedPet); // Save in the "name:type" format

        saveUpdatedData(saveData); // Delegate saving logic to saveUpdatedData
    }

    // Save the updated save data map to the JSON file
    public static void saveUpdatedData(Map<String, String> saveData) {
        File saveFile = new File(SAVE_FILE);
        saveFile.getParentFile().mkdirs(); // Ensure directories exist

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(saveFile, saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Utility method to get pet name
    public static String getPetName(String slotKey) {
        Map<String, String> saveData = loadSaveData();
        String petData = saveData.get(slotKey);
        if (petData != null && petData.contains(":")) {
            return petData.split(":")[0]; // Extract the name before the ":"
        }
        return null; // Return null if not found or invalid format
    }

    // Utility method to get pet type
    public static String getPetType(String slotKey) {
        Map<String, String> saveData = loadSaveData();
        String petData = saveData.get(slotKey);
        if (petData != null && petData.contains(":")) {
            return petData.split(":")[1]; // Extract the type after the ":"
        }
        return null; // Return null if not found or invalid format
    }

    // Add the time limit saving and loading methods here
    public static void saveTimeLimits(Map<String, Integer> timeLimits) {
        try {
            Map<String, String> saveData = loadSaveData();
            saveData.put("time_limits", objectMapper.writeValueAsString(timeLimits)); // Save as a JSON string
            saveUpdatedData(saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> getTimeLimits() {
        Map<String, String> saveData = loadSaveData();
        String data = saveData.get("time_limits");
        if (data != null) {
            try {
                return new ObjectMapper().readValue(data, new TypeReference<Map<String, Integer>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>(); // Return empty map if no time limits are set
    }

    public static void setGameplayLocked(boolean locked) {
        Map<String, String> saveData = loadSaveData();
        saveData.put("gameplay_locked", locked ? "true" : "false");
        saveUpdatedData(saveData);
    }

    public static boolean isGameplayLocked() {
        Map<String, String> saveData = loadSaveData();
        return Boolean.parseBoolean(saveData.getOrDefault("gameplay_locked", "false"));
    }

    public static void resetTimeLimitForToday() {
        Map<String, Integer> timeLimits = getTimeLimits();
        String todayKey = LocalDate.now().getDayOfWeek().toString();
        timeLimits.put(todayKey, 0); // Reset timer for the current day
        saveTimeLimits(timeLimits); // Use saveTimeLimits to save the updated map
    }

    // Save playtime and session count
    public static void savePlaytimeData() {
        Map<String, String> saveData = loadSaveData();
        saveData.put("total_play_time", String.valueOf(totalPlayTime));
        saveData.put("session_count", String.valueOf(sessionCount));
        saveUpdatedData(saveData);
        System.out.println("Saving playtime data: " + totalPlayTime + " minutes, " + sessionCount + " sessions");

    }

    // Add playtime (in minutes)
    public static void addPlaytime(int minutes) {
        totalPlayTime += minutes;
        sessionCount++;
        System.out.println("Adding playtime: " + minutes + " minutes, Total: " + totalPlayTime + " minutes, Sessions: " + sessionCount);
        savePlaytimeData();
    }

    // Reset total playtime
    public static void resetTotalPlayTime() {
        totalPlayTime = 0;
        savePlaytimeData(); // Save updated values
    }

    // Reset session count
    public static void resetAveragePlayTime() {
        sessionCount = 0;
        savePlaytimeData(); // Save updated values
    }

    // Get total playtime in hours and minutes
    public static String getTotalPlayTime() {
        int hours = totalPlayTime / 60;
        int minutes = totalPlayTime % 60;
        return hours + " hrs " + minutes + " min";
    }

    // Get average playtime in hours and minutes
    public static String getAveragePlayTime() {
        if (sessionCount == 0) return "0 hrs 0 min";
        int avgMinutes = totalPlayTime / sessionCount;
        int hours = avgMinutes / 60;
        int minutes = avgMinutes % 60;
        return hours + " hrs " + minutes + " min";
    }


    public static int getTotalPlayTimeInMinutes() {
        return totalPlayTime;
    }

    public static int getSessionCount() {
        return sessionCount;
    }

    public static void saveSessionDuration() {
        System.out.println("saveSessionDuration called...");
        long sessionEndTime = System.currentTimeMillis();
        int sessionMinutes = (int) ((sessionEndTime - sessionStartTime) / (1000 * 60)); // Convert milliseconds to minutes
        addPlaytime(sessionMinutes);
        System.out.println("Session duration saved: " + sessionMinutes + " minutes");
    }






}
