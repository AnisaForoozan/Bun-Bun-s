package com.cs2212.bunbun.gameplay;

import java.util.HashMap;
import java.util.Map;

public class GameSave {
    private Map<String, String> slots = new HashMap<>(); // Stores "Slot X" -> "petName:petType"

    public Map<String, String> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, String> slots) {
        this.slots = slots;
    }

    public void saveSlot(String slot, String petName, String petType) {
        slots.put(slot, petName + ":" + petType);
    }

    public String getPetName(String slot) {
        if (slots.containsKey(slot)) {
            String[] data = slots.get(slot).split(":");
            return data[0]; // Returns the petName
        }
        return null;
    }

    public String getPetType(String slot) {
        if (slots.containsKey(slot)) {
            String[] data = slots.get(slot).split(":");
            return data.length > 1 ? data[1] : null; // Returns the petType if available
        }
        return null;
    }
}
