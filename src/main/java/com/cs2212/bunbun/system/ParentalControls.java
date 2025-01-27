package com.cs2212.bunbun.system;

import com.cs2212.bunbun.gameplay.GameSaveManager;


import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * The ParentalControls class represents the parental controls panel within the application.
 * It provides functionality for setting time limits, viewing statistics, reviving pets, and more.
 * Extends JPanel to be used within a Swing application.
 * @author Janreve Salubre
 * @version 1.0
 * @since 1.0
 */
public class ParentalControls extends JPanel {
    /** The AudioPlayer used for playing sound effects. */
    private AudioPlayer audioPlayer;

    /** The background image for the panel. */
    private Image backgroundImage;

    /** The CardLayout for switching between different layouts in the content panel. */
    private CardLayout layout;

    /** The main content panel that holds different layouts. */
    private JPanel contentPanel;

    /** An array of JComboBoxes representing units (Hours or Minutes) for each day. */
    private JComboBox<String>[] units;

    /** An array of JSpinners for setting time values for each day. */
    private JSpinner[] spinners;

    /** The main JPanel container. */
    private JPanel mainPanel;

    /** JLabel displaying total playtime. */
    private JLabel totalPlayTimeLabel;

    /** JLabel displaying average playtime. */
    private JLabel avgPlayTimeLabel;

    /**
     * Constructs a new ParentalControls panel.
     *
     * @param cardLayout  the CardLayout used for switching between panels.
     * @param mainPanel   the main JPanel container.
     * @param audioPlayer the AudioPlayer for playing audio effects.
     */
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

        JButton backButton = createButton("<<", e -> cardLayout.show(mainPanel, "MainMenu"));
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
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
        contentPanel.add(createStatisticsPanel(), "Layout5");
        contentPanel.add(createRevivePetPanel(), "Layout6");

        // Start with Layout 1
        layout.show(contentPanel, "Layout1");

        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Overrides the paintComponent method to draw the background image.
     *
     * @param g the Graphics object used for drawing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Creates the panel for entering the parental control password.
     *
     * @return the JPanel for entering password.
     */
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

    /**
     * Creates the panel for setting up a new parental control password.
     *
     * @return the JPanel for password setup.
     */
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

    /**
     * Creates the protected content panel after successful password entry.
     *
     * @return the JPanel for protected content.
     */
    private JPanel createProtectedContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Add Time Limits button
        JButton timeLimitsButton = createCustomButton("Time Limits", e -> layout.show(contentPanel, "Layout4"));
        panel.add(timeLimitsButton, gbc);

        // Add Statistics button
        JButton statisticsButton = createCustomButton("Statistics", e -> layout.show(contentPanel, "Layout5"));
        panel.add(statisticsButton, gbc);

        // Add Revive Pet button
        JButton revivePetButton = createCustomButton("Revive Pet", e -> layout.show(contentPanel, "Layout6"));
        panel.add(revivePetButton, gbc);

        // Add Back button
        JButton backButton = createCustomButton("Back", e -> layout.show(contentPanel, "Layout1"));
        panel.add(backButton, gbc);

        return panel;
    }

    /**
     * Creates the panel for setting time limits on gameplay.
     *
     * @return the JPanel for time limits.
     */
    private JPanel createTimeLimitsPanel() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
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
        rowConstraints.fill = GridBagConstraints.HORIZONTAL;
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
                    g2d.setColor(new Color(135, 135, 135, 50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                    // Draw border
                    g2d.setColor(new Color(117, 101, 81));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

                    g2d.dispose();
                }
            };
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(500, 50));

            // Add constraints for the day label
            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.weightx = 0.2;
            labelConstraints.fill = GridBagConstraints.BOTH;
            labelConstraints.anchor = GridBagConstraints.WEST;
            labelConstraints.insets = new Insets(0, 30, 0, 10);

            // Add day label
            JLabel dayLabel = new JLabel(days[i]);
            dayLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            dayLabel.setForeground(Color.WHITE);
            row.add(dayLabel, labelConstraints);

            // Add constraints for the control panel
            GridBagConstraints controlConstraints = new GridBagConstraints();
            controlConstraints.gridx = 1;
            controlConstraints.weightx = 0.8;
            controlConstraints.fill = GridBagConstraints.BOTH;
            controlConstraints.anchor = GridBagConstraints.EAST;
            controlConstraints.insets = new Insets(0, 10, 0, 30);

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
                textField.setBackground(new Color(0, 0, 0, 0));
                textField.setOpaque(false);
                textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
                textField.setForeground(Color.WHITE);
            }
            controlPanel.add(spinners[i]);

            // Add combo box with transparency fixes
            units[i] = new JComboBox<>(new String[]{"Hrs", "Min"});
            units[i].setFont(new Font("Comic Sans MS", Font.PLAIN, 18));

            // Ensure the renderer has consistent styling
            units[i].setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                            cellHasFocus);
                    label.setBackground(new Color(0, 0, 0, 0));
                    label.setOpaque(false);
                    label.setForeground(Color.BLACK);
                    label.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
                    return label;
                }
            });

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
                    textField.setBackground(new Color(0, 0, 0, 0));
                    textField.setOpaque(false);
                    textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
                    textField.setForeground(Color.WHITE);
                }
            });

            controlPanel.add(units[i]);
            row.add(controlPanel, controlConstraints);

            // Add the row to the parent panel
            rowsPanel.add(row, rowConstraints);
        }

        // Add rowsPanel to the main time limits panel
        panel.add(rowsPanel, gbc);

        // Add Save Button
        JButton saveButton = createCustomButton("Apply", e -> {
            Map<String, Integer> timeLimits = GameSaveManager.getTimeLimits();

            for (int i = 0; i < days.length; i++) {
                int value = (int) spinners[i].getValue();
                String unit = (String) units[i].getSelectedItem();

                // Convert to minutes if the unit is hours
                int minutes = unit.equals("Hrs") ? value * 60 : value;

                // Update the time limit for the corresponding day
                timeLimits.put(days[i], minutes);
            }

            // Save updated time limits to GameSaveManager
            GameSaveManager.saveTimeLimits(timeLimits);

            JOptionPane.showMessageDialog(this, "Time limits updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(saveButton, gbc);

        // Add toggle switch for enabling/disabling restrictions
        JPanel toggleRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toggleRow.setOpaque(false);

        JLabel toggleLabel = new JLabel("Enable Time Restrictions");
        toggleLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        toggleLabel.setForeground(Color.WHITE);
        toggleRow.add(toggleLabel);

        // Initialize toggle button state based on saved value
        boolean isRestrictionEnabled = GameSaveManager.isTimeRestrictionEnabled();
        JToggleButton toggleButton = new JToggleButton(isRestrictionEnabled ? "ON" : "OFF", isRestrictionEnabled);
        toggleButton.setFont(new Font("Comic Sans MS", Font.BOLD, 18));

        // Add the ActionListener for enabling/disabling restrictions
        toggleButton.addActionListener(e -> {
            boolean isEnabled = toggleButton.isSelected();
            toggleButton.setText(isEnabled ? "ON" : "OFF");

            if (isEnabled) {
                // Enable time restrictions
                GameSaveManager.setTimeRestrictionEnabled(true);
                GameSaveManager.setGameplayLocked(false);
                GameSaveManager.resetTimeLimitForToday();
            } else {
                // Disable time restrictions
                GameSaveManager.setTimeRestrictionEnabled(false);
                GameSaveManager.setGameplayLocked(false);
                GameSaveManager.resetTimeLimitForToday();
            }

            // Notify Gameplay to reset its timer if active
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

    /**
     * Creates the panel displaying player statistics.
     *
     * @return the JPanel for statistics.
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("Player Statistics");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, gbc);

        // Total Play Time
        totalPlayTimeLabel = new JLabel("Total Play Time: 0 hrs 0 min");
        totalPlayTimeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        totalPlayTimeLabel.setForeground(Color.WHITE);
        totalPlayTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(totalPlayTimeLabel, gbc);

        // Reset Total Play Time Button
        JButton resetTotalButton = createCustomButton("Reset", e -> {
            GameSaveManager.resetTotalPlayTime();
            updateStatisticsLabels();
        });
        panel.add(resetTotalButton, gbc);

        // Average Play Time
        avgPlayTimeLabel = new JLabel("Average Play Time: 0 hrs 0 min");
        avgPlayTimeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        avgPlayTimeLabel.setForeground(Color.WHITE);
        avgPlayTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(avgPlayTimeLabel, gbc);

        // Reset Average Play Time Button
        JButton resetAverageButton = createCustomButton("Reset", e -> {
            GameSaveManager.resetAveragePlayTime();
            updateStatisticsLabels();
        });
        panel.add(resetAverageButton, gbc);

        // Back Button
        JButton backButton = createCustomButton("Back", e -> layout.show(contentPanel, "Layout3"));
        panel.add(backButton, gbc);

        // Initialize the statistics labels with current data
        updateStatisticsLabels();

        return panel;
    }

    /**
     * Updates the statistics labels dynamically.
     */
    private void updateStatisticsLabels() {
        int totalPlayTimeMinutes = GameSaveManager.getTotalPlayTimeInMinutes();
        int sessions = GameSaveManager.getSessionCount();

        int totalHours = totalPlayTimeMinutes / 60;
        int totalMinutes = totalPlayTimeMinutes % 60;
        totalPlayTimeLabel.setText(String.format("Total Play Time: %d hrs %d min", totalHours, totalMinutes));

        if (sessions > 0) {
            int avgPlayTimeMinutes = totalPlayTimeMinutes / sessions;
            int avgHours = avgPlayTimeMinutes / 60;
            int avgMinutes = avgPlayTimeMinutes % 60;
            avgPlayTimeLabel.setText(String.format("Average Play Time: %d hrs %d min", avgHours, avgMinutes));
        } else {
            avgPlayTimeLabel.setText("Average Play Time: 0 hrs 0 min");
        }
    }

    /**
     * Refreshes the statistics, can be called periodically.
     */
    public void refreshStatistics() {
        updateStatisticsLabels();
    }

    /**
     * Creates the panel for reviving pets.
     *
     * @return the JPanel for reviving pets.
     */
    private JPanel createRevivePetPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(20, 0, 20, 0);

        // Title
        JLabel titleLabel = new JLabel("Revive Pet");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);

        // Retrieve save slots
        Map<String, String> saveData = GameSaveManager.loadSaveData();

        // Create a panel to hold the slots
        JPanel slotsPanel = new JPanel(new GridBagLayout());
        slotsPanel.setOpaque(false);

        GridBagConstraints slotGbc = new GridBagConstraints();
        slotGbc.gridx = 0;
        slotGbc.gridy = 0;
        slotGbc.insets = new Insets(10, 0, 10, 0);

        for (int i = 0; i < 4; i++) {
            String slotKey = "Slot " + (i + 1);

            if (!saveData.containsKey(slotKey)) {
                continue;
            }

            int health = GameSaveManager.getPetHealth(slotKey);
            String state = (health == 0) ? "Dead" : "Normal State";
            String buttonText = slotKey + ": " + (health == 0 ? "Dead" : "Alive");

            // Create the button dynamically
            JButton slotButton = createCustomButton(buttonText, null);

            slotButton.addActionListener(e -> {
                if (health == 0) {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "This pet is dead. Do you want to revive it?",
                            "Revive Pet",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Revive the pet
                        GameSaveManager.savePetHealth(slotKey, GameSaveManager.getMaxHealth());
                        GameSaveManager.saveStat(slotKey + "_sleep", 120);
                        GameSaveManager.saveStat(slotKey + "_happiness", 200);
                        GameSaveManager.saveStat(slotKey + "_fullness", 150);

                        JOptionPane.showMessageDialog(this, "Pet successfully revived! All stats are maximized.", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh the Revive Pet Panel
                        refreshRevivePetPanel();

                        // Refresh the LoadGame UI
                        for (Component comp : mainPanel.getComponents()) {
                            if (comp instanceof LoadGame) {
                                ((LoadGame) comp).refreshLoadGameUI();
                                break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "This pet is already alive!", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            // Panel for the button with rounded styling
            JPanel slotButtonPanel = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();

                    // Draw semi-transparent background
                    g2d.setColor(new Color(135, 135, 135, 75));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                    // Draw border
                    g2d.setColor(new Color(117, 101, 81));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25);

                    g2d.dispose();
                }
            };
            slotButtonPanel.setPreferredSize(new Dimension(400, 50));
            slotButtonPanel.setOpaque(false);
            slotButtonPanel.setLayout(new BorderLayout());
            slotButtonPanel.add(slotButton, BorderLayout.CENTER);

            slotsPanel.add(slotButtonPanel, slotGbc);
            slotGbc.gridy++;
        }

        panel.add(slotsPanel, gbc);

        // Back button
        JButton backButton = createCustomButton("Back", e -> layout.show(contentPanel, "Layout3"));
        panel.add(backButton, gbc);

        return panel;
    }

    /**
     * Shows a popup dialog when reviving a pet.
     *
     * @param state the state of the pet (e.g., "Dead" or "Normal State").
     */
    private void showRevivePopup(String state) {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);

        JDialog dialog = new JDialog(parentFrame, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.getRootPane().setOpaque(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw the rounded rectangle background
                g2d.setColor(new Color(117, 101, 81));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);

                g2d.dispose();
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setPreferredSize(new Dimension(400, 200));

        JLabel messageLabel = new JLabel(state.equals("Normal State") ? "Pet is alive!" : "Revive this pet?");
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(50, 50, 300, 50);
        contentPanel.add(messageLabel);

        JButton confirmButton = createCustomButton("OK", e -> dialog.dispose());
        confirmButton.setBounds(150, 120, 100, 40);
        confirmButton.setFocusPainted(false);
        confirmButton.setContentAreaFilled(false);

        contentPanel.add(confirmButton);

        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Updates the revive pet slots after a pet is revived.
     */
    private void updateReviveSlots() {
        Map<String, String> saveData = GameSaveManager.loadSaveData();

        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel && "Layout6".equals(layout.toString())) {
                JPanel revivePanel = (JPanel) comp;
                revivePanel.removeAll();

                // Recreate updated revive pet panel
                JPanel updatedPanel = createRevivePetPanel();
                revivePanel.add(updatedPanel);

                revivePanel.revalidate();
                revivePanel.repaint();
                break;
            }
        }
        revalidate();
    }

    /**
     * Creates a custom styled button with specified text and action listener.
     *
     * @param text    the text to display on the button.
     * @param onClick the action to perform when the button is clicked.
     * @return the customized JButton.
     */
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

        if (onClick != null) {
            button.addActionListener(e -> {
                audioPlayer.playSFX("audio/sfx/click_sound.wav");
                onClick.actionPerformed(e);
            });
        }

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

    /**
     * Refreshes the revive pet panel after a pet has been revived.
     */
    private void refreshRevivePetPanel() {
        // Retrieve the latest data
        Map<String, String> saveData = GameSaveManager.loadSaveData();

        // Remove the existing Revive Pet panel (Layout6)
        contentPanel.remove(contentPanel.getComponent(5));

        // Create a new Revive Pet panel with the latest data
        JPanel newRevivePetPanel = createRevivePetPanel();
        contentPanel.add(newRevivePetPanel, "Layout6");

        // Refresh the content panel and ensure we stay on Layout6
        contentPanel.revalidate();
        contentPanel.repaint();
        layout.show(contentPanel, "Layout6");
    }

    /**
     * Refreshes the UI after a pet has been revived.
     */
    private void refreshAfterRevive() {
        refreshRevivePetPanel();
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof LoadGame) {
                ((LoadGame) comp).refreshLoadGameUI();
                break;
            }
        }
    }

    /**
     * Creates a styled button with a specified text and action listener.
     *
     * @param text    the text to display on the button.
     * @param onClick the action to perform when the button is clicked.
     * @return the customized JButton.
     */
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

    /**
     * Resets the panel to the initial layout (Layout1).
     */
    public void resetToLayout1() {
        layout.show(contentPanel, "Layout1");
    }
}