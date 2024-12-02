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
    private JLabel capacityLabel; // Label to show inventory capacity

    public static int totalItems; // Track total items in inventory
    public static final int MAX_CAPACITY = 50; // Maximum inventory capacity

    //Button declarations
    private JButton confirmButton;
    JButton exit_button;
    //JButton inventory_button;
    //JButton shop_button;

    // Constructor
    public Inventory() {
        totalItems = 0; // Initialize total items

        capacityLabel = new JLabel("<html>Number of items: <b>" + totalItems + "</b>/" + MAX_CAPACITY + "</html>");
        capacityLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        capacityLabel.setBounds(10, 10, 100, 35);

        // Create "Food" label
        JLabel food_label = new JLabel("Food");
        food_label.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        food_label.setHorizontalAlignment(JLabel.LEFT);
        food_label.setForeground(new Color(0, 0, 0));
        food_label.setBounds(20, 30, 100, 100);

        // Initialize the food list
        foodListModel = new DefaultListModel<>();
        foodList = new JList<>(foodListModel);
        foodList.setCellRenderer(new InventoryItemRenderer()); // Set custom renderer
        foodList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        foodList.setVisibleRowCount(1);
        foodList.setBackground(new Color(193, 154, 107));
        foodList.setBounds(20, 100, 750, 70);

        // Add a listener to foodList to clear selection in giftList
        foodList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && foodList.getSelectedIndex() != -1) {
                giftList.clearSelection(); // Clear selection in gift list
            }
        });

        // Create new separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(0, 0, 0));
        separator.setBounds(0, 90, 800, 10);

        // Create "Gifts" label
        JLabel gifts_label = new JLabel("Gifts");
        gifts_label.setHorizontalAlignment(JLabel.LEFT);
        gifts_label.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gifts_label.setBounds(20, 185, 100, 100);

        // Create new separator
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator1.setForeground(new Color(0, 0, 0));
        separator1.setBounds(0, 246, 800, 10);

        // Initialize the gift list
        giftListModel = new DefaultListModel<>();
        giftList = new JList<>(giftListModel);
        giftList.setCellRenderer(new InventoryItemRenderer()); // Set custom renderer
        giftList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        giftList.setVisibleRowCount(1);
        giftList.setBackground(new Color(193, 154, 107));
        giftList.setBounds(20, 260, 750, 70);

        // Add a listener to giftList to clear selection in foodList
        giftList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && giftList.getSelectedIndex() != -1) {
                foodList.clearSelection(); // Clear selection in food list
            }
        });

        // Create confirm button
        confirmButton = new JButton("Confirm");
        confirmButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        confirmButton.setHorizontalAlignment(JLabel.CENTER);
        confirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
        confirmButton.setBounds(430, 370, 105, 40);
        confirmButton.addActionListener(this); // Add action listener for the button

        // Create exit button
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
        this.add(capacityLabel);
        this.add(food_label); // Add food label
        this.add(foodList);
        this.add(separator); // Add separator
        this.add(gifts_label); // Add gifts label
        this.add(giftList);
        this.add(separator1); // Add separator
        this.add(confirmButton); // Add confirm button
        this.add(exit_button); // Add exit button

    }

    class InventoryItemRenderer extends JPanel implements ListCellRenderer<Item> {
        private JLabel iconLabel;
        private JLabel nameLabel;

        public InventoryItemRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            iconLabel = new JLabel();
            nameLabel = new JLabel();
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(iconLabel);
            add(nameLabel);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index, boolean isSelected, boolean cellHasFocus) {
            if (item != null) {
                ImageIcon image = item.getImage();
                if (image != null) {
                    Image scaledImage = image.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    iconLabel.setIcon(new ImageIcon(scaledImage));
                }
                nameLabel.setText(item.getName() + "[" + item.getQuantity() + "]");
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            // Set a fixed cell size
            this.setPreferredSize(new Dimension(90, 80)); // Width: 120, Height: 80
            return this;
        }
    }

    // Method to add a food item
    public boolean addFoodItem(Item item) {
        if (totalItems >= MAX_CAPACITY) {
//            JOptionPane.showMessageDialog(this, "Inventory is full!");
            return false;
        }

        // Check if the item already exists and increase quantity
        for (int i = 0; i < foodListModel.size(); i++) {
            Item existingItem = foodListModel.getElementAt(i);
            if (existingItem.getName().equals(item.getName())) {
                existingItem.incrementQuantity();
                foodListModel.set(i, existingItem);  // Refresh the item in the list
                totalItems++;
                updateCapacity();
                return false; // Item exists, no need to add new one
            }
        }

        // Add new item if it doesn't exist
        foodListModel.addElement(item);
        totalItems++;
        updateCapacity();
        return true;
    }

    // Method to add a gift item
    public boolean addGiftItem(Item item) {
        if (totalItems >= MAX_CAPACITY) {
//            JOptionPane.showMessageDialog(this, "Inventory is full!");
            return false;
        }

        // Check if the item already exists and increase quantity
        for (int i = 0; i < giftListModel.size(); i++) {
            Item existingItem = giftListModel.getElementAt(i);
            if (existingItem.getName().equals(item.getName())) {
                existingItem.incrementQuantity();
                giftListModel.set(i, existingItem);  // Refresh the item in the list
                totalItems++;
                updateCapacity();
                return false; // Item exists, no need to add new one
            }
        }

        // Add new item if it doesn't exist
        giftListModel.addElement(item);
        totalItems++;
        updateCapacity();
        return true;
    }


    // Update inventory capacity label
    private void updateCapacity() {
        capacityLabel.setText("<html>Number of items: <b>" + totalItems + "</b>/" + MAX_CAPACITY + "</html>");
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
            totalItems--;
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

        Item apple = new Item("Apple", 25, null);
        // Add some test items
        inventoryPanel.addFoodItem(apple);
        inventoryPanel.addGiftItem(apple); // Add duplicate to test quantity

        frame.setVisible(true);
    }

}