package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inventory extends JPanel implements ActionListener {
    // Instance variables

    private DefaultListModel<Item> foodListModel; // Model for food items
    private DefaultListModel<Item> giftListModel; // Model for gift items
    private JList<Item> foodList; // JList for food items
    private JList<Item> giftList; // JList for gift items
    private JLabel capacityLabel; // Label to show inventory capacityg

    private int totalItems; // Track total items in inventory
    private static final int MAX_CAPACITY = 50; // Maximum inventory capacity

    //Button declarations
    private JButton confirmButton;
    JButton exit_button;
    JButton inventory_button;
    JButton shop_button;

    // Constructor
    public Inventory() {
        totalItems = 0; // Initialize total items

        // Create "Inventory" button
        inventory_button = new JButton("Inventory");
        inventory_button.setHorizontalAlignment(JLabel.CENTER);
        inventory_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
        inventory_button.setBounds(290, 0, 100, 30);

        // Create "Shop" button
        shop_button = new JButton("Shop");
        shop_button.setHorizontalAlignment(JLabel.CENTER);
        shop_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
        shop_button.setBounds(430, 0, 100, 30);

        capacityLabel = new JLabel("Inventory Capacity: 0/" + MAX_CAPACITY);
        capacityLabel.setBounds(10, 10, 100, 30);

        // Create "Food" label
        JLabel food_label = new JLabel("Food");
        food_label.setHorizontalAlignment(JLabel.LEFT);
        food_label.setBounds(20, 30, 100, 100);

        // Initialize the food list
        foodListModel = new DefaultListModel<>();
        foodList = new JList<>(foodListModel);
        foodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set the layout to FlowLayout to display items horizontally
        foodList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        foodList.setVisibleRowCount(1); // Only show one row at a time horizontally
        foodList.setBounds(20, 100, 750, 50);

        // Create new separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBounds(0, 90, 800, 10);

        // Create "Gifts" label
        JLabel gifts_label = new JLabel("Gifts");
        gifts_label.setHorizontalAlignment(JLabel.LEFT);
        gifts_label.setBounds(20, 205, 100, 100);

        // Create new separator
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator1.setBounds(0, 266, 800, 10);

        // Initialize the gift list
        giftListModel = new DefaultListModel<>();
        giftList = new JList<>(giftListModel);
        giftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set the layout to FlowLayout to display items horizontally
        giftList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        giftList.setVisibleRowCount(1); // Only show one row at a time horizontally
        giftList.setBounds(20, 300, 750, 50);

        // Create confirm button
        confirmButton = new JButton("Confirm");
        confirmButton.setBounds(420, 390, 105, 40);
        confirmButton.addActionListener(this); // Add action listener for the button

        // Create exit button
        exit_button = new JButton("Exit");
        exit_button.setBounds(300, 390, 105, 40);
        exit_button.addActionListener(this); // add action listener for the button

        // Add components to the panel
        this.setLayout(null);
        this.setPreferredSize(new Dimension(700, 400)); //FIXME: make the panel a bit smaller?
        this.add(inventory_button); // Add inventory button
        this.add(shop_button); // Add shop button
        this.add(capacityLabel);
        this.add(food_label); // Add food label
        this.add(foodList);
        this.add(separator); // Add separator
        this.add(gifts_label); // Add gifts label
        this.add(giftList);
        this.add(separator1); // Add separator
        this.add(confirmButton); // Add confirm button
        this.add(exit_button); // Add exit button

        // Sample food items for demonstration
        //addFoodItem(new FoodItem("Apple", 25, new ImageIcon("apple.png")));
    }

    // Method to add a food item
    public boolean addFoodItem(String name, int points, ImageIcon image) {
        if (totalItems >= MAX_CAPACITY) {
            JOptionPane.showMessageDialog(this, "Inventory is full!");
            return false;
        }

        addItem(foodListModel, name, points, image);
        return true;
    }

    // Method to add a gift item
    public boolean addGiftItem(String name, int points, ImageIcon image) {
        if (totalItems >= MAX_CAPACITY) {
            JOptionPane.showMessageDialog(this, "Inventory is full!");
            return false;
        }

        addItem(giftListModel, name, points, image);
        return true;
    }

    // Helper method to add an item to a list
    private void addItem(DefaultListModel<Item> listModel, String name, int points, ImageIcon image) {
        // Check if the item already exists
        for (int i = 0; i < listModel.size(); i++) {
            Item item = listModel.getElementAt(i);
            if (item.getName().equals(name)) {
                item.incrementQuantity();
                listModel.set(i, item); // Refresh the item in the list
                updateCapacity();
                return;
            }
        }

        // Add new item if it doesn't exist
        listModel.addElement(new Item(name, points, image));
        totalItems++;
        updateCapacity();
    }

    // Update inventory capacity label
    private void updateCapacity() {
        capacityLabel.setText("Inventory Capacity: " + totalItems + "/" + MAX_CAPACITY);
    }

    //actions for buttons
    @Override
    public void actionPerformed(ActionEvent e) {
        //exit button
        if (e.getSource() == exit_button) {
            // Exit action
            JOptionPane.showMessageDialog(this, "Exiting Inventory...");
            System.exit(0); // Close the application
        }
        //confirm button
        if (e.getSource() == confirmButton) {
            // Check if an item is selected in foodList or giftList
            Item selectedFood = foodList.getSelectedValue();
            Item selectedGift = giftList.getSelectedValue();

            if (selectedFood != null) {
                useItem(selectedFood, foodListModel);
            } else if (selectedGift != null) {
                useItem(selectedGift, giftListModel);
            } else {
                JOptionPane.showMessageDialog(this, "No item selected!");
            }
        }
    }

    // Use an item: decrement quantity or remove from list
    private void useItem(Item item, DefaultListModel<Item> listModel) {
        if (item.getQuantity() > 1) {
            item.decrementQuantity();
            listModel.setElementAt(item, listModel.indexOf(item)); // Refresh the item in the list
        } else {
            listModel.removeElement(item); // Remove item completely
            totalItems--;
        }
        JOptionPane.showMessageDialog(this, "Used: " + item.getName() + " (+" + item.getPoints() + " points)");
        updateCapacity();
    }

    // Test the Inventory class
    public static void main(String[] args) {
        JFrame frame = new JFrame("Inventory Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        Inventory inventoryPanel = new Inventory();
        frame.add(inventoryPanel);

        // Add some test items
        inventoryPanel.addFoodItem("Apple", 25, new ImageIcon("apple.png"));
        inventoryPanel.addFoodItem("Apple", 25, new ImageIcon("apple.png")); // Add duplicate to test quantity

        frame.setVisible(true);
    }

}