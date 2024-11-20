package com.cs2212.bunbun.system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Settings extends JPanel {
    private AudioPlayer audioPlayer;

    public Settings(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;

        setBackground(new Color(0xE8CAE8)); // Main panel background
        setLayout(new BorderLayout());

        // Create a panel for the back button and add it to the top-left
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);

        JButton backButton = new JButton("⬅");
        backButton.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(new Color(117, 101, 81));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        topLeftPanel.add(backButton);
        add(topLeftPanel, BorderLayout.NORTH);

        // Create a panel for the title and sliders
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 20, 0); // Add general spacing between components
        gbc.anchor = GridBagConstraints.NORTH; // Align everything to the top

        // Add title label
        gbc.gridy = 0; // Title at the very top
        gbc.insets = new Insets(-120, 0, 20, 0); // Add negative insets to move the title higher
        JLabel titleLabel = new JLabel("Audio");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 255, 255));
        centerPanel.add(titleLabel, gbc);

        // Add volume sliders
        gbc.gridy = 1; // Sliders below the title
        gbc.insets = new Insets(-20, 0, 0, 0); // Adjust spacing between title and sliders
        JPanel slidersPanel = new JPanel();
        slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
        slidersPanel.setOpaque(false);

        // Create sliders
        slidersPanel.add(createVolumeControl("Master Volume", 100, value -> {
            float volume = convertSliderValueToDecibels(value);
            audioPlayer.setVolume(volume); // Master volume control
        }));
        slidersPanel.add(Box.createVerticalStrut(20)); // Add space between sliders

        slidersPanel.add(createVolumeControl("Music Volume", 100, value -> {
            // Add functionality for music volume (if different from master)
        }));
        slidersPanel.add(Box.createVerticalStrut(20)); // Add space between sliders

        slidersPanel.add(createVolumeControl("SFX Volume", 100, value -> {
            // Add functionality for SFX volume
        }));

        centerPanel.add(slidersPanel, gbc);

        // Add center panel to the screen
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a volume control slider with a label.
     *
     * @param labelText The text for the label (e.g., "Master Volume").
     * @param initialValue The initial slider value (0-100).
     * @param onChange Function to execute on slider value change.
     * @return A JPanel containing the label and slider.
     */
    private JPanel createVolumeControl(String labelText, int initialValue, java.util.function.Consumer<Integer> onChange) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false); // Transparent background

        // Create the label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.WEST);

        // Create the slider
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, initialValue);
        slider.setOpaque(false);
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setUI(new CustomSliderUI(slider)); // Apply custom slider UI

        slider.addChangeListener(e -> onChange.accept(slider.getValue()));
        panel.add(slider, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Converts a slider value (0-100) to decibels (-80.0 to 6.0).
     */
    private float convertSliderValueToDecibels(int value) {
        if (value == 0) {
            return -80.0f; // Mute at 0
        }
        return (float) (Math.log10(value / 100.0) * 20); // Convert to decibel range
    }
}
