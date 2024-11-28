package com.cs2212.bunbun.gameplay;

import java.util.HashMap;
import java.util.Map;

public class GameSave {
    private Map<String, String> slots = new HashMap<>();

    public Map<String, String> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, String> slots) {
        this.slots = slots;
    }
}
