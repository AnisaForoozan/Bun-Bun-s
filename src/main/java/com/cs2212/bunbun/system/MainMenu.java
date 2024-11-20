package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu extends JFrame {

    public MainMenu() {
        // Set up the frame
        setTitle("Bun bun");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing

        // Set up the background color
        getContentPane().setBackground(new Color(0xE8CAE8));

        // Initialize the UI
        initializeUI();
    }

    private void initializeUI() {
        // Set layout to GridBagLayout for dynamic positioning
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Add title image
        URL imageUrl = getClass().getClassLoader().getResource("images/bunbunlogo.png"); // Update with your file path
        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(400, 200, Image.SCALE_SMOOTH); // Adjust size
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JLabel titleLabel = new JLabel(scaledIcon);

            // Configure GridBagConstraints for the title
            gbc.gridx = 0; // Centered horizontally
            gbc.gridy = 0; // First row (at the top)
            gbc.insets = new Insets(0, 0, 40, 0); // Add padding
            gbc.anchor = GridBagConstraints.PAGE_START; // Align towards the top
            gbc.weightx = 1.0; // Distribute horizontal space evenly
            gbc.weighty = 0.0; // No extra vertical space for the title
            add(titleLabel, gbc);
        } else {
            System.out.println("Image not found: bunbun_logo.png");
        }

        // Configure GridBagConstraints for buttons
        gbc.gridy = 1; // Start button section
        gbc.insets = new Insets(30, 0, 10, 0); // Spacing between buttons
        gbc.anchor = GridBagConstraints.CENTER; // Center-align buttons
        gbc.weighty = 0.0; // No extra vertical space

        // Create a panel to group the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 40)); // Stack buttons vertically with 40px spacing
        buttonPanel.setOpaque(false); // Transparent background to match the frame

        // Create buttons
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

        // Add buttons to the panel
        buttonPanel.add(newGameButton);
        buttonPanel.add(loadGameButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(parentalControlsButton);
        buttonPanel.add(exitButton);

        // Add the button panel to the layout
        add(buttonPanel, gbc);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false); // Remove focus outline
        button.setFont(new Font("Arial", Font.PLAIN, 32)); // Increased font size for buttons
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0xE8CAE8));
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2)); // Transparent color
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}
