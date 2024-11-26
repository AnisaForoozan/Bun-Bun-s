package com.cs2212.bunbun.gameplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inventory extends JFrame implements ActionListener {
    // Instance variables

    // Constructor
    public Inventory() {

        //create "Inventory" label
        JLabel inventory = new JLabel("Inventory");
        inventory.setHorizontalAlignment(JLabel.CENTER);
        inventory.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); //border for the label
        inventory.setBounds(320, 0, 150, 30);

        //create "Food" label
        JLabel food = new JLabel("Food");
        food.setHorizontalAlignment(JLabel.LEFT);
        food.setBounds(20, 30, 100, 100);

        //create new separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBounds(0, 90, 800, 10);

        //create "Gifts" label
        JLabel gifts = new JLabel("Gifts");
        gifts.setHorizontalAlignment(JLabel.LEFT);
        gifts.setBounds(20, 205, 100, 100);

        //create new separator
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator1.setBounds(0, 266, 800, 10);

        //Create new label
        this.setSize(800, 500); //set dimensions
        this.setLayout(null);
        this.setResizable(false);  // Prevent resizing of the frame
        this.setVisible(true); //make the frame visible
        this.add(inventory); //add label
        this.add(food);
        this.add(separator);
        this.add(gifts);
        this.add(separator1);
        this.setVisible(true); //make frame visible
    }

        // Initialize the JFrame properties inside the constructor
//        JLabel inventory = new JLabel("Inventory"); //create a label
//        inventory.setPreferredSize(new Dimension(20, 40));
//        inventory.setHorizontalAlignment(JLabel.CENTER);
//        inventory.setVerticalAlignment(JLabel.CENTER);
//        inventory.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); //add some padding
//        inventory.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//
//
//        // Create new JPanel to wrap the label
//        JPanel panel = new JPanel();
//        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
//
//        // Create new frame
//        this.setTitle("Bun Bun's"); // Set the title of the frame
//        this.setResizable(false);  // Prevent resizing of the frame
//        this.setSize(800, 500); // Set the size of the frame
//        this.setLayout(new BorderLayout()); //new borderlayout
//        this.add(inventory, BorderLayout.NORTH); //set inventory label
//        this.setVisible(true); // Make the frame visible
//        this.setLayout(new FlowLayout());

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