package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inventory extends JFrame implements ActionListener {
    // Instance variables

    JButton exit_button;
    // Constructor
    public Inventory() {

        //create "Inventory" button
        JButton inventory_button = new JButton("Inventory");
        inventory_button.setHorizontalAlignment(JLabel.CENTER);
        inventory_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); //border for the label
        inventory_button.setBounds(290, 0, 100, 30);

        //create "Shop" button
        JButton shop_button = new JButton("Shop");
        shop_button.setHorizontalAlignment(JLabel.CENTER);
        shop_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); //border for the label
        shop_button.setBounds(430, 0, 100, 30);

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
        this.add(inventory_button); //add inventory button
        this.add(shop_button);
        this.add(food_label);
        this.add(separator);
        this.add(gifts_label);
        this.add(separator1);
        this.add(exit_button);
        this.setVisible(true); //make frame visible
    }

    public static void main(String[] args) {
        // Create and display the Settings frame
        new Inventory();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        if (e.getSource()==exit) {
//
//        }
    }
}