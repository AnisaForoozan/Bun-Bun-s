package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class Item {
    private String name;
    private int points;
    private int quantity; // Track quantity of the item
    private ImageIcon image;

    public Item(String name, int points, ImageIcon image) {
        this.name = name;
        this.points = points;
        this.image = image;
        this.quantity = 1; // Default quantity is 1
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incrementQuantity() {
        quantity++;
    }

    public void decrementQuantity() {
        quantity--;
    }

    public ImageIcon getImage() {
        return image;
    }

    @Override
    public String toString() {
        return name + "[" + quantity + "]";
    }

}
