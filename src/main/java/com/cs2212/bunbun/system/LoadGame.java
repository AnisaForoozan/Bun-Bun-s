package com.cs2212.bunbun.system;

import com.cs2212.bunbun.gameplay.GameSaveManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.io.File;
import java.io.IOException;

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

        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
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

        // Content panel with rounded rectangle design
        JPanel contentPanel = new JPanel(null) {
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

        if (GameSaveManager.isGameplayLocked()) {
            JOptionPane.showMessageDialog(this, "Gameplay is currently locked due to time restrictions.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return; // Prevent further actions
        }

        if (saveData.containsKey(slotKey)) {
            int petHealth = GameSaveManager.getPetHealth(slotKey); // Get the pet's health

            if (petHealth == 0) { // Check if the pet is dead
                JOptionPane.showMessageDialog(this, "This pet has died. You can rename or delete the slot.", "Pet Died", JOptionPane.ERROR_MESSAGE);
            }

            dialog.setSize(450, 200); // Set the dialog size

            // Back button (as an "X")
            JButton backButton = new JButton("X");
            styleDialogButton(backButton, new Color(232, 202, 232), dialog::dispose);
            backButton.setFont(new Font("Comic Sans MS", Font.BOLD, 18)); // Ensure proper font
            backButton.setForeground(Color.WHITE);
            backButton.setFocusPainted(false);
            backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            backButton.setContentAreaFilled(false); // Make it transparent
            backButton.setOpaque(false);
            backButton.setBounds(10, 10, 50, 30); // Adjust bounds to give enough space
            contentPanel.add(backButton);

            // Slot is taken - add "Play," "Rename," and "Delete" buttons centered
            JButton playButton = new JButton(petHealth == 0 ? "Pet is Dead" : "Play");
            styleDialogButton(playButton, new Color(232, 202, 232), () -> {
                if (petHealth > 0) {
                    dialog.dispose();
                    navigateToGameplay(slotKey);
                    showLoadingScreenAndSwitchPanel("Gameplay");
                } else {
                    JOptionPane.showMessageDialog(this, "This pet has died and cannot be played.", "Pet Died", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton renameButton = new JButton("Rename");
            styleDialogButton(renameButton, new Color(232, 202, 232), () -> {
                String newName = JOptionPane.showInputDialog(this, "Enter new name:", "Rename Pet", JOptionPane.PLAIN_MESSAGE);
                if (newName != null && !newName.trim().isEmpty()) {
                    renameSlot(slotKey, newName.trim());
                }
                dialog.dispose();
            });

            JButton deleteButton = new JButton("Delete");
            styleDialogButton(deleteButton, new Color(232, 202, 232), () -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this slot?", "Delete Slot", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteSlot(slotKey);
                }
                dialog.dispose();
            });

            // Use GridBagLayout for centering the buttons
            JPanel buttonPanel = new JPanel(new GridBagLayout());
            buttonPanel.setOpaque(false);
            buttonPanel.setBounds(0, 60, 450, 100); // Positioning within the content panel

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 10, 0, 10); // Add spacing between buttons

            buttonPanel.add(playButton, gbc);

            gbc.gridx++;
            buttonPanel.add(renameButton, gbc);

            gbc.gridx++;
            buttonPanel.add(deleteButton, gbc);

            contentPanel.add(buttonPanel);

        } else {
            dialog.setSize(450, 200); // Smaller size for empty slot confirmation

            // Slot is free - "Are you sure?" text at the center
            JLabel messageLabel = new JLabel("Are you sure?", SwingConstants.CENTER);
            messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            messageLabel.setForeground(Color.WHITE);
            messageLabel.setBounds(0, 50, 450, 30); // Center the label vertically and horizontally
            contentPanel.add(messageLabel);

            JButton yesButton = new JButton("Yes");
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

            JButton noButton = new JButton("No");
            styleDialogButton(noButton, new Color(232, 202, 232), dialog::dispose);

            // Position "YES" and "NO" buttons at the bottom
            JPanel yesNoPanel = new JPanel(new GridBagLayout());
            yesNoPanel.setOpaque(false);
            yesNoPanel.setBounds(0, 130, 450, 50); // Positioned at the bottom of the dialog

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.insets = new Insets(0, 10, 0, 10); // Spacing between buttons

            yesNoPanel.add(yesButton, gbc);

            gbc.gridx++;
            yesNoPanel.add(noButton, gbc);

            contentPanel.add(yesNoPanel);
        }

        dialog.setContentPane(contentPanel);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.getRootPane().setOpaque(false);
        dialog.setLocationRelativeTo(this); // Center the dialog on the parent panel

        dialog.setVisible(true);
    }




    private void renameSlot(String slotKey, String newName) {
        Map<String, String> saveData = GameSaveManager.loadSaveData();
        String petData = saveData.get(slotKey); // Get the existing "name:type" format

        if (petData != null && petData.contains(":")) {
            String petType = petData.split(":")[1]; // Extract the type (after the ":")
            GameSaveManager.saveData(slotKey, newName, petType); // Save the new name with the same type
        }
        updateSlots(); // Refresh the UI
    }


    private void deleteSlot(String slotKey) {
        Map<String, String> saveData = GameSaveManager.loadSaveData();
        if (saveData.containsKey(slotKey)) {
            saveData.remove(slotKey); // Remove the slot from the map
            GameSaveManager.saveUpdatedData(saveData); // Save the updated map
        }
        updateSlots(); // Refresh the UI
    }




    private void navigateToGameplay(String slotKey) {
        String bunnyName = GameSaveManager.getPetName(slotKey);
        String petType = GameSaveManager.getPetType(slotKey);

        if (bunnyName == null || petType == null) {
            System.out.println("Invalid data for slot: " + slotKey);
            return;
        }

        // Initialize Gameplay with the saved data
        Gameplay gameplayPanel = new Gameplay(cardLayout, mainPanel, audioPlayer, bunnyName, petType, slotKey);
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
            String slotKey = "Slot " + (i + 1); // Generate the slot key (e.g., "Slot 1")
            if (saveData.containsKey(slotKey)) {
                String petData = saveData.get(slotKey); // Get the raw data (name:type)
                String petName = petData.split(":")[0]; // Extract only the name (before the ":")
                int petHealth = GameSaveManager.getPetHealth(slotKey); // Get the pet's health
                String healthStatus = (petHealth > 0) ? "" : " (Dead)";
                slotButtons[i].setText("Slot " + (i + 1) + ": " + petName + healthStatus);
            } else {
                slotButtons[i].setText("Choose your slot"); // Show "Choose your slot" for empty slots
            }
        }

        revalidate(); // Refresh the UI
        repaint();
    }


    public void refreshLoadGameUI() {
        updateSlots(); // Use the same logic as updateSlots
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