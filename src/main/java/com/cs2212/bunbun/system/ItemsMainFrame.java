package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemsMainFrame extends JPanel implements ActionListener {
    private Inventory inventoryPanel;
    private Shop shopPanel;
    private JPanel cardPanel; // CardLayout container
    private JButton inventory_button;
    private JButton shop_button;
    private JButton backButton;

    // Constructor for ItemsMainFrame
    public ItemsMainFrame(CardLayout mainCardLayout, JPanel mainPanel) {
        // Create Inventory and Shop panels
        inventoryPanel = new Inventory();
        shopPanel = new Shop(inventoryPanel);

        // Create CardLayout container
        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(inventoryPanel, "Inventory");
        cardPanel.add(shopPanel, "Shop");

        // Create navigation buttons
        inventory_button = new JButton("Inventory");
        inventory_button.addActionListener(this); // Use actionPerformed method

        shop_button = new JButton("Shop");
        shop_button.addActionListener(this); // Use actionPerformed method

        backButton = new JButton("Back");
        backButton.addActionListener(e -> mainCardLayout.show(mainPanel, "Gameplay")); // Navigate back to Gameplay

        // Button panel for navigation
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(inventory_button);
        buttonPanel.add(shop_button);
        buttonPanel.add(backButton);

        // Layout for the main panel
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

        // Switch between Inventory and Shop views based on which button is clicked
        if (e.getSource() == inventory_button) {
            cardLayout.show(cardPanel, "Inventory"); // Show the Inventory panel
        } else if (e.getSource() == shop_button) {
            cardLayout.show(cardPanel, "Shop"); // Show the Shop panel
        }
    }
}
