package com.cs2212.bunbun.gameplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inventory extends JFrame implements ActionListener {
    // Instance variables

    JButton exit_button;
    // Constructor
    public Inventory() {

        //create "Inventory" label
        JLabel inventory_label= new JLabel("Inventory");
        inventory_label.setHorizontalAlignment(JLabel.CENTER);
        inventory_label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); //border for the label
        inventory_label.setBounds(320, 0, 150, 30);

        //create "Food" label
        JLabel food_label = new JLabel("Food");
        food_label.setHorizontalAlignment(JLabel.LEFT);
        food_label.setBounds(20, 30, 100, 100);

        //create new separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBounds(0, 90, 800, 10);

        //create "Gifts" label
        JLabel gifts_label = new JLabel("Gifts");
        gifts_label.setHorizontalAlignment(JLabel.LEFT);
        gifts_label.setBounds(20, 205, 100, 100);

        //create new separator
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator1.setBounds(0, 266, 800, 10);

        exit_button = new JButton();
        exit_button.setBounds(340, 390, 105, 40);
//        exit_button.addActionListener(this);
        exit_button.setText("Exit");


        //Create new label
        this.setSize(800, 500); //set dimensions
        this.setLayout(null);
        this.setResizable(false);  // Prevent resizing of the frame
        this.setVisible(true); //make the frame visible
        this.add(inventory_label); //add label
        this.add(food_label);
        this.add(separator);
        this.add(gifts_label);
        this.add(separator1);
        this.add(exit_button);
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
//        if (e.getSource()==exit) {
//
//        }
    }
}