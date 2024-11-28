package com.cs2212.bunbun.system;

import javax.swing.*;

public class GiftItem {
    private String name;    // Name of the gift item
    private int points;     // Points the gift item gives
    private ImageIcon image; // Image associated with the gift item

    // Constructor
    public GiftItem(String name, int points, ImageIcon image) {
        this.name = name;
        this.points = points;
        this.image = image;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public ImageIcon getImage() {
        return image;
    }

    // Setters (if needed)
    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }
}

