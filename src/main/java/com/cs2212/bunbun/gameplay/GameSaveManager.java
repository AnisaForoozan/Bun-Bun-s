package com.cs2212.bunbun.gameplay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameSaveManager {

    private static final String SAVE_FILE = "saves/game_save.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
}
