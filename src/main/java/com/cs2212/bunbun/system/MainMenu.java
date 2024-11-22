package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AudioPlayer audioPlayer;
    private ImageIcon backgroundImage; // Class-level field for background image

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
        JPanel menuPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        menuPanel.setOpaque(false); // Ensure transparency

        GridBagConstraints gbc = new GridBagConstraints();

        // Add title image
        URL imageUrl = getClass().getClassLoader().getResource("images/image.png");
        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(400, 200, Image.SCALE_SMOOTH);
            JLabel titleLabel = new JLabel(new ImageIcon(scaledImage));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 40, 0);
            gbc.anchor = GridBagConstraints.PAGE_START;
            menuPanel.add(titleLabel, gbc);
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
        JButton newGameButton = createMenuButton("NEW GAME");
        JButton loadGameButton = createMenuButton("LOAD GAME");
        JButton tutorialButton = createMenuButton("TUTORIAL");
        JButton parentalControlsButton = createMenuButton("PARENTAL CONTROLS");
        JButton settingsButton = createMenuButton("SETTINGS");
        JButton exitButton = createMenuButton("EXIT");

        // Add button actions
        newGameButton.addActionListener(e -> cardLayout.show(mainPanel, "PetSelection"));
        loadGameButton.addActionListener(e -> cardLayout.show(mainPanel, "Gameplay"));
        parentalControlsButton.addActionListener(e -> cardLayout.show(mainPanel, "ParentalControls"));
        tutorialButton.addActionListener(e -> cardLayout.show(mainPanel, "Tutorial"));
        settingsButton.addActionListener(e -> cardLayout.show(mainPanel, "Settings"));
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to the button panel
        buttonPanel.add(newGameButton);
        buttonPanel.add(loadGameButton);
        buttonPanel.add(tutorialButton);
        buttonPanel.add(parentalControlsButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);

        // Add button panel to the menuPanel
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        menuPanel.add(buttonPanel, gbc);

        return menuPanel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0xE8CAE8));
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2));

        // Add sound effects
        button.addActionListener(e -> audioPlayer.playSFX("audio/sfx/click_sound.wav"));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
