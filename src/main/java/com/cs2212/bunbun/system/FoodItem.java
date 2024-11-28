package com.cs2212.bunbun.system;

import javax.swing.*;

public class FoodItem {
    private String name;      // Name of the food item
    private int energyPoints; // Energy points that the food provides
    private ImageIcon image;  // Image associated with the food item

    // Constructor
    public FoodItem(String name, int energyPoints, ImageIcon image) {
        this.name = name;
        this.energyPoints = energyPoints;
        this.image = image;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getEnergyPoints() {
        return energyPoints;
    }

    public ImageIcon getImage() {
        return image;
    }

    // Setters (if needed)
    public void setName(String name) {
        this.name = name;
    }

    public void setEnergyPoints(int energyPoints) {
        this.energyPoints = energyPoints;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }
}
