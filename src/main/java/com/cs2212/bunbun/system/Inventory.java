package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inventory extends JPanel implements ActionListener {
    // Instance variables
    JButton exit_button;
    JButton inventory_button;
    JButton shop_button;

    // Constructor
    public Inventory() {

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

        // Create "Exit" button
        exit_button = new JButton("Exit");
        exit_button.setBounds(340, 390, 105, 40);
        exit_button.addActionListener(this); // add action listener for the button

        // Add components to the panel
        this.setLayout(null);
        this.add(inventory_button); // add inventory button
        this.add(shop_button); // add shop button
        this.add(food_label); // add food label
        this.add(separator); // add separator
        this.add(gifts_label); // add gifts label
        this.add(separator1); // add separator
        this.add(exit_button); // add exit button
    }

    // Action performed method
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exit_button) {
            // Exit action
            JOptionPane.showMessageDialog(this, "Exiting Inventory...");
            System.exit(0); // close the application
        }
    }

    // Test the panel in a JFrame
    public static void main(String[] args) {
        // Create and display the JFrame
        JFrame frame = new JFrame("Inventory Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        // Create an instance of Inventory and add it to the frame
        Inventory inventoryPanel = new Inventory();
        frame.add(inventoryPanel); // add inventory panel to the frame

        // Make the frame visible
        frame.setVisible(true); // make the frame visible
    }
}

