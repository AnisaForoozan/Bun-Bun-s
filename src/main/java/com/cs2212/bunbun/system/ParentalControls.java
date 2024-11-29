package com.cs2212.bunbun.system;

import com.cs2212.bunbun.gameplay.GameSaveManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ParentalControls extends JPanel {
    private AudioPlayer audioPlayer;
    private Image backgroundImage;

    private CardLayout layout;
    private JPanel contentPanel;

    public ParentalControls(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;

        // Load the background image
        URL resource = getClass().getResource("/images/dimbackground.png");
        if (resource != null) {
            backgroundImage = new ImageIcon(resource).getImage();
        } else {
            System.err.println("Background image not found!");
        }

        setLayout(new BorderLayout());

        // Back button
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);

        JButton backButton = createButton("⬅", e -> cardLayout.show(mainPanel, "MainMenu"));
        topLeftPanel.add(backButton);
        add(topLeftPanel, BorderLayout.NORTH);

        // Content Panel with CardLayout
        layout = new CardLayout();
        contentPanel = new JPanel(layout);
        contentPanel.setOpaque(false);

        // Add the three layouts
        contentPanel.add(createEnterPasswordPanel(), "Layout1");
        contentPanel.add(createPasswordSetupPanel(), "Layout2");
        contentPanel.add(createProtectedContentPanel(), "Layout3");

        // Start with Layout 1
        layout.show(contentPanel, "Layout1");

        add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JPanel createEnterPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel instructionLabel = new JLabel("Enter Password:");
        instructionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        instructionLabel.setForeground(Color.WHITE);
        panel.add(instructionLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        panel.add(passwordField, gbc);

        JButton enterButton = createCustomButton("Enter", e -> {
            Map<String, String> saveData = GameSaveManager.loadSaveData();
            String savedPassword = saveData.get("parental_password");

            if (savedPassword == null) {
                JOptionPane.showMessageDialog(this, "You do not have a password created.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String enteredPassword = new String(passwordField.getPassword());
                if (savedPassword.equals(enteredPassword)) {
                    layout.show(contentPanel, "Layout3"); // Go to protected content
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton createPasswordButton = createCustomButton("Create Password", e -> layout.show(contentPanel, "Layout2"));
        panel.add(enterButton, gbc);
        panel.add(createPasswordButton, gbc);

        return panel;
    }

    private JPanel createPasswordSetupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel instructionLabel = new JLabel("Create a New Password:");
        instructionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        instructionLabel.setForeground(Color.WHITE);
        panel.add(instructionLabel, gbc);

        JPasswordField newPasswordField = new JPasswordField(15);
        newPasswordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        panel.add(newPasswordField, gbc);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        confirmLabel.setForeground(Color.WHITE);
        panel.add(confirmLabel, gbc);

        JPasswordField confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        panel.add(confirmPasswordField, gbc);

        JButton confirmButton = createCustomButton("Confirm", e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Passwords cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Save the password
                Map<String, String> saveData = GameSaveManager.loadSaveData();
                saveData.put("parental_password", newPassword);
                GameSaveManager.saveUpdatedData(saveData);

                JOptionPane.showMessageDialog(this, "Password successfully created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                layout.show(contentPanel, "Layout1");
            }
        });

        panel.add(confirmButton, gbc);

        // Add Back button below Confirm button
        JButton backButton = createCustomButton("Back", e -> layout.show(contentPanel, "Layout1"));
        panel.add(backButton, gbc);

        return panel;
    }

    private JPanel createProtectedContentPanel() {
        return createIconButtonPanel(
                List.of("Time Limits", "Statistics", "Revive Pet"),
                List.of("/images/hourglass.png", "/images/chart.png", "/images/bunny.png")
        );


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
                button.setForeground(new Color(117, 101, 81));
                audioPlayer.playSFX("audio/sfx/hover_sound.wav");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });

        return button;
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

    // Method to reset the panel to Layout1
    public void resetToLayout1() {
        layout.show(contentPanel, "Layout1");
    }

}
