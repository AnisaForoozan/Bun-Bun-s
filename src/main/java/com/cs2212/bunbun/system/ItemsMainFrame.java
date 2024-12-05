package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents the main frame and provides users the option to switch between the shop and the inventory panels.
 * The frame uses a CardLayout to switch between views and includes buttons
 * for interacting with the panels.
 *
 * The frame initializes and displays inventory and shop functionalities
 * using separate panels, managed by a CardLayout for seamless transitions.
 *
 * @author      Anne Liu <aliu432@uwo.ca>
 * @version     1.0
 * @since       1.0
 */
public class ItemsMainFrame extends JFrame implements ActionListener {
    private Inventory inventoryPanel; // The inventory panel
    private Shop shopPanel; // The shop panel
    private JPanel cardPanel; // The CardLayout panel
    private JButton inventory_button;
    private JButton shop_button;
    private CustomListDemo customListDemo;
    private CustomGiftList customGiftList;

    /**
     * Constructs the ItemsMainFrame, setting up the layout and panels.
     * Initializes the inventory and shop panels and sets up the CardLayout
     * container to switch between these views. The constructor also adds buttons
     * to toggle between the Inventory and Shop views.
     */
    public ItemsMainFrame() {
        getContentPane().setBackground(new Color(193, 154, 107)); // Set the frame background color

        customListDemo = new CustomListDemo();
        customGiftList = new CustomGiftList();

        // Set up the frame
        setTitle("Inventory and Shop");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this frame
        setLocationRelativeTo(null);

        // Create the inventory and shop panels
        inventoryPanel = new Inventory();
        shopPanel = new Shop(customListDemo, customGiftList, inventoryPanel); // Assuming Shop class exists with your shop panel

        // Create CardLayout container
        cardPanel = new JPanel(new CardLayout());

        // Add panels to the CardLayout container
        cardPanel.add(inventoryPanel, "Inventory");
        cardPanel.add(shopPanel, "Shop");

        // Create "Inventory" button
        inventory_button = new JButton("Inventory");
        inventory_button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        inventory_button.setHorizontalAlignment(JLabel.CENTER);
        inventory_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
        inventory_button.setBounds(290, 10, 100, 30);
        inventory_button.addActionListener(this); // Register action listener for Inventory button

        // Create "Shop" button
        shop_button = new JButton("Shop");
        shop_button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        shop_button.setHorizontalAlignment(JLabel.CENTER);
        shop_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border for the button
        shop_button.setBounds(430, 10, 100, 30);
        shop_button.addActionListener(this); // Register action listener for Shop button

        // Set up the layout for the buttons (outside the cardPanel)
        setLayout(null);
        add(inventory_button);
        add(shop_button);
        add(cardPanel);

        // Adjust cardPanel bounds to fit inside the frame
        cardPanel.setBounds(0, 40, 800, 460); // leave space for the buttons at the top
    }


    /**
     * Handles button actions. When either the "Inventory" or "Shop" button
     * is clicked, the CardLayout switches to the corresponding panel.
     *
     * @param e The event triggered by a button click.
     */
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

    /**
     * Main method to launch the ItemsMainFrame application.
     * Initializes and displays the frame.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ItemsMainFrame mainFrame = new ItemsMainFrame();
            mainFrame.setVisible(true);
        });
    }
}