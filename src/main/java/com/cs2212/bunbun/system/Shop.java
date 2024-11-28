package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Shop extends JPanel implements ActionListener {
    // Instance variables
    private JList<String> foodList; // List for food items
    private JList<String> giftList; // List for gift items
    private JButton purchaseButton; // Button to purchase item

    //Button declarations
    JButton exit_button;
    JButton inventory_button;
    JButton shop_button;

    // Constructor
    public Shop() {

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

        // Create "Food" label
        JLabel food_label = new JLabel("Food");
        food_label.setHorizontalAlignment(JLabel.LEFT);
        food_label.setBounds(20, 30, 100, 100);

        // Create list of food items
        String[] foodItems = {"Apple", "Bread", "Cake", "Apple", "Bread", "Cake", "Apple", "Bread", "Cake"}; // Example food items
        foodList = new JList<>(foodItems); //FIXME: update with actual items we'll use
        foodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        foodList.setLayoutOrientation(JList.HORIZONTAL_WRAP); // Set horizontal layout
        foodList.setVisibleRowCount(1); // Show all items in a single row

        JScrollPane foodScrollPane = new JScrollPane(foodList); // Wrap in a JScrollPane for better layout
        foodScrollPane.setBounds(20, 100, 750, 50); // Adjust bounds to fit the content

        // Create new separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBounds(0, 90, 800, 10);

        // Create "Gifts" label
        JLabel gifts_label = new JLabel("Gifts");
        gifts_label.setHorizontalAlignment(JLabel.LEFT);
        gifts_label.setBounds(20, 205, 100, 100);

        // Create list of gift items (horizontal)
        String[] giftItems = {"Teddy Bear", "Flowers", "Chocolate"}; // Example gift items
        giftList = new JList<>(giftItems); // Create JList for gift items
        giftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        giftList.setLayoutOrientation(JList.HORIZONTAL_WRAP); // Set horizontal layout
        giftList.setVisibleRowCount(1); // Show all items in a single row

        JScrollPane giftScrollPane = new JScrollPane(giftList); // Wrap in a JScrollPane for better layout
        giftScrollPane.setBounds(20, 280, 750, 50); // Adjust bounds to fit the content

        // Create new separator
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator1.setBounds(0, 266, 800, 10);

        purchaseButton = new JButton("Purchase");
        purchaseButton.setBounds(420, 390, 105, 40);
        purchaseButton.addActionListener(this); // Add action listener for the button

        // Create "Exit" button
        exit_button = new JButton("Exit");
        exit_button.setBounds(300, 390, 105, 40);
        exit_button.addActionListener(this); // add action listener for the button

        // Add components to the panel
        this.setLayout(null);
        this.setPreferredSize(new Dimension(700, 400)); //FIXME: make the panel a bit smaller?
        this.add(inventory_button); // Add inventory button
        this.add(shop_button); // Add shop button
        this.add(food_label); // Add food label
        this.add(foodScrollPane); // Add food scroll pane
        this.add(separator); // Add separator
        this.add(gifts_label); // Add gifts label
        this.add(giftScrollPane); // Add gift list
        this.add(separator1); // Add separator
        this.add(purchaseButton); // Add purchase button
        this.add(exit_button); // Add exit button
    }

    // Action performed method
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == purchaseButton) {
            // Check which item is selected
            String selectedFood = foodList.getSelectedValue();
            String selectedGift = giftList.getSelectedValue();

            if (selectedFood != null) {
                // Show confirmation for food item
                JOptionPane.showMessageDialog(this, "You selected: " + selectedFood, "Item Selected", JOptionPane.INFORMATION_MESSAGE);
            } else if (selectedGift != null) {
                // Show confirmation for gift item
                JOptionPane.showMessageDialog(this, "You selected: " + selectedGift, "Item Selected", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // No item selected
                JOptionPane.showMessageDialog(this, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == exit_button) {
            // Exit action
            JOptionPane.showMessageDialog(this, "Exiting Inventory...");
            System.exit(0); // Close the application
        }
    }

    // Test the panel in a JFrame
    public static void main(String[] args) {
        // Create and display the JFrame
        JFrame frame = new JFrame("Shop Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        // Create an instance of Shop and add it to the frame
        Shop ShopPanel = new Shop();
        frame.add(ShopPanel); // Add Shop panel to the frame

        // Make the frame visible
        frame.setVisible(true); // Make the frame visible
    }
}