package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class Tutorial extends JPanel {
    public Tutorial(CardLayout cardLayout, JPanel mainPanel) {
        setBackground(new Color(0xE8CAE8));
        setLayout(new BorderLayout());

        // Add a label
        JLabel tutorialLabel = new JLabel("Tutorial Screen", SwingConstants.CENTER);
        tutorialLabel.setFont(new Font("Arial", Font.BOLD, 48));
        tutorialLabel.setForeground(new Color(117, 101, 81));
        add(tutorialLabel, BorderLayout.CENTER);

        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        add(backButton, BorderLayout.SOUTH);
    }
}
