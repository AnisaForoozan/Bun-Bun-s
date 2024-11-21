package com.cs2212.bunbun.gameplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inventory extends JFrame implements ActionListener {
    // Instance variables

    // Constructor
    public Inventory() {
        // Initialize the JFrame properties inside the constructor
        JLabel inventory = new JLabel("Inventory"); //create a label
        inventory.setVerticalTextPosition(JLabel.TOP);
        inventory.setHorizontalAlignment(JLabel.CENTER);
        inventory.setVerticalAlignment(JLabel.CENTER);

        // Create new JPanel to wrap the label
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));

        // Create new frame
        this.setTitle("Bun Bun's"); // Set the title of the frame
        this.setResizable(false);  // Prevent resizing of the frame
        this.setSize(800, 500); // Set the size of the frame
        this.setLayout(new BorderLayout()); //new borderlayout
        this.add(inventory, BorderLayout.NORTH); //set inventory label
        this.setVisible(true); // Make the frame visible
    }

    public static void main(String[] args) {
        // Create and display the Settings frame
        new com.cs2212.bunbun.gameplay.Inventory();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        if (e.getSource()==saveGame) {
//
//        }
    }
}