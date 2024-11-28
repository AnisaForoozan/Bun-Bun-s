package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        // Set up the frame
        setTitle("Bun bun");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing

        // Set up the background color
        getContentPane().setBackground(new Color(0xE8CAE8));

        // Initialize the UI
        initializeUI();
    }

    private void initializeUI() {
        // Set layout to GridBagLayout for dynamic centering
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Configure GridBagConstraints
        gbc.gridx = 0; // Single column layout
        gbc.gridy = GridBagConstraints.RELATIVE; // Components stacked vertically
        gbc.insets = new Insets(10, 0, 10, 0); // Spacing between components
        gbc.anchor = GridBagConstraints.CENTER;

        // Add title label
        JLabel titleLabel = new JLabel("Bun bun's");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel, gbc); // Add title to the layout

        // Add buttons
        JButton newGameButton = createMenuButton("New Game");
        JButton loadGameButton = createMenuButton("Load Game");
        JButton settingsButton = createMenuButton("Settings");
        JButton parentalControlsButton = createMenuButton("Parental Controls");
        JButton exitButton = createMenuButton("Exit");

        // Add button actions
        newGameButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "New Game button clicked!"));
        loadGameButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Load Game button clicked!"));
        settingsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Settings button clicked!"));
        parentalControlsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Parental Controls button clicked!"));
        exitButton.addActionListener(e -> System.exit(0)); // Exit application

        // Add buttons to the layout
        add(newGameButton, gbc);
        add(loadGameButton, gbc);
        add(settingsButton, gbc);
        add(parentalControlsButton, gbc);
        add(exitButton, gbc);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false); // Remove focus outline
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0xE8CAE8));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}
