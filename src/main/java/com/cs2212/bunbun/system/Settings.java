package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;

public class Settings extends JPanel {
    private AudioPlayer audioPlayer;
    private Image backgroundImage;

    public Settings(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;

        // Load the background image
        backgroundImage = new ImageIcon(getClass().getResource("/images/dimbackground.png")).getImage();

        setLayout(new BorderLayout());

        // Back Button
        JButton backButton = createButton("⬅", e -> cardLayout.show(mainPanel, "MainMenu"));
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);
        topLeftPanel.add(backButton);
        add(topLeftPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Title
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Audio Settings");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        centerPanel.add(titleLabel, gbc);

        // Sliders
        gbc.gridy = 1;
        JPanel slidersPanel = new JPanel();
        slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
        slidersPanel.setOpaque(false);

        slidersPanel.add(createVolumeControl("Master Volume", 50, value -> {
            float volume = convertSliderValueToDecibels(value);
            audioPlayer.setMasterVolume(volume);
        }));
        slidersPanel.add(Box.createVerticalStrut(20));

        slidersPanel.add(createVolumeControl("Music Volume", 50, value -> {
            float volume = convertSliderValueToDecibels(value);
            audioPlayer.setMusicVolume(volume);
        }));
        slidersPanel.add(Box.createVerticalStrut(20));

        slidersPanel.add(createVolumeControl("SFX Volume", 50, value -> {
            float volume = convertSliderValueToDecibels(value);
            audioPlayer.setSFXVolume(volume);
        }));

        centerPanel.add(slidersPanel, gbc);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Draw the background image, scaled to fit the panel
        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        g2d.dispose();
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

    private JPanel createVolumeControl(String labelText, int initialValue, java.util.function.Consumer<Integer> onChange) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.WEST);

        JSlider slider = new JSlider(0, 100, initialValue);
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setUI(new CustomSliderUI(slider)); // Custom slider UI
        slider.setOpaque(false);
        slider.addChangeListener(e -> onChange.accept(slider.getValue()));

        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }

    private float convertSliderValueToDecibels(int value) {
        if (value == 0) return -80.0f; // Minimum decibel value for muting
        return (float) (Math.log10(value / 100.0) * 20); // Convert percentage to decibels
    }
}

