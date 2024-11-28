package com.cs2212.bunbun.gameplay;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GameSaveManager {

    private static final String SAVE_FILE = "saves/game_save.json";

    // Load save data from the JSON file
    public static Map<String, String> loadSaveData() {
        Map<String, String> saveData = new HashMap<>();
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return saveData; // Return empty save data if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String json = jsonBuilder.toString();

            // Parse JSON manually
            json = json.replace("{", "").replace("}", "").trim();
            if (!json.isEmpty()) {
                String[] entries = json.split(",");
                for (String entry : entries) {
                    String[] pair = entry.split(":");
                    String key = pair[0].trim().replace("\"", "");
                    String value = pair[1].trim().replace("\"", "");
                    saveData.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveData;
    }

    // Save data to the JSON file
    public static void saveData(String slot, String bunnyName) {
        Map<String, String> saveData = loadSaveData();
        saveData.put(slot, bunnyName);

        File saveFile = new File(SAVE_FILE);
        saveFile.getParentFile().mkdirs(); // Ensure directories exist

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            writer.write("{\n");
            int count = 0;
            for (Map.Entry<String, String> entry : saveData.entrySet()) {
                writer.write(String.format("    \"%s\": \"%s\"", entry.getKey(), entry.getValue()));
                count++;
                if (count < saveData.size()) {
                    writer.write(",\n");
                }
            }
            writer.write("\n}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
