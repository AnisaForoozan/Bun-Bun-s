package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.cs2212.bunbun.system.Inventory.MAX_CAPACITY;
import static com.cs2212.bunbun.system.Inventory.totalItems;

/**
 * Represents the shop panel where users can view and purchase food or gift items.
 * This class allows users to interact with lists of available food and gift items,
 * and make purchases by adding items to their inventory.
 * The panel includes buttons for purchasing items and exiting the shop.
 *
 * It utilizes CustomListDemo and CustomGiftList instances for displaying food and gift items
 * and allows users to interact with these lists to select and purchase items.
 * The purchase button ensures the inventory capacity is checked before allowing an item to be purchased.
 *
 * @author      Anne Liu <aliu432@uwo.ca>
 * @version     1.0
 * @since       1.0
 */
public class Shop extends JPanel implements ActionListener {
    private CustomListDemo customListDemo;
    private CustomGiftList customGiftList;
    private Inventory inventoryPanel; // The inventory panel
    private Shop shopPanel; // The shop panel
    private JPanel cardPanel; // The CardLayout panel
    private JList<String> foodList; // List for food items
    private JList<String> giftList; // List for gift items
    private JButton purchaseButton; // Button to purchase item

    JButton exit_button; //Button declarations

    /**
     * Constructs the Shop panel, initializing and setting up components
     * such as food and gift lists, labels, separators, and buttons.
     * This constructor configures the layout, adds components to the panel,
     * and establishes interactions between the shop, inventory, and item lists.
     *
     * @param customListDemo The list of food items.
     * @param customGiftList The list of gift items.
     * @param inventoryPanel The inventory panel to add purchased items to.
     */
    public Shop(CustomListDemo customListDemo, CustomGiftList customGiftList, Inventory inventoryPanel) {

        this.customListDemo = customListDemo;
        this.customGiftList = customGiftList;
        this.inventoryPanel = inventoryPanel;

        // Set the references between food and gift lists
        this.customListDemo.setGiftList(this.customGiftList.petList);
        this.customGiftList.setFoodList(this.customListDemo.petList);

        // Create "Food" label
        JLabel food_label = new JLabel("Food");
        food_label.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        food_label.setHorizontalAlignment(JLabel.LEFT);
        food_label.setBounds(20, 10, 100, 100);

        // Create new separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBounds(0, 90, 800, 10);

        // Create list of food items
        CustomListDemo foodList = customListDemo; // Use the existing CustomListDemo instance
        foodList.setBackground(new Color(193, 154, 107));
        foodList.setBounds(20, 60, 750, 90);

        // Create "Gifts" label
        JLabel gifts_label = new JLabel("Gifts");
        gifts_label.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gifts_label.setHorizontalAlignment(JLabel.LEFT);
        gifts_label.setBounds(20, 165, 100, 100);

        // Create new separator
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator1.setBounds(0, 266, 800, 10);

        // Create list of gift items (horizontal)
        CustomGiftList giftList = customGiftList; // Use the existing CustomListDemo instance
        giftList.setBackground(new Color(193, 154, 107));
        giftList.setBounds(20, 220, 750, 100);

        purchaseButton = new JButton("Purchase");
        purchaseButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        purchaseButton.setHorizontalAlignment(JLabel.CENTER);
        purchaseButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
        purchaseButton.setBounds(430, 370, 105, 40);
        purchaseButton.addActionListener(this); // Add action listener for the button

        // Create "Exit" button
        exit_button = new JButton("Exit");
        exit_button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        exit_button.setHorizontalAlignment(JLabel.CENTER);
        exit_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
        exit_button.setBounds(290, 370, 105, 40);
        exit_button.addActionListener(this); // add action listener for the button

        // Add components to the panel
        this.setLayout(null);
        this.setPreferredSize(new Dimension(700, 400)); //FIXME: make the panel a bit smaller?
        this.setBackground(new Color(193, 154, 107));
        this.add(food_label); // Add food label
        this.add(foodList); // Add food scroll pane
        this.add(gifts_label); // Add gifts label
        this.add(giftList); // Add gift list
        this.add(purchaseButton); // Add purchase button
        this.add(exit_button); // Add exit button
    }

    /**
     * Handles actions for the "Purchase" and "Exit" buttons.
     * When the purchase button is clicked, it checks if an item is selected,
     * verifies the inventory capacity, and then adds the selected item
     * (food or gift) to the inventory.
     * The exit button closes the application when clicked.
     *
     * @param e The event triggered by a button click.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == purchaseButton) {
            // Check if the inventory is full
            if (totalItems == MAX_CAPACITY) {
                JOptionPane.showMessageDialog(this, "Inventory is full! Use items to free up space", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit early since no purchase can be made
            }

            // Check for selection in the gift list
            String selectedGift = null;
            if (customGiftList != null) {
                selectedGift = customGiftList.isSelected(); // Get the selected gift
                if (selectedGift != null) {
                    int selectedIndex = customGiftList.petList.getSelectedIndex();
                    ImageIcon selectedImage = customGiftList.images[selectedIndex]; // Retrieve the associated image

                    Item giftItem = new Item(selectedGift, 40, selectedImage);
                    inventoryPanel.addGiftItem(giftItem); // Add gift item to inventory
                    JOptionPane.showMessageDialog(this, "You purchased: " + selectedGift, "Purchase Success", JOptionPane.INFORMATION_MESSAGE);
                    return; // Exit after successful purchase
                }
            }

            // Check for selection in the food list
            String selectedFood = null;
            if (customListDemo != null) {
                selectedFood = customListDemo.isSelected(); // Get the selected food
                if (selectedFood != null) {
                    int selectedIndex = customListDemo.petList.getSelectedIndex();
                    ImageIcon selectedImage = customListDemo.images[selectedIndex]; // Retrieve the associated image

                    Item foodItem = new Item(selectedFood, 25, selectedImage);
                    inventoryPanel.addFoodItem(foodItem); // Add food item to inventory
                    JOptionPane.showMessageDialog(this, "You purchased: " + selectedFood, "Purchase Success", JOptionPane.INFORMATION_MESSAGE);
                    return; // Exit after successful purchase
                }
            }

            // If no item is selected in either list
            JOptionPane.showMessageDialog(this, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);

        } else if (e.getSource() == exit_button) {
            // Exit action
            JOptionPane.showMessageDialog(this, "Exiting Inventory...");
            System.exit(0); // Close the application
        }
    }
}