package com.cs2212.bunbun.system;

import com.cs2212.bunbun.gameplay.GameSaveManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

public class LoadGame extends JPanel {
    private AudioPlayer audioPlayer;
    private Image backgroundImage;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private SlotButton[] slotButtons;

    public LoadGame(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.cardLayout = cardLayout; // Store the CardLayout instance
        this.mainPanel = mainPanel; // Store the mainPanel instance
        this.audioPlayer = audioPlayer; // Store the AudioPlayer instance

        // Load the background image
        backgroundImage = new ImageIcon(getClass().getResource("/images/dimbackground.png")).getImage();

        setLayout(new BorderLayout());

        // Create a panel for the back button and add it to the top-left
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);

        JButton backButton = createButton("⬅", e -> cardLayout.show(mainPanel, "MainMenu"));

        topLeftPanel.add(backButton);
        add(topLeftPanel, BorderLayout.NORTH); // Add back button panel to the top

        // Create a panel for the slots
        JPanel slotsPanel = new JPanel(new GridBagLayout());
        slotsPanel.setOpaque(false); // Transparent background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0); // Spacing between slots
        gbc.anchor = GridBagConstraints.CENTER;

        // Initialize slot buttons
        slotButtons = new SlotButton[4];
        for (int i = 0; i < 4; i++) {
            slotButtons[i] = createSlotButton("Choose your slot", i);
            slotsPanel.add(slotButtons[i], gbc);
            gbc.gridy++; // Move to the next row
        }

        // Update slot text based on saved data
        updateSlots();

        add(slotsPanel, BorderLayout.CENTER); // Add the slots panel to the center
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Draw the background image, scaled to fit the panel
        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        g2d.dispose();
    }

    private SlotButton createSlotButton(String text, int slotIndex) {
        SlotButton slotButton = new SlotButton(text);

        // Add hover effects using the custom hover state
        slotButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                slotButton.setHovered(true); // Enable hover effect
                audioPlayer.playSFX("audio/sfx/hover_sound.wav"); // Play hover sound
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                slotButton.setHovered(false); // Disable hover effect
            }
        });

        // Add click sound and action
        slotButton.addActionListener(e -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav");
            handleSlotClick(slotIndex);
        });

        return slotButton;
    }

    private void handleSlotClick(int slotIndex) {
        String slotKey = "Slot " + (slotIndex + 1); // Determine the slot key based on index
        Map<String, String> saveData = GameSaveManager.loadSaveData();

        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);

        // Create a custom modal dialog
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
                g2d.setColor(new Color(117, 101, 81)); // Dialog background color
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50); // Rounded edges
                g2d.dispose();
            }
        };

        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        messageLabel.setForeground(Color.WHITE);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        // Check if the slot is already taken
        if (saveData.containsKey(slotKey)) {
            messageLabel.setText("This slot is already taken.");

            JButton playButton = new JButton("Play");
            styleDialogButton(playButton, new Color(232, 202, 232), () -> {
                dialog.dispose();
                navigateToGameplay(slotKey);
            });

            JButton backButton = new JButton("Back");
            styleDialogButton(backButton, new Color(232, 202, 232), dialog::dispose);

            buttonPanel.add(playButton);
            buttonPanel.add(backButton);

        } else {
            messageLabel.setText("Are you sure?");

            JButton yesButton = new JButton("YES");
            styleDialogButton(yesButton, new Color(232, 202, 232), () -> {
                dialog.dispose();
                for (Component comp : mainPanel.getComponents()) {
                    if (comp instanceof PetSelection) {
                        ((PetSelection) comp).setSelectedSlot(slotKey); // Pass slotKey to PetSelection
                        break;
                    }
                }
                showLoadingScreenAndSwitchPanel("PetSelection"); // Navigate to PetSelection
            });

            JButton noButton = new JButton("NO");
            styleDialogButton(noButton, new Color(232, 202, 232), dialog::dispose);

            buttonPanel.add(yesButton);
            buttonPanel.add(noButton);
        }

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(contentPanel);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.getRootPane().setOpaque(false);

        dialog.setVisible(true);
    }

    private void navigateToGameplay(String slotKey) {
        String bunnyName = GameSaveManager.getPetName(slotKey);
        String petType = GameSaveManager.getPetType(slotKey);

        if (bunnyName == null || petType == null) {
            System.out.println("Invalid data for slot: " + slotKey);
            return;
        }

        // Initialize Gameplay with the saved data
        Gameplay gameplayPanel = new Gameplay(cardLayout, mainPanel, audioPlayer, bunnyName, petType);
        mainPanel.add(gameplayPanel, "Gameplay");
        cardLayout.show(mainPanel, "Gameplay");
    }



    private void styleDialogButton(JButton button, Color hoverColor, Runnable onClick) {
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // Add click effect
        button.addActionListener(e -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav"); // Play click sound
            onClick.run();
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(hoverColor);
                audioPlayer.playSFX("audio/sfx/hover_sound.wav"); // Play hover sound
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
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

    public void updateSlots() {
        Map<String, String> saveData = GameSaveManager.loadSaveData();

        for (int i = 0; i < slotButtons.length; i++) {
            String slotKey = "Slot " + (i + 1);
            if (saveData.containsKey(slotKey)) {
                String petData = saveData.get(slotKey); // Get the raw data (name:type)
                String petName = petData.split(":")[0]; // Extract only the name (before the ":")
                slotButtons[i].setText(slotKey + ": " + petName); // Set the slot button text
            } else {
                slotButtons[i].setText("Choose your slot");
            }
        }

        revalidate(); // Refresh the UI
        repaint();
    }


    private JButton createButton(String text, java.awt.event.ActionListener onClick) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(new Color(255, 255, 255));

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

    private static class SlotButton extends JButton {
        private boolean isHovered = false;

        public SlotButton(String text) {
            super(text);
            setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            setForeground(Color.WHITE); // Keep text color white
            setFocusPainted(false);
            setPreferredSize(new Dimension(400, 100)); // Set button size
            setContentAreaFilled(false); // Disable default background fill
            setBorder(BorderFactory.createEmptyBorder()); // Remove default border
        }

        public void setHovered(boolean hovered) {
            this.isHovered = hovered;
            repaint(); // Trigger repaint to update hover effect
        }



        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Enable anti-aliasing for smooth edges
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw semi-transparent background
            g2d.setColor(new Color(135, 135, 135, isHovered ? 150 : 75)); // Brighter gray on hover
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); // Rounded rectangle

            // Draw brown border
            g2d.setColor(new Color(117, 101, 81)); // Brown color
            g2d.setStroke(new BasicStroke(2)); // Border thickness
            g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25); // Rounded border

            g2d.dispose();

            // Draw button text
            super.paintComponent(g);
        }
    }
}