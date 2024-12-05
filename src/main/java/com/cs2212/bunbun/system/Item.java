package com.cs2212.bunbun.system;

import javax.swing.*;

/**
 * Represents an item in the system with a name, points, quantity, and an image.
 * This class provides methods to retrieve item details and manage its quantity.
 *
 * @author      Anne Liu <aliu432@uwo.ca>
 * @version     1.0
 * @since       1.0
 */
public class Item {
    private String name;
    private int points;
    private int quantity; // Track quantity of the item
    private ImageIcon image;

    /**
     * Constructs an Item with the specified name, points, and image.
     * The default quantity is initialized to 1.
     *
     * @param name  the name of the item
     * @param points the number of points associated with the item
     * @param image  an {@link ImageIcon} representing the item's image
     */
    public Item(String name, int points, ImageIcon image) {
        this.name = name;
        this.points = points;
        this.image = image;
        this.quantity = 1; // Default quantity is 1
    }

    /**
     * Gets the name of the item.
     *
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the points associated with the item.
     *
     * @return the points of the item
     */
    public int getPoints() {
        return points;
    }

    /**
     * Gets the current quantity of the item.
     *
     * @return the quantity of the item
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Increments the quantity of the item by 1.
     */
    public void incrementQuantity() {
        quantity++;
    }


    /**
     * Decrements the quantity of the item by 1.
     */
    public void decrementQuantity() {
        quantity--;
    }

    /**
     * Gets the image of the item.
     *
     * @return an {@link ImageIcon} representing the item's image
     */
    public ImageIcon getImage() {
        return image;
    }

    /**
     * Returns a string representation of the item, including its name and quantity.
     *
     * @return a string in the format "name[quantity]".
     */
    @Override
    public String toString() {
        return name + "[" + quantity + "]";
    }

}