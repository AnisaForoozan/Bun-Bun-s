package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class PetSelection extends JPanel {
    private AudioPlayer audioPlayer;

    public PetSelection(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;

        setBackground(new Color(0xE8CAE8));
        setLayout(new BorderLayout());

        // Back Button
        JButton backButton = createButton("⬅", e -> showBackDialog(cardLayout, mainPanel));
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);
        topLeftPanel.add(backButton);
        add(topLeftPanel, BorderLayout.NORTH);

        // Title Panel
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Choose your pet");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, gbc);

        add(titlePanel, BorderLayout.CENTER);
    }

    private void showBackDialog(CardLayout cardLayout, JPanel mainPanel) {
        // Get the parent JFrame
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);

        // Create a custom modal dialog
        JDialog dialog = new JDialog(parentFrame, true);
        dialog.setUndecorated(true); // Remove title bar and close/maximize/minimize buttons
        dialog.setSize(450, 200); // Set dialog size
        dialog.setLocationRelativeTo(this); // Center on the PetSelection panel

        // Custom panel for the dialog content
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(117, 101, 81)); // Dialog background color
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50); // Rounded edges
                g2d.dispose();
            }
        };

        contentPanel.setOpaque(false); // Ensure transparency around the rounded corners
        contentPanel.setLayout(new BorderLayout());

        // Label for the dialog message
        JLabel messageLabel = new JLabel("Where do you want to go?", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        messageLabel.setForeground(Color.WHITE); // White text
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        // Main Menu Button
        JButton mainMenuButton = new JButton("Menu");
        styleDialogButton(mainMenuButton, new Color(232, 202, 232), () -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav"); // Play click sound
            dialog.dispose(); // Close the dialog
            showLoadingScreenAndSwitchPanel(cardLayout, mainPanel, "MainMenu"); // Show loading screen and switch to Main Menu
        });

        // Slots Button
        JButton slotsButton = new JButton("Slots");
        styleDialogButton(slotsButton, new Color(232, 202, 232), () -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav"); // Play click sound
            dialog.dispose(); // Close the dialog
            showLoadingScreenAndSwitchPanel(cardLayout, mainPanel, "Gameplay"); // Show loading screen and switch to Gameplay
        });

        // Add buttons to the button panel
        buttonPanel.add(mainMenuButton);
        buttonPanel.add(slotsButton);

        // Add the button panel to the content panel
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the content panel to the dialog
        dialog.setContentPane(contentPanel);

        // Add transparency to the dialog itself
        dialog.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        dialog.getRootPane().setOpaque(false); // Ensure the root pane does not paint a background

        // Make the dialog visible
        dialog.setVisible(true);
    }

    private void showLoadingScreenAndSwitchPanel(CardLayout cardLayout, JPanel mainPanel, String targetPanel) {
        // Create a loading screen panel
        JPanel loadingScreen = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK); // Background color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Add a loading label to the panel
        JLabel loadingLabel = new JLabel("Loading", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        loadingLabel.setForeground(Color.WHITE);
        loadingScreen.add(loadingLabel, BorderLayout.CENTER);

        // Add the loading screen to mainPanel and show it
        mainPanel.add(loadingScreen, "Loading");
        cardLayout.show(mainPanel, "Loading");

        // Timer for animating the dots
        Timer dotTimer = new Timer(500, null);
        final String baseText = "Loading";
        dotTimer.addActionListener(e -> {
            String currentText = loadingLabel.getText();
            if (currentText.endsWith("...")) {
                loadingLabel.setText(baseText); // Reset to "Loading"
            } else {
                loadingLabel.setText(currentText + "."); // Add a dot
            }
        });
        dotTimer.start();

        // Timer to simulate loading and then switch to the target panel
        Timer loadingTimer = new Timer(2000, e -> {
            dotTimer.stop(); // Stop the dot animation
            cardLayout.show(mainPanel, targetPanel); // Switch to the target panel
            mainPanel.remove(loadingScreen); // Remove the loading screen
        });

        loadingTimer.setRepeats(false); // Ensure the timer runs only once
        loadingTimer.start();
    }

    private JButton createButton(String text, java.awt.event.ActionListener onClick) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);

        // Add click sound
        button.addActionListener(e -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav");
            onClick.actionPerformed(e);
        });

        // Add hover sound and color change
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color defaultColor = button.getForeground();
            private final Color hoverColor = new Color(0x756551);

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(hoverColor);
                audioPlayer.playSFX("audio/sfx/hover_sound.wav");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(defaultColor);
            }
        });

        return button;
    }

    private void styleDialogButton(JButton button, Color hoverColor, Runnable onClick) {
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        button.setForeground(Color.WHITE); // Default foreground color
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // Add hover and click effects
        button.addActionListener(e -> onClick.run()); // Run the provided click action
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(hoverColor); // Change foreground to hover color
                audioPlayer.playSFX("audio/sfx/hover_sound.wav"); // Play hover sound
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE); // Reset to default color on exit
            }
        });
    }
}
