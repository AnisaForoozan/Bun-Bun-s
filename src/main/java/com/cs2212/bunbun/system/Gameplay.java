package com.cs2212.bunbun.system;

import com.cs2212.bunbun.system.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Gameplay extends JPanel {
    private AudioPlayer audioPlayer;
    private PixelArtProgressBar sleepBar, happinessBar, hungerBar, healthBar, pointsBar;

    public Gameplay(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer, String petType, String petName) {
        this.audioPlayer = audioPlayer;

        setBackground(new Color(0xE8CAE8));
        setLayout(null);

        // Back button
        JButton backButton = createButton("<<", 20, 20, e -> cardLayout.show(mainPanel, "MainMenu"));
        add(backButton);

        // Progress Bars
        sleepBar = createProgressBar(20, 80, "Sleep", 0);
        happinessBar = createProgressBar(150, 80, "Happiness", 0);
        hungerBar = createProgressBar(280, 80, "Hunger", 0);
        healthBar = createProgressBar(410, 80, "Health", 100); // Start health bar at maximum
        pointsBar = createProgressBar(540, 80, "Points", 0);

        // Buttons for actions
        add(createButton("Take to Vet", 20, 200, e -> modifyBar(healthBar, 20)));
        add(createButton("Exercise", 250, 200, e -> modifyBar(happinessBar, 10)));
        add(createButton("Play", 20, 600, e -> modifyBar(happinessBar, 15)));
        add(createButton("Feed", 250, 600, e -> modifyBar(hungerBar, 10)));
        add(createButton("Give Gift", 480, 600, e -> modifyBar(pointsBar, 10)));
        add(createButton("Go To Bed", 710, 600, e -> modifyBar(sleepBar, 20)));

        // Sidebar Buttons
        add(createButton("Settings", 1700, 100, e -> showMessage("Settings clicked")));
        add(createButton("Inventory", 1700, 200, e -> showMessage("Inventory clicked")));
        add(createButton("Shop", 1700, 300, e -> showMessage("Shop clicked")));

        // Pet Placeholder
        ImageIcon bunny = new ImageIcon("src/main/resources/images/normal.png");
        JLabel petPlaceholder = new JLabel("Bunny Name", SwingConstants.CENTER);
        petPlaceholder.setIcon(bunny);
        petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
        petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM); // Text below icon
        petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
        petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        petPlaceholder.setBounds(893, 449, 300, 400);
        petPlaceholder.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(petPlaceholder);

        // Start automatic animations for the bars
        new Thread(() -> autoAnimateStat(sleepBar)).start();
        new Thread(() -> autoAnimateStat(happinessBar)).start();
        new Thread(() -> autoAnimateStat(hungerBar)).start();
    }

    private PixelArtProgressBar createProgressBar(int x, int y, String label, int initialValue) {
        PixelArtProgressBar bar = new PixelArtProgressBar(0, 100);
        bar.setBounds(x, y, 100, 20);
        bar.setValue(initialValue); // Start with the provided initial value
        add(bar);

        JLabel barLabel = new JLabel(label, SwingConstants.CENTER);
        barLabel.setBounds(x, y + 25, 100, 20);
        add(barLabel);

        return bar;
    }

    private JButton createButton(String text, int x, int y, ActionListener action) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 180, 45); // Set position and size
        button.addActionListener(action); // Add action listener
        return button;
    }

    private void modifyBar(PixelArtProgressBar bar, int delta) {
        int newValue = Math.max(0, Math.min(100, bar.getValue() + delta));
        bar.setValue(newValue);
    }

    private void autoAnimateStat(PixelArtProgressBar bar) {
        boolean increasing = true;
        try {
            while (true) {
                if (increasing) {
                    if (bar.getValue() < 100) {
                        bar.setValue(bar.getValue() + 1);
                    } else {
                        increasing = false; // Start decreasing after hitting 100
                    }
                } else {
                    if (bar.getValue() > 0) {
                        bar.setValue(bar.getValue() - 1);
                    }
                }
                Thread.sleep(100); // Speed of animation
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // Pixel Art Progress Bar Inner Class
    private class PixelArtProgressBar extends JProgressBar {
        private int segmentSize = 20;

        public PixelArtProgressBar(int min, int max) {
            super(min, max);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            int value = getValue();
            int totalSegments = width / segmentSize; // Total number of segments
            int filledSegments = (int) ((value / (float) getMaximum()) * totalSegments); // Filled segments

            // Paint the background
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, width, height);

            // Paint the progress segments
            for (int i = 0; i < totalSegments; i++) {
                int x = i * segmentSize; // Position of the segment
                if (i < filledSegments) {
                    g2d.setColor(getColorForValue(value)); // Filled segment color
                } else {
                    g2d.setColor(Color.DARK_GRAY); // Empty segment color
                }
                g2d.fillRect(x, 0, segmentSize - 2, height); // Draw the segment
            }
        }

        private Color getColorForValue(int value) {
            if (value <= 30) {
                return Color.RED;
            } else if (value <= 60) {
                return Color.ORANGE;
            } else if (value <= 90) {
                return Color.YELLOW;
            } else {
                return Color.GREEN;
            }
        }
    }
}
