package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ParentalControls extends JPanel {
    public ParentalControls(CardLayout cardLayout, JPanel mainPanel) {
        setBackground(new Color(0xE8CAE8));
        setLayout(new BorderLayout());

        // Create a panel for the back button and add it to the top-left
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);

        JButton backButton = new JButton("⬅");
        backButton.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        backButton.setOpaque(false); // Make the button non-opaque
        backButton.setContentAreaFilled(false); // Disable the default background fill
        backButton.setBorderPainted(false); // Remove the default border
        backButton.setForeground(new Color(117, 101, 81)); // Set text color
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        topLeftPanel.add(backButton);
        add(topLeftPanel, BorderLayout.NORTH); // Add back button panel to the top



        // Create a panel for the title with GridBagLayout
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false); // Make the panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Center horizontally
        gbc.gridy = 0; // Center vertically
        gbc.insets = new Insets(0, 0, 40, 0); // Add padding
        gbc.anchor = GridBagConstraints.CENTER; // Center the label

        // Add title label
        JLabel titleLabel = new JLabel("Parental Controls", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 255, 255));
        titlePanel.add(titleLabel, gbc);

        // Add the title panel to the center of the screen
        add(titlePanel, BorderLayout.CENTER);
    }
}
