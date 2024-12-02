//package com.cs2212.bunbun.system;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class ItemsMainFrame extends JFrame implements ActionListener {
//    private Inventory inventoryPanel; // The inventory panel
//    private Shop shopPanel; // The shop panel
//    private JPanel cardPanel; // The CardLayout panel
//    private JButton inventory_button;
//    private JButton shop_button;
//    private CustomListDemo customListDemo;
//    private CustomGiftList customGiftList;
//
//    // Constructor for MainFrame
//    public ItemsMainFrame() {
//        getContentPane().setBackground(new Color(193, 154, 107)); // Set the frame background color
//        // Initialize the custom list demo panel
//        customListDemo = new CustomListDemo();
////        customListDemo.setBackground(new Color(193, 154, 107));
//        customGiftList = new CustomGiftList();
//
//        // Set up the frame
//        setTitle("Inventory and Shop");
//        setSize(800, 500);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        // Create the inventory and shop panels
//        inventoryPanel = new Inventory();
////        inventoryPanel.setBackground(new Color(193, 154, 107));
//        shopPanel = new Shop(customListDemo, customGiftList, inventoryPanel); // Assuming Shop class exists with your shop panel
//
//        // Create CardLayout container
//        cardPanel = new JPanel(new CardLayout());
////        cardPanel.setForeground(new Color(193, 154, 107));
//
//        // Add panels to the CardLayout container
//        cardPanel.add(inventoryPanel, "Inventory");
//        cardPanel.add(shopPanel, "Shop");
//
//        // Create "Inventory" button
//        inventory_button = new JButton("Inventory");
//        inventory_button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
//        inventory_button.setHorizontalAlignment(JLabel.CENTER);
//        inventory_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
//        inventory_button.setBounds(290, 10, 100, 30);
//        inventory_button.addActionListener(this); // Register action listener for Inventory button
//
//        // Create "Shop" button
//        shop_button = new JButton("Shop");
//        shop_button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
//        shop_button.setHorizontalAlignment(JLabel.CENTER);
//        shop_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
//        shop_button.setBounds(430, 10, 100, 30);
//        shop_button.addActionListener(this); // Register action listener for Shop button
//
//        // Set up the layout for the buttons (outside the cardPanel)
//        setLayout(null);
//        add(inventory_button);
//        add(shop_button);
//        add(cardPanel);
//
//        // Adjust cardPanel bounds to fit inside the frame
//        cardPanel.setBounds(0, 40, 800, 460); // leave space for the buttons at the top
//    }
//
//
//
//    // Action listener for the buttons
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
//
//        // Switch between Inventory and Shop views based on which button is clicked
//        if (e.getSource() == inventory_button) {
//            cardLayout.show(cardPanel, "Inventory"); // Show the Inventory panel
//        } else if (e.getSource() == shop_button) {
//            cardLayout.show(cardPanel, "Shop"); // Show the Shop panel
//        }
//    }
//
//    // Main method to run the application
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            ItemsMainFrame mainFrame = new ItemsMainFrame();
//            mainFrame.setVisible(true);
//        });
//    }
//}

package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class ItemsMainFrame extends JFrame implements ActionListener {
    private Inventory inventoryPanel; // The inventory panel
    private Shop shopPanel; // The shop panel
    private JPanel cardPanel; // The CardLayout panel
    private JButton inventory_button;
    private JButton shop_button;
    private CustomListDemo customListDemo;
    private CustomGiftList customGiftList;

    private Consumer<String> onItemConsumedCallback; // Callback for item consumption

    // Constructor for MainFrame
    public ItemsMainFrame() {
        getContentPane().setBackground(new Color(193, 154, 107)); // Set the frame background color
        customListDemo = new CustomListDemo();
        customGiftList = new CustomGiftList();

        setTitle("Inventory and Shop");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this frame
        setLocationRelativeTo(null);

        // Create the inventory and shop panels
        inventoryPanel = new Inventory();
        shopPanel = new Shop(customListDemo, customGiftList, inventoryPanel);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(inventoryPanel, "Inventory");
        cardPanel.add(shopPanel, "Shop");

        inventory_button = new JButton("Inventory");
        inventory_button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        inventory_button.setHorizontalAlignment(JLabel.CENTER);
        inventory_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        inventory_button.setBounds(290, 10, 100, 30);
        inventory_button.addActionListener(this);

        shop_button = new JButton("Shop");
        shop_button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        shop_button.setHorizontalAlignment(JLabel.CENTER);
        shop_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        shop_button.setBounds(430, 10, 100, 30);
        shop_button.addActionListener(this);

        setLayout(null);
        add(inventory_button);
        add(shop_button);
        add(cardPanel);
        cardPanel.setBounds(0, 40, 800, 460);
    }

    public void setOnItemConsumedCallback(Consumer<String> callback) {
        this.onItemConsumedCallback = callback;
    }

    public void simulateItemConsumption(String itemType) {
        if (onItemConsumedCallback != null) {
            onItemConsumedCallback.accept(itemType);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        if (e.getSource() == inventory_button) {
            cardLayout.show(cardPanel, "Inventory");
        } else if (e.getSource() == shop_button) {
            cardLayout.show(cardPanel, "Shop");
        }
    }
}

//package com.cs2212.bunbun.system;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.function.Consumer;
//
//public class ItemsMainFrame extends JFrame {
//    private Consumer<String> onItemConsumedCallback;
//
//    public ItemsMainFrame() {
//        setTitle("Inventory and Store");
//        setSize(400, 300);
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//        setLayout(new GridLayout(3, 1));
//
//        JButton foodButton = new JButton("Consume Food");
//        foodButton.addActionListener(e -> consumeItem("food"));
//
//        JButton giftButton = new JButton("Consume Gift");
//        giftButton.addActionListener(e -> consumeItem("gift"));
//
//        JButton closeButton = new JButton("Close");
//        closeButton.addActionListener(e -> dispose());
//
//        add(foodButton);
//        add(giftButton);
//        add(closeButton);
//    }
//
//    public void setOnItemConsumedCallback(Consumer<String> callback) {
//        this.onItemConsumedCallback = callback;
//    }
//
//    private void consumeItem(String itemType) {
//        if (onItemConsumedCallback != null) {
//            onItemConsumedCallback.accept(itemType);
//        }
//        dispose(); // Close the window after consuming an item
//    }
//}

