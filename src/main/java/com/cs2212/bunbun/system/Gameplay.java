package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class Gameplay extends JPanel {
    private AudioPlayer audioPlayer;

    public Gameplay(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer; // Store the AudioPlayer instance

        setBackground(new Color(0xE8CAE8));
        setLayout(new BorderLayout());

        // Create a panel for the back button and add it to the top-left
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);

        JButton backButton = createButton("⬅", e -> cardLayout.show(mainPanel, "MainMenu"));

        topLeftPanel.add(backButton);
        add(topLeftPanel, BorderLayout.NORTH); // Add back button panel to the top

        // Create a panel for the title with GridBagLayout
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false); // Make the panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Center horizontally
        gbc.gridy = 0; // Center vertically
        gbc.insets = new Insets(0, 0, 40, 0); // Add padding
        gbc.anchor = GridBagConstraints.CENTER; // Center the label

        // Add title label
        JLabel titleLabel = new JLabel("Bunny hop hop yes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 255, 255));
        titlePanel.add(titleLabel, gbc);

        // Add the title panel to the center of the screen
        add(titlePanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, java.awt.event.ActionListener onClick) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(new Color(117, 101, 81));

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
