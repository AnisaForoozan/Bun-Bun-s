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
    private JComboBox<String>[] units;
    private JSpinner[] spinners;
    private JPanel mainPanel;

    public ParentalControls(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.mainPanel = mainPanel;


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
        contentPanel.add(createTimeLimitsPanel(), "Layout4");

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
        JPanel panel = createIconButtonPanel(
                List.of("Time Limits", "Statistics", "Revive Pet"),
                List.of("/images/hourglass.png", "/images/chart.png", "/images/bunny.png")
        );

        // Add Back button to return to Layout1
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton backButton = createCustomButton("Back", e -> layout.show(contentPanel, "Layout1"));
        panel.add(backButton, gbc);

        // Add Time Limits button functionality
        JButton timeLimitsButton = (JButton) ((JPanel) panel.getComponent(0)).getComponent(1); // Assumes "Time Limits" is the first button
        timeLimitsButton.addActionListener(e -> layout.show(contentPanel, "Layout4")); // Switch to Layout4

        return panel;
    }

    private JPanel createTimeLimitsPanel() {
        String[] days = {"Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat"};
        units = new JComboBox[days.length];
        spinners = new JSpinner[days.length];

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Title or instruction
        JLabel titleLabel = new JLabel("Set Time Limits");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);

        JPanel rowsPanel = new JPanel(new GridBagLayout());
        rowsPanel.setOpaque(false);

        GridBagConstraints rowConstraints = new GridBagConstraints();
        rowConstraints.gridx = 0;
        rowConstraints.gridy = GridBagConstraints.RELATIVE;
        rowConstraints.insets = new Insets(10, 0, 10, 0);
        rowConstraints.fill = GridBagConstraints.HORIZONTAL; // Ensure rows fill the width
        rowConstraints.weightx = 1.0;

        for (int i = 0; i < days.length; i++) {
            final int index = i;

            JPanel row = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Draw semi-transparent gray background
                    g2d.setColor(new Color(135, 135, 135, 50)); // Semi-transparent gray
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                    // Draw border
                    g2d.setColor(new Color(117, 101, 81)); // Border color
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

                    g2d.dispose();
                }
            };
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(500, 50)); // Set uniform width for rows

            // Add constraints for the day label
            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.weightx = 0.2; // Space allocated for the day label
            labelConstraints.fill = GridBagConstraints.BOTH;
            labelConstraints.anchor = GridBagConstraints.WEST;
            labelConstraints.insets = new Insets(0, 30, 0, 10); // Padding: 30px from left edge, 10px right of label

            // Add day label
            JLabel dayLabel = new JLabel(days[i]);
            dayLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            dayLabel.setForeground(Color.WHITE);
            row.add(dayLabel, labelConstraints);

            // Add constraints for the control panel
            GridBagConstraints controlConstraints = new GridBagConstraints();
            controlConstraints.gridx = 1;
            controlConstraints.weightx = 0.8; // Space allocated for the control panel
            controlConstraints.fill = GridBagConstraints.BOTH;
            controlConstraints.anchor = GridBagConstraints.EAST;
            controlConstraints.insets = new Insets(0, 10, 0, 30); // Padding: 10px from spinner/combobox, 30px from right edge

            // Create a panel for spinner and combo box
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            controlPanel.setOpaque(false);

            // Add spinner
            spinners[i] = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
            spinners[i].setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            spinners[i].setOpaque(false);

            // Make spinner editor transparent and set text color to white
            JComponent editor = spinners[i].getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
                JTextField textField = defaultEditor.getTextField();
                textField.setBackground(new Color(0, 0, 0, 0)); // Transparent background
                textField.setOpaque(false);
                textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7)); // Add left padding for separation
                textField.setForeground(Color.WHITE); // Set text color to white
            }
            controlPanel.add(spinners[i]);


            // Add combo box with transparency fixes
            units[i] = new JComboBox<>(new String[]{"Hrs", "Min"});
            units[i].setFont(new Font("Comic Sans MS", Font.PLAIN, 18));

            // Ensure the renderer has consistent styling
            units[i].setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    label.setBackground(new Color(0, 0, 0, 0)); // Transparent background for dropdown items
                    label.setOpaque(false); // Ensure transparency
                    label.setForeground(Color.BLACK); // Black text for readability
                    label.setFont(new Font("Comic Sans MS", Font.PLAIN, 18)); // Consistent font
                    return label;
                }
            });

            // Ensure the dropdown arrow is transparent



            // Adjust spinner limits and reapply editor customizations
            units[i].addActionListener(e -> {
                boolean isHours = "Hrs".equals(units[index].getSelectedItem());
                spinners[index].setModel(new SpinnerNumberModel(1, 1, isHours ? 24 : 60, 1));

                // Get the updated editor for the spinner
                JComponent updatedEditor = spinners[index].getEditor();
                if (updatedEditor instanceof JSpinner.DefaultEditor) {
                    JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) updatedEditor;
                    JTextField textField = defaultEditor.getTextField();

                    // Customize text field properties
                    textField.setBackground(new Color(0, 0, 0, 0)); // Transparent background
                    textField.setOpaque(false);
                    textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7)); // Add spacing
                    textField.setForeground(Color.WHITE); // Set text color to white
                }
            });



            controlPanel.add(units[i]);
            row.add(controlPanel, controlConstraints);

            // Add the row to the parent panel
            rowsPanel.add(row, rowConstraints);
        }

        // Add rowsPanel to the main time limits panel
        panel.add(rowsPanel, gbc);

        // Add toggle switch for enabling/disabling restrictions
        JPanel toggleRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toggleRow.setOpaque(false);

        JLabel toggleLabel = new JLabel("Enable Time Restrictions");
        toggleLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        toggleLabel.setForeground(Color.WHITE);
        toggleRow.add(toggleLabel);

        JToggleButton toggleButton = new JToggleButton("OFF");
        toggleButton.setFont(new Font("Comic Sans MS", Font.BOLD, 18));

        // Add the ActionListener for enabling/disabling restrictions
        toggleButton.addActionListener(e -> {
            boolean isEnabled = toggleButton.isSelected();
            toggleButton.setText(isEnabled ? "ON" : "OFF");

            if (isEnabled) {
                // Reset gameplay_locked to false and timer when enabling restrictions
                GameSaveManager.setGameplayLocked(false);
                GameSaveManager.resetTimeLimitForToday();
            } else {
                // Turn off restrictions (unlock gameplay)
                GameSaveManager.setGameplayLocked(false);
            }

            // Notify Gameplay (if it's active) to reset the timer
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof Gameplay) {
                    ((Gameplay) comp).resetTimer();
                    break;
                }
            }
        });

        toggleRow.add(toggleButton);
        panel.add(toggleRow, gbc);

        // Add Back button
        JButton backButton = createCustomButton("Back", e -> layout.show(contentPanel, "Layout3"));
        panel.add(backButton, gbc);

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
