package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class ParentalControls extends JPanel {
    private AudioPlayer audioPlayer;
    private Image backgroundImage;

    public ParentalControls(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;

        // Load the background image
        URL resource = getClass().getResource("/images/dimbackground.png"); // Replace with your image path
        if (resource != null) {
            backgroundImage = new ImageIcon(resource).getImage();
        } else {
            System.err.println("Background image not found!");
        }

        // Set layout
        setLayout(new BorderLayout());

        // Create a panel for the back button and add it to the top-left
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);

        JButton backButton = createButton("⬅", e -> cardLayout.show(mainPanel, "MainMenu"));

        topLeftPanel.add(backButton);
        add(topLeftPanel, BorderLayout.NORTH);

        // Layout to choose: Uncomment the relevant layout section as needed
        // Layout 1: Simple Two Buttons
        add(createButtonPanel(List.of("Enter Password", "Create Password")), BorderLayout.CENTER);

        // Layout 2: Create and Confirm Password Buttons
        //add(createButtonPanel(List.of("Create New Password", "Confirm New Password")), BorderLayout.CENTER);

        // Layout 3: Buttons with Icons
//        add(createIconButtonPanel(
//                List.of("Time Limits", "Statistics", "Revive Pet"),
//                List.of("hourglass.png", "chart.png", "bunny.png") // Replace with actual icon paths
//        ), BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
        }
    }

    private JPanel createButtonPanel(List<String> buttonLabels) {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);

        for (String label : buttonLabels) {
            JButton button = createCustomButton(label, e -> {
                // Add action listener functionality for each button
            });
            buttonPanel.add(button, gbc);
        }

        return buttonPanel;
    }

    private JPanel createIconButtonPanel(List<String> labels, List<String> iconPaths) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            String iconPath = iconPaths.get(i);

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setOpaque(false);

            JLabel iconLabel = new JLabel(new ImageIcon(iconPath)); // Replace with actual icons
            JButton button = createCustomButton(label, e -> {
                // Add functionality for each button
            });

            row.add(iconLabel);
            row.add(button);
            panel.add(row, gbc);
        }

        return panel;
    }

    private JButton createCustomButton(String text, java.awt.event.ActionListener onClick) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background fill
                g2.setColor(new Color(135, 135, 135, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Border
                g2.setColor(new Color(117, 101, 81));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

                super.paintComponent(g2);
            }
        };

        button.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.addActionListener(e -> {
            audioPlayer.playSFX("audio/sfx/click_sound.wav");
            onClick.actionPerformed(e);
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(117, 101, 81));; // Hover effect
                audioPlayer.playSFX("audio/sfx/hover_sound.wav");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });

        return button;
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
}
