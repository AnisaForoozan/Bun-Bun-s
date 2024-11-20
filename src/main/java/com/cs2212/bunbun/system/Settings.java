package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class Settings extends JPanel {
    public Settings(CardLayout cardLayout, JPanel mainPanel) {
        setBackground(new Color(0xE8CAE8));
        setLayout(new BorderLayout());

        // Add a label
        JLabel settingsLabel = new JLabel("Settings Screen", SwingConstants.CENTER);
        settingsLabel.setFont(new Font("Arial", Font.BOLD, 48));
        settingsLabel.setForeground(new Color(117, 101, 81));
        add(settingsLabel, BorderLayout.CENTER);

        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        add(backButton, BorderLayout.SOUTH);
    }
}
