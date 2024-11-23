package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AudioPlayer audioPlayer;
    private ImageIcon backgroundImage; // Class-level field for background image
    private JDialog infoDialog; // For the custom dialog

    public MainMenu() {
        audioPlayer = new AudioPlayer();
        audioPlayer.playMusic("audio/music/menu_music.wav", true); // Background music

        // Set up the frame
        setTitle("Bun bun");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Initialize CardLayout and Main Panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add the Main Menu, Gameplay, and other panels
        mainPanel.add(createMainMenuPanel(), "MainMenu");
        mainPanel.add(new PetSelection(cardLayout, mainPanel, audioPlayer), "PetSelection");
        mainPanel.add(new Gameplay(cardLayout, mainPanel, audioPlayer), "Gameplay");
        mainPanel.add(new Tutorial(cardLayout, mainPanel, audioPlayer), "Tutorial");
        mainPanel.add(new ParentalControls(cardLayout, mainPanel, audioPlayer), "ParentalControls");
        mainPanel.add(new Settings(cardLayout, mainPanel, audioPlayer), "Settings");

        // Add the main panel to the frame
        add(mainPanel);

        // Show the Main Menu initially
        cardLayout.show(mainPanel, "MainMenu");
    }

    private JPanel createMainMenuPanel() {
        // Load the background image
        URL backgroundUrl = getClass().getClassLoader().getResource("images/menubackground.png");
        if (backgroundUrl != null) {
            backgroundImage = new ImageIcon(backgroundUrl);
        } else {
            System.err.println("Background image not found!");
        }

        // Create the main menu panel with a custom background
        JPanel menuPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        menuPanel.setOpaque(false); // Ensure transparency

        // Create the central panel for the buttons and title
        JPanel centralPanel = new JPanel(new GridBagLayout());
        centralPanel.setOpaque(false); // Transparent background
        GridBagConstraints gbc = new GridBagConstraints();

        // Add title image
        URL imageUrl = getClass().getClassLoader().getResource("images/image.png");
        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(400, 200, Image.SCALE_SMOOTH);
            JLabel titleLabel = new JLabel(new ImageIcon(scaledImage));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(20, 0, 40, 0); // Adjust top spacing
            gbc.anchor = GridBagConstraints.PAGE_START;
            centralPanel.add(titleLabel, gbc);
        }

        // Create the custom button panel with semi-transparent gray background
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(135, 135, 135, 50)); // Semi-transparent gray
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); // Rounded rectangle
                g2d.dispose();
            }
        };
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 40)); // Stack buttons vertically with 40px spacing
        buttonPanel.setOpaque(false); // Transparent background to allow custom painting

        // Add padding inside the panel and a border around it
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(117, 101, 81), 5),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Create buttons
        JButton playGameButton = createMenuButton("PLAY", "Gameplay");
        JButton tutorialButton = createMenuButton("HOW TO PLAY", "Tutorial");
        JButton settingsButton = createMenuButton("SETTINGS", "Settings");
        JButton parentalControlsButton = createMenuButton("PARENTAL CONTROLS", "ParentalControls");
        JButton exitButton = createMenuButton("EXIT", null);

        // Add buttons to the button panel
        buttonPanel.add(playGameButton);
        buttonPanel.add(tutorialButton);
        buttonPanel.add(parentalControlsButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);

        // Add button panel to the centralPanel
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 0, 10, 0); // Adjust top and bottom spacing
        gbc.anchor = GridBagConstraints.CENTER;
        centralPanel.add(buttonPanel, gbc);

        // Add central panel to the center of the menuPanel
        menuPanel.add(centralPanel, BorderLayout.CENTER);

        // Create and add the info button to the bottom-right
        JButton infoButton = new JButton("?");
        infoButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        infoButton.setForeground(Color.WHITE); // Default color to white
        infoButton.setBackground(new Color(117, 101, 81));
        infoButton.setFocusPainted(false);
        infoButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add hover and click sounds
        infoButton.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color defaultColor = Color.WHITE; // Default foreground color
            private final Color hoverColor = new Color(0x756551); // Hover color

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                infoButton.setForeground(hoverColor); // Change foreground on hover
                audioPlayer.playSFX("audio/sfx/hover_sound.wav"); // Play hover sound
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                infoButton.setForeground(defaultColor); // Revert to default foreground
            }
        });

        infoButton.addActionListener(e -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav"); // Play click sound

            // Create a custom undecorated dialog
            JDialog infoDialog = new JDialog((Window) SwingUtilities.getWindowAncestor(this), Dialog.ModalityType.APPLICATION_MODAL);
            infoDialog.setUndecorated(true); // Remove title bar and buttons
            infoDialog.setSize(400, 300); // Set dialog size
            infoDialog.setLocationRelativeTo(this); // Center on the parent frame
            infoDialog.setBackground(new Color(0, 0, 0, 0)); // Fully transparent background

            // Custom panel with rounded corners
            JPanel messagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(117, 101, 81)); // Background color
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rounded rectangle
                    g2d.dispose();
                }
            };
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding
            messagePanel.setOpaque(false); // Make the panel transparent

            // Create the "X" close button
            JButton closeButton = new JButton("X");
            closeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            closeButton.setContentAreaFilled(false);
            closeButton.setOpaque(false);

            closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
                private final Color hoverColor = new Color(232, 202, 232); // Hover color for the button

                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    closeButton.setForeground(hoverColor);
                    audioPlayer.playSFX("audio/sfx/hover_sound.wav"); // Play hover sound
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    closeButton.setForeground(Color.WHITE);
                }
            });

// Add action listener with click sound
            closeButton.addActionListener(closeEvent -> {
                audioPlayer.playSFX("audio/sfx/click_sound.wav"); // Play click sound
                infoDialog.dispose(); // Close dialog on click
            });

            closeButton.addActionListener(closeEvent -> infoDialog.dispose()); // Close dialog on click

            // Top panel for the close button
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setOpaque(false);
            topPanel.add(closeButton);

            // Create the message content
            JLabel creditsLabel = new JLabel("<html><div style='text-align: center;'>"
                    + "Created by Group 28:<br>"
                    + "Anisa Lua Foroozan, Anya Ziyan Liu<br>"
                    + "Janreve Salubre, Jared Rone Costales<br>"
                    + "and Joseph Taining Huang<br><br>"
                    + "CS2212 Fall 2024 at Western University"
                    + "</div></html>");
            creditsLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            creditsLabel.setForeground(Color.WHITE);
            creditsLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Add components to the message panel
            messagePanel.add(topPanel, BorderLayout.NORTH); // Close button at the top
            messagePanel.add(creditsLabel, BorderLayout.CENTER); // Message content in the center

            infoDialog.add(messagePanel); // Add the message panel to the dialog
            infoDialog.setVisible(true); // Show the dialog
        });

        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRightPanel.setOpaque(false); // Transparent background
        bottomRightPanel.add(infoButton);

        // Add the bottom-right panel to the menuPanel
        menuPanel.add(bottomRightPanel, BorderLayout.SOUTH);

        return menuPanel;
    }

    private JButton createMenuButton(String text, String targetPanel) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2));

        // Add sound effects
        button.addActionListener(e -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav");
            if ("EXIT".equals(text)) {
                System.exit(0); // Exit the application
            } else if (targetPanel != null) {
                showLoadingScreenAndSwitchPanel(targetPanel);
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color defaultColor = button.getForeground(); // Default foreground color
            private final Color hoverColor = new Color(0x756551);      // Hover color

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(hoverColor); // Change foreground on hover
                audioPlayer.playSFX("audio/sfx/hover_sound.wav"); // Play hover sound
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(defaultColor); // Revert to default foreground
            }
        });

        return button;
    }

    private void showLoadingScreenAndSwitchPanel(String targetPanel) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
