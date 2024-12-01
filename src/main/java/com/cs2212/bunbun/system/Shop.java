package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.cs2212.bunbun.system.Inventory.MAX_CAPACITY;
import static com.cs2212.bunbun.system.Inventory.totalItems;

public class Shop extends JPanel implements ActionListener {
    private CustomListDemo customListDemo;
    private Inventory inventoryPanel; // The inventory panel
    private Shop shopPanel; // The shop panel
    private JPanel cardPanel; // The CardLayout panel
    private JList<String> foodList; // List for food items
    private JList<String> giftList; // List for gift items
    private JButton purchaseButton; // Button to purchase item

    //Button declarations
    JButton exit_button;

    // Constructor
    public Shop(CustomListDemo customListDemo, Inventory inventoryPanel) {
        // Ensure this is initialized somewhere in your Shop class constructor or method
        this.customListDemo = customListDemo;
        this.inventoryPanel = inventoryPanel;

        // Create "Food" label
        JLabel food_label = new JLabel("Food");
        food_label.setHorizontalAlignment(JLabel.LEFT);
        food_label.setBounds(20, 30, 100, 100);

        // Create list of food items
//        String[] foodItems = {"Apple", "Bread", "Cake", "Apple", "Bread", "Cake", "Apple", "Bread", "Cake"}; // Example food items
//        foodList = new JList<>(foodItems); //FIXME: update with actual items we'll use
//        foodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        foodList.setLayoutOrientation(JList.HORIZONTAL_WRAP); // Set horizontal layout
//        foodList.setVisibleRowCount(1); // Show all items in a single row
//
//        JScrollPane foodScrollPane = new JScrollPane(foodList); // Wrap in a JScrollPane for better layout
//        foodScrollPane.setBounds(20, 100, 750, 50); // Adjust bounds to fit the content

        CustomListDemo foodList = customListDemo; // Use the existing CustomListDemo instance
        foodList.setBounds(20, 100, 750, 100);

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
        purchaseButton.setBounds(420, 370, 105, 40);
        purchaseButton.addActionListener(this); // Add action listener for the button

        // Create "Exit" button
        exit_button = new JButton("Exit");
        exit_button.setBounds(300, 370, 105, 40);
        exit_button.addActionListener(this); // add action listener for the button

        // Add components to the panel
        this.setLayout(null);
        this.setPreferredSize(new Dimension(700, 400)); //FIXME: make the panel a bit smaller?
        this.add(food_label); // Add food label
        this.add(foodList); // Add food scroll pane
        this.add(separator); // Add separator
        this.add(gifts_label); // Add gifts label
        this.add(giftScrollPane); // Add gift list
        this.add(separator1); // Add separator
        this.add(purchaseButton); // Add purchase button
        this.add(exit_button); // Add exit button

    }

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == purchaseButton) {
//            if (customListDemo != null) {
//                String selectedFood = customListDemo.isSelected();  // Corrected method name
//                System.out.println("Selected Food: " + selectedFood);  // Debugging line
//
//                if (selectedFood != null) {
//                    Item foodItem = new Item(selectedFood, 25, null);  // Adjust points value as needed
//                    if (!inventoryPanel.addFoodItem(foodItem)) {
//                        JOptionPane.showMessageDialog(this, "You purchased: " + selectedFood, "Purchase Success", JOptionPane.INFORMATION_MESSAGE);
//                    } else {
//                        JOptionPane.showMessageDialog(this, "You purchased: " + selectedFood, "Purchase Success", JOptionPane.INFORMATION_MESSAGE);
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(this, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "CustomListDemo is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//
////            } else if (selectedGift != null) {
////                // Add gift item to inventory
////                Item giftItem = new Item(selectedGift, 10, null);  // Replace `10` with the appropriate points value
////                if (!inventoryPanel.addGiftItem(giftItem)) {
////                    // If the item already exists, it will increase the quantity automatically
////                    JOptionPane.showMessageDialog(this, "You purchased: " + selectedGift, "Item Purchased", JOptionPane.INFORMATION_MESSAGE);
////                } else {
////                    JOptionPane.showMessageDialog(this, "You purchased: " + selectedGift, "Item Purchased", JOptionPane.INFORMATION_MESSAGE);
////                }
////            } else {
////                // No item selected
////                JOptionPane.showMessageDialog(this, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
////            }
//        }
//        else if (e.getSource() == exit_button) {
//            // Exit action
//            JOptionPane.showMessageDialog(this, "Exiting Inventory...");
//            System.exit(0); // Close the application
//        }
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == purchaseButton) {
            if (totalItems == MAX_CAPACITY) {
                JOptionPane.showMessageDialog(this, "Inventory is full! Use items to free up space", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else if (customListDemo != null) {
                String selectedFood = customListDemo.isSelected();  // Call isSelected() directly on customListDemo
                System.out.println("Selected Food: " + selectedFood);  // Debugging line

                if (selectedFood != null) {
                    Item foodItem = new Item(selectedFood, 25, null);  // Adjust points value as needed
                    if (!inventoryPanel.addFoodItem(foodItem)) {
                        JOptionPane.showMessageDialog(this, "You purchased: " + selectedFood, "Purchase Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "You purchased: " + selectedFood, "Purchase Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "CustomListDemo is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (e.getSource() == exit_button) {
            // Exit action
            JOptionPane.showMessageDialog(this, "Exiting Inventory...");
            System.exit(0); // Close the application
        }
    }

//    // Test the panel in a JFrame
//    public static void main(String[] args) {
//        // Create and display the JFrame
//        JFrame frame = new JFrame("Shop Panel");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 500);
//
//        // Create an instance of Shop and add it to the frame
//        Shop ShopPanel = new Shop(new Inventory());
//        frame.add(ShopPanel); // Add Shop panel to the frame
//
//        // Make the frame visible
//        frame.setVisible(true); // Make the frame visible
//    }
}
