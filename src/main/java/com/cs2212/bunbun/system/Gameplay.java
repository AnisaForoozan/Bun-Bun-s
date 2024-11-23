package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Gameplay extends JPanel {
    private AudioPlayer audioPlayer;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Gameplay(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.cardLayout = cardLayout; // Store the CardLayout instance
        this.mainPanel = mainPanel; // Store the mainPanel instance
        this.audioPlayer = audioPlayer; // Store the AudioPlayer instance

        setBackground(new Color(0xE8CAE8)); // Background color
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

        // Add 4 slots to the panel
        for (int i = 0; i < 4; i++) {
            SlotButton slotButton = createSlotButton("Create New Slot", i);
            slotsPanel.add(slotButton, gbc);
            gbc.gridy++; // Move to the next row
        }

        add(slotsPanel, BorderLayout.CENTER); // Add the slots panel to the center
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
            handleSlotClick(slotIndex, e);
        });

        return slotButton;
    }

    private void handleSlotClick(int slotIndex, ActionEvent e) {
        // Get the parent JFrame
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);

        // Create a custom modal dialog
        JDialog dialog = new JDialog(parentFrame, true);
        dialog.setUndecorated(true); // Remove title bar and close/maximize/minimize buttons
        dialog.setSize(450, 200); // Set dialog size
        dialog.setLocationRelativeTo(this); // Center on the Gameplay panel

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

        // Label for the confirmation message
        JLabel messageLabel = new JLabel("Are you sure?", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        messageLabel.setForeground(Color.WHITE); // White text
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        // Yes button
        JButton yesButton = new JButton("Yes");
        styleDialogButton(yesButton, new Color(232, 202, 232), () -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav"); // Play click sound
            dialog.dispose(); // Close the dialog
            cardLayout.show(mainPanel, "PetSelection"); // Switch to PetSelection panel
        });

        // No button
        JButton noButton = new JButton("No");
        styleDialogButton(noButton, new Color(232, 202, 232), () -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav"); // Play click sound
            dialog.dispose(); // Close the dialog
        });

        // Add buttons to the button panel
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

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
            setBackground(new Color(135, 135, 135, 50));  // Base background color
            setFocusPainted(false);
            setBorder(BorderFactory.createLineBorder(new Color(117, 101, 81), 5)); // White border
            setPreferredSize(new Dimension(400, 100)); // Set button size
            setContentAreaFilled(false); // Disable default background fill
        }

        public void setHovered(boolean hovered) {
            this.isHovered = hovered;
            repaint(); // Trigger repaint to update hover effect
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Draw background color
            if (isHovered) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(135, 135, 135, 100)); // Semi-transparent gray
                g2d.fillRect(0, 0, getWidth(), getHeight()); // Draw hover background
                g2d.dispose();
            } else {
                g.setColor(getBackground()); // Base background
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // Draw button text
            super.paintComponent(g); // Call JButton's paintComponent to handle the text rendering
        }
    }
}
