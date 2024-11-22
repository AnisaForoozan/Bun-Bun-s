package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class Tutorial extends JPanel {
    private AudioPlayer audioPlayer;

    public Tutorial(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;

        setBackground(new Color(0xE8CAE8));
        setLayout(new BorderLayout());

        // Back Button
        JButton backButton = createButton("⬅", e -> cardLayout.show(mainPanel, "MainMenu"));
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

        JLabel titleLabel = new JLabel("Tutorial");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, gbc);

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
