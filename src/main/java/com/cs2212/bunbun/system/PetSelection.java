package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class PetSelection extends JPanel {
    private AudioPlayer audioPlayer;
    private String selectedPet;

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

        JLabel titleLabel = new JLabel("Choose your pet!");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, gbc);

        // Pet Selection Panel
        JPanel petPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        petPanel.setOpaque(false);

        // Define bunny names and image paths
        String[][] bunnies = {
                {"Black Bunny", "/images/black-bunny-normal.png"},
                {"Brown Bunny", "/images/brown-bunny-normal.png"},
                {"White Bunny", "/images/white-bunny-normal.png"}
        };

        for (String[] bunny : bunnies) {
            String bunnyName = bunny[0];
            String imagePath = bunny[1];
            JButton bunnyButton = createBunnyButton(bunnyName, imagePath);
            petPanel.add(bunnyButton);
        }

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 40, 0);
        titlePanel.add(petPanel, gbc);


        add(titlePanel, BorderLayout.CENTER);

        // Name Panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Name your pet:");
        nameLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        nameLabel.setForeground(Color.WHITE);

        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

        JButton confirmButton = createButton("Confirm", e -> {
            String petName = nameField.getText().trim();
            if (selectedPet == null) {
                JOptionPane.showMessageDialog(this, "Please select a pet first!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (petName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name for your pet!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "You have chosen " + selectedPet + " and named it " + petName + "!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Navigate to Bunny panel with loading screen
                showLoadingScreenAndSwitchPanel(cardLayout, mainPanel, "Bunny", petName, selectedPet);
            }
        });

        namePanel.add(nameLabel);
        namePanel.add(nameField);
        namePanel.add(confirmButton);

        add(namePanel, BorderLayout.SOUTH);
    }

    private JButton createBunnyButton(String bunnyName, String imagePath) {
        JButton bunnyButton = new JButton();
        bunnyButton.setPreferredSize(new Dimension(170, 170)); // Set initial size
        bunnyButton.setBackground(new Color(255, 228, 225));
        bunnyButton.setBorderPainted(false);
        bunnyButton.setFocusPainted(false);
        bunnyButton.setContentAreaFilled(false);

        // Load and set the initial icon
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon normalIcon = new ImageIcon(scaledImage);

        // Store the hover and click icons
        Image scaledImageHover = icon.getImage().getScaledInstance(165, 165, Image.SCALE_SMOOTH);
        ImageIcon hoverIcon = new ImageIcon(scaledImageHover);

        Image scaledImageClick = icon.getImage().getScaledInstance(165, 165, Image.SCALE_SMOOTH);
        ImageIcon clickedIcon = new ImageIcon(scaledImageClick);

        // Set the initial icon
        bunnyButton.setIcon(normalIcon);

        // Track the clicked state for this button
        bunnyButton.putClientProperty("isClicked", false);

        // Add hover, click effects, and toggling logic
        bunnyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!(boolean) bunnyButton.getClientProperty("isClicked")) { // Only apply hover effect if not clicked
                    bunnyButton.setIcon(hoverIcon);
                    audioPlayer.playSFX("audio/sfx/hover_sound.wav");
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!(boolean) bunnyButton.getClientProperty("isClicked")) { // Only revert to normal if not clicked
                    bunnyButton.setIcon(normalIcon);
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean isCurrentlyClicked = (boolean) bunnyButton.getClientProperty("isClicked");

                // Reset all buttons
                for (Component comp : bunnyButton.getParent().getComponents()) {
                    if (comp instanceof JButton) {
                        JButton otherButton = (JButton) comp;
                        otherButton.setIcon(new ImageIcon(new ImageIcon(
                                getClass().getResource((String) otherButton.getClientProperty("imagePath"))
                        ).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH))); // Reset to normal size
                        otherButton.putClientProperty("isClicked", false); // Reset click state
                    }
                }

                if (!isCurrentlyClicked) {
                    bunnyButton.setIcon(clickedIcon); // Enlarge the clicked button
                    bunnyButton.putClientProperty("isClicked", true);
                    selectedPet = bunnyName; // Set the selected pet
                    audioPlayer.playSFX("audio/sfx/click_sound.wav"); // Play click sound
                } else {
                    // If already clicked, revert to normal size
                    bunnyButton.setIcon(normalIcon);
                    bunnyButton.putClientProperty("isClicked", false);
                    selectedPet = null; // Deselect the pet
                }
            }
        });

        // Store the image path as a client property for each button
        bunnyButton.putClientProperty("imagePath", imagePath);

        return bunnyButton;
    }


    private void showLoadingScreenAndSwitchPanel(CardLayout cardLayout, JPanel mainPanel, String targetPanel, String petName, String petType) {
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

            if ("Bunny".equals(targetPanel)) {
                Gameplay bunnyPanel = new Gameplay(cardLayout, mainPanel, audioPlayer, petName, petType);
                mainPanel.add(bunnyPanel, "Bunny");
                cardLayout.show(mainPanel, "Bunny");
            } else {
                cardLayout.show(mainPanel, targetPanel);
            }

            mainPanel.remove(loadingScreen); // Remove the loading screen
        });

        loadingTimer.setRepeats(false); // Ensure the timer runs only once
        loadingTimer.start();
    }

    private void showBackDialog(CardLayout cardLayout, JPanel mainPanel) {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);

        JDialog dialog = new JDialog(parentFrame, true);
        dialog.setUndecorated(true);
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(117, 101, 81));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2d.dispose();
            }
        };

        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        JLabel messageLabel = new JLabel("Where do you want to go?", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        messageLabel.setForeground(Color.WHITE);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton mainMenuButton = new JButton("Menu");
        styleDialogButton(mainMenuButton, new Color(232, 202, 232), () -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav");
            dialog.dispose();
            showLoadingScreenAndSwitchPanel(cardLayout, mainPanel, "MainMenu", null, null);
        });

        JButton slotsButton = new JButton("Slots");
        styleDialogButton(slotsButton, new Color(232, 202, 232), () -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav");
            dialog.dispose();
            showLoadingScreenAndSwitchPanel(cardLayout, mainPanel, "LoadGame", null, null);
        });

        buttonPanel.add(mainMenuButton);
        buttonPanel.add(slotsButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setContentPane(contentPanel);

        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.getRootPane().setOpaque(false);

        dialog.setVisible(true);
    }

    private JButton createButton(String text, java.awt.event.ActionListener onClick) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);

        button.addActionListener(e -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav");
            onClick.actionPerformed(e);
        });

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
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(e -> onClick.run());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(hoverColor);
                audioPlayer.playSFX("audio/sfx/hover_sound.wav");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
    }
}
