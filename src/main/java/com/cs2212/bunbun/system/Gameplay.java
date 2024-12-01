package com.cs2212.bunbun.system;

import com.cs2212.bunbun.system.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Gameplay extends JPanel {
    private AudioPlayer audioPlayer;
    private Image backgroundImage;
    private PixelArtProgressBar sleepBar, happinessBar, hungerBar, healthBar, pointsBar;
    private JButton takeToVetButton, exerciseButton, feedButton, playButton, giftButton, goToBedButton, backButton;  // Store references to the buttons
    private JButton settingsButton, inventoryButton, storeButton;
    private JPanel bunnyPanel;  // We'll use this to manage the visibility of components

    private JButton bunnyPlayButton; // "Bunny Play" button
    private boolean isPlayCooldownActive = false; // Cooldown flag for "Bunny Play" button

    private JButton petSleepButton; // "Pet Goes to Sleep" button
    private boolean isSleepCooldownActive = false; // Cooldown flag for sleep button

    private JButton giveMedsButton; // "Give Meds" button
    private boolean isGiveMedsCooldownActive = false; // Cooldown flag for "Give Meds" button

    private JButton bunnyWorkoutButton; // "Bunny Workout" button
    private boolean isWorkoutCooldownActive = false; // Cooldown flag for "Bunny Workout" button


    private JButton giveBunnyGiftButton; // "Give Bunny Gift" button
    private boolean isGiftCooldownActive = false; // Cooldown flag for "Give Bunny Gift" button
    private volatile boolean isAnimationPaused = false; // Flag to pause animations

    private JButton giveBunnyFoodButton; // "Give Bunny Food" button
    private boolean isFeedCooldownActive = false; // Cooldown flag for "Give Bunny Food" button


    public Gameplay(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer, String petType, String petName) {
        this.audioPlayer = audioPlayer;

        // Load background image (initial background)
        backgroundImage = new ImageIcon(getClass().getResource("/images/main-gameplay-background.png")).getImage();

        // Set up the Bunny panel layout
        setLayout(new BorderLayout());  // Use BorderLayout to control layout
        setOpaque(false);  // Make the panel transparent to allow the background image to show

        // Create a panel for Bunny content
        bunnyPanel = new JPanel();
        bunnyPanel.setLayout(null);  // Use null layout for absolute positioning
        bunnyPanel.setOpaque(false);  // Make this panel transparent to allow the background to show

        // Back button to go to MainMenu
        JButton backToMainMenu = createButton("<<", 20, 20, e -> cardLayout.show(mainPanel, "MainMenu"));
        backToMainMenu.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        backToMainMenu.setContentAreaFilled(false);
        backToMainMenu.setBorderPainted(false);
        backToMainMenu.setForeground(Color.WHITE);
        bunnyPanel.add(backToMainMenu);

        // Progress Bars
        sleepBar = createProgressBar(20, 80, "Sleep", 0, 120, bunnyPanel); // The progress bar for the sleep value
        happinessBar = createProgressBar(150, 80, "Happiness", 0, 200, bunnyPanel); // The progress bar for the happiness value
        hungerBar = createProgressBar(280, 80, "Fullness", 0, 150, bunnyPanel); // The progress bar for the fullness value
        healthBar = createProgressBar(410, 80, "Health", 100, 300, bunnyPanel); // The progress bar for the health value
        pointsBar = createProgressBar(540, 80, "Points", 0, 50, bunnyPanel); // The progress bar for the amount of game points



        // Original Action Buttons
        takeToVetButton = createButton("Take to Vet", 20, 200, e -> {
            hideOriginalButtons();  // Hide the main buttons
            showVetTaskButtons();   // Show "Back" and "Give Meds" buttons
            changeBackgroundImage("/images/vet-background.jpg"); // Change background to vet
        });
        bunnyPanel.add(takeToVetButton);


        exerciseButton = createButton("Exercise", 250, 200, e -> {
            hideOriginalButtons();  // Hide the main buttons
            showExerciseTaskButtons();   // Show "Back" and "Bunny Workout" buttons
            changeBackgroundImage("/images/exercise-background.jpg"); // Change background to exercise
        });
        bunnyPanel.add(exerciseButton);

        feedButton = createButton("Feed", 250, 600, e -> {
            hideOriginalButtons();  // Hide the main buttons
            showFeedTaskButtons();  // Show "Back" and "Give Bunny Food" buttons
            changeBackgroundImage("/images/feed-background.jpg"); // Change background to feed
        });
        bunnyPanel.add(feedButton);



        playButton = createButton("Play", 20, 400, e -> {
            hideOriginalButtons();  // Hide the original buttons
            showPlayTaskButtons();  // Show the buttons specific to the play task
            changeBackgroundImage("/images/play-outside.jpg");  // Change background for play task
        });
        bunnyPanel.add(playButton);

        giftButton = createButton("Give Gift", 480, 600, e -> {
            hideOriginalButtons();  // Hide the main buttons
            showGiftTaskButtons();  // Show "Back" and "Give Bunny Gift" buttons
            changeBackgroundImage("/images/gift-background.jpg"); // Change background to gift
        });
        bunnyPanel.add(giftButton);



        goToBedButton = createButton("Go To Bed", 710, 600, e -> {
            hideOriginalButtons();  // Hide original buttons
            showSleepTaskButtons(); // Show "Pet Goes to Sleep" and "Back" buttons
            changeBackgroundImage("/images/sleep-background.jpg"); // Change to sleep background
        });
        bunnyPanel.add(goToBedButton);


        // Sidebar Buttons
        settingsButton = createButton("Settings", 1700, 50, e -> showMessage("Settings clicked")) ;
        bunnyPanel.add(settingsButton);

        inventoryButton = createButton("Inventory", 1700, 150, e -> {
            cardLayout.show(mainPanel, "ItemsMainFrame"); // Switch to ItemsMainFrame
        });
        bunnyPanel.add(inventoryButton);


        storeButton = createButton("Store", 1700, 250, e -> {
            cardLayout.show(mainPanel, "ItemsMainFrame"); // Switch to ItemsMainFrame
        });
        bunnyPanel.add(storeButton);


        // Pet Placeholder
        ImageIcon bunnyImage = new ImageIcon("src/main/resources/images/black-bunny-normal.png");
        JLabel petPlaceholder = new JLabel("Bunny Name", SwingConstants.CENTER);
        Image scaledImage = bunnyImage.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        petPlaceholder.setIcon(scaledIcon);
        petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
        petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM);
        petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
        petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        petPlaceholder.setBounds(893, 449, 300, 400);
        petPlaceholder.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bunnyPanel.add(petPlaceholder);

        startAnimation(sleepBar, 2, -1); // Sleep bar: +2, -1
        startAnimation(happinessBar, 3, -2); // Happiness bar: +3, -2
        startAnimation(hungerBar, 4, -3); // Hunger bar: +4, -3

        // Add Bunny panel to the main panel
        add(bunnyPanel, BorderLayout.CENTER);
    }

    public void updatePetInfo(String petName, String petType) {
        System.out.println("Updating Gameplay with Pet: " + petName + " (" + petType + ")");

        // Example: Update the pet display based on the provided info
        String petImagePath = switch (petType) {
            case "Black Bunny" -> "/images/black-bunny-normal.png";
            case "Brown Bunny" -> "/images/brown-bunny-normal.png";
            case "White Bunny" -> "/images/white-bunny-normal.png";
            default -> "/images/normal.png";
        };

        // Update the pet's display image or any other pet-specific elements
        // Load and set the new pet image or properties here.
        // If you have a JLabel for the pet image, update it like this:
        // petImageLabel.setIcon(new ImageIcon(getClass().getResource(petImagePath)));
    }




    // Show the original buttons after finishing the feeding task
    private void showOriginalButtons() {
        takeToVetButton.setVisible(true);
        exerciseButton.setVisible(true);
        feedButton.setVisible(true);
        playButton.setVisible(true);
        giftButton.setVisible(true);
        goToBedButton.setVisible(true);

    }

    // Hide the original buttons
    private void hideOriginalButtons() {
        takeToVetButton.setVisible(false);
        exerciseButton.setVisible(false);
        feedButton.setVisible(false);
        playButton.setVisible(false);
        giftButton.setVisible(false);
        goToBedButton.setVisible(false);
    }

    private void showPlayTaskButtons() {
        bunnyPlayButton = createButton("Bunny Play", 250, 400, e -> {
            if (!isPlayCooldownActive) {
                performBunnyPlayTask(); // Perform the play task
            } else {
                showMessage("Please wait before playing again!"); // Notify the user
            }
        });
        bunnyPlayButton.setEnabled(!isPlayCooldownActive); // Enable or disable based on cooldown
        bunnyPanel.add(bunnyPlayButton);
        bunnyPlayButton.setVisible(true);

        backButton = createButton("Back", 480, 400, e -> {
            showOriginalButtons();  // Restore original buttons
            hidePlayTaskButtons();  // Hide play-specific buttons
            changeBackgroundImage("/images/main-gameplay-background.png"); // Restore original background
        });
        bunnyPanel.add(backButton);
        backButton.setVisible(true);
    }

    private void hidePlayTaskButtons() {
        if (bunnyPlayButton != null) {
            bunnyPlayButton.setVisible(false);
        }
        if (backButton != null) {
            backButton.setVisible(false);
        }
    }

    private void performBunnyPlayTask() {
        new Thread(() -> {
            try {
                isAnimationPaused = true; // Pause animations

                Thread.sleep(10000); // Simulate 10 seconds for the task

                SwingUtilities.invokeLater(() -> {
                    modifyProgressBar(pointsBar, 20);  // Increase pointsBar by 20
                    modifyProgressBar(happinessBar, 10); // Increase happinessBar by 10
                });

                isAnimationPaused = false; // Resume animations

                startPlayCooldown(); // Start cooldown after task
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startPlayCooldown() {
        isPlayCooldownActive = true; // Activate cooldown
        SwingUtilities.invokeLater(() -> bunnyPlayButton.setEnabled(false)); // Disable button

        Timer cooldownTimer = new Timer(20000, e -> { // 20-second cooldown
            isPlayCooldownActive = false; // Deactivate cooldown
            SwingUtilities.invokeLater(() -> bunnyPlayButton.setEnabled(true)); // Re-enable button
        });
        cooldownTimer.setRepeats(false);
        cooldownTimer.start();
    }



    private void showSleepTaskButtons() {
        petSleepButton = createButton("Pet Goes to Sleep", 250, 400, e -> {
            if (!isSleepCooldownActive) {
                performSleepTask(); // Start the sleep task
            } else {
                showMessage("The pet needs time to rest before sleeping again!"); // Notify the user
            }
        });
        petSleepButton.setEnabled(!isSleepCooldownActive); // Enable or disable based on cooldown
        bunnyPanel.add(petSleepButton);
        petSleepButton.setVisible(true);

        backButton = createButton("Back", 480, 400, e -> {
            showOriginalButtons();  // Restore original buttons
            hideSleepTaskButtons(); // Hide sleep-specific buttons
            changeBackgroundImage("/images/main-gameplay-background.png"); // Restore original background
        });
        bunnyPanel.add(backButton);
        backButton.setVisible(true);
    }


    private void hideSleepTaskButtons() {
        if (petSleepButton != null) {
            petSleepButton.setVisible(false);
        }
        if (backButton != null) {
            backButton.setVisible(false);
        }
    }

    private void performSleepTask() {
        new Thread(() -> {
            try {
                isAnimationPaused = true; // Pause animations

                int currentValue = sleepBar.getValue();
                int maxValue = sleepBar.getMaximum();
                int increment = (maxValue - currentValue) / 10; // Increment over 10 steps
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(1000); // Wait 1 second for each increment
                    final int newValue = currentValue + (increment * i);
                    SwingUtilities.invokeLater(() -> sleepBar.setValue(Math.min(newValue, maxValue))); // Update sleep bar
                }
                SwingUtilities.invokeLater(() -> increasePointsBar(5)); // Increase points by +5 after sleep task
                isAnimationPaused = false; // Resume animations

                startSleepCooldown(); // Start cooldown after task is complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startSleepCooldown() {
        isSleepCooldownActive = true; // Activate cooldown
        SwingUtilities.invokeLater(() -> petSleepButton.setEnabled(false)); // Disable button

        Timer cooldownTimer = new Timer(20000, e -> { // 20-second cooldown
            isSleepCooldownActive = false; // Deactivate cooldown
            SwingUtilities.invokeLater(() -> petSleepButton.setEnabled(true)); // Re-enable button
        });
        cooldownTimer.setRepeats(false); // Run once
        cooldownTimer.start();
    }

    private void increasePointsBar(int value) {
        int currentPoints = pointsBar.getValue(); // Get current points
        int maxPoints = pointsBar.getMaximum(); // Ensure it doesn't exceed max value
        int newPoints = Math.min(currentPoints + value, maxPoints); // Add value, ensuring it stays within bounds
        pointsBar.setValue(newPoints); // Set the new points value
    }


    private void showVetTaskButtons() {
        // Create "Give Meds" button
        giveMedsButton = createButton("Give Meds", 250, 400, e -> {
            if (!isGiveMedsCooldownActive) {
                performGiveMedsTask(); // Perform the Give Meds task
            } else {
                showMessage("Please wait before giving meds again!"); // Notify the user
            }
        });
        giveMedsButton.setEnabled(!isGiveMedsCooldownActive); // Enable or disable based on cooldown
        bunnyPanel.add(giveMedsButton);
        giveMedsButton.setVisible(true);

        // Create "Back" button
        backButton = createButton("Back", 480, 400, e -> {
            showOriginalButtons();  // Restore original buttons
            hideVetTaskButtons();  // Hide vet-specific buttons
            changeBackgroundImage("/images/main-gameplay-background.png"); // Restore original background
        });
        bunnyPanel.add(backButton);
        backButton.setVisible(true);
    }

    private void hideVetTaskButtons() {
        if (giveMedsButton != null) {
            giveMedsButton.setVisible(false);
        }
        if (backButton != null) {
            backButton.setVisible(false);
        }
    }

    private void performGiveMedsTask() {
        new Thread(() -> {
            try {
                isAnimationPaused = true; // Pause animations

                // Simulate 10 seconds for the task
                Thread.sleep(10000);

                // Increase Points Bar and Health Bar
                SwingUtilities.invokeLater(() -> {
                    modifyProgressBar(pointsBar, 10); // Add 10 points to pointsBar
                    modifyProgressBar(healthBar, 50); // Add 50 to healthBar
                });

                isAnimationPaused = false; // Resume animations

                // Start cooldown after task is complete
                startGiveMedsCooldown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startGiveMedsCooldown() {
        isGiveMedsCooldownActive = true; // Activate cooldown
        SwingUtilities.invokeLater(() -> giveMedsButton.setEnabled(false)); // Disable button

        Timer cooldownTimer = new Timer(20000, e -> { // 20-second cooldown
            isGiveMedsCooldownActive = false; // Deactivate cooldown
            SwingUtilities.invokeLater(() -> giveMedsButton.setEnabled(true)); // Re-enable button
        });
        cooldownTimer.setRepeats(false);
        cooldownTimer.start();
    }




    private void showExerciseTaskButtons() {
        bunnyWorkoutButton = createButton("Bunny Workout", 250, 400, e -> {
            if (!isWorkoutCooldownActive) {
                performBunnyWorkoutTask(); // Perform the workout task
            } else {
                showMessage("Please wait before working out again!"); // Notify the user
            }
        });
        bunnyWorkoutButton.setEnabled(!isWorkoutCooldownActive); // Enable or disable based on cooldown
        bunnyPanel.add(bunnyWorkoutButton);
        bunnyWorkoutButton.setVisible(true);

        backButton = createButton("Back", 480, 400, e -> {
            showOriginalButtons();  // Restore original buttons
            hideExerciseTaskButtons();  // Hide exercise-specific buttons
            changeBackgroundImage("/images/main-gameplay-background.png"); // Restore original background
        });
        bunnyPanel.add(backButton);
        backButton.setVisible(true);
    }

    private void hideExerciseTaskButtons() {
        if (bunnyWorkoutButton != null) {
            bunnyWorkoutButton.setVisible(false);
        }
        if (backButton != null) {
            backButton.setVisible(false);
        }
    }

    private void performBunnyWorkoutTask() {
        new Thread(() -> {
            try {
                isAnimationPaused = true; // Pause animations

                Thread.sleep(10000); // Simulate 10 seconds for the task

                SwingUtilities.invokeLater(() -> {
                    modifyProgressBar(pointsBar, 7);  // Increase pointsBar by 7
                    modifyProgressBar(healthBar, 20); // Increase healthBar by 20
                    modifyProgressBar(sleepBar, -5);  // Decrease sleepBar by 5
                    modifyProgressBar(hungerBar, -6);
                });

                isAnimationPaused = false; // Pause animations

                startWorkoutCooldown(); // Start cooldown after task
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startWorkoutCooldown() {
        isWorkoutCooldownActive = true; // Activate cooldown
        SwingUtilities.invokeLater(() -> bunnyWorkoutButton.setEnabled(false)); // Disable button

        Timer cooldownTimer = new Timer(20000, e -> { // 20-second cooldown
            isWorkoutCooldownActive = false; // Deactivate cooldown
            SwingUtilities.invokeLater(() -> bunnyWorkoutButton.setEnabled(true)); // Re-enable button
        });
        cooldownTimer.setRepeats(false);
        cooldownTimer.start();
    }



    private void showGiftTaskButtons() {
        giveBunnyGiftButton = createButton("Give Bunny Gift", 250, 400, e -> {
            if (!isGiftCooldownActive) {
                performGiveBunnyGiftTask(); // Perform the gift task
            } else {
                showMessage("Please wait before giving another gift!"); // Notify the user
            }
        });
        giveBunnyGiftButton.setEnabled(!isGiftCooldownActive); // Enable or disable based on cooldown
        bunnyPanel.add(giveBunnyGiftButton);
        giveBunnyGiftButton.setVisible(true);

        backButton = createButton("Back", 480, 400, e -> {
            showOriginalButtons();  // Restore original buttons
            hideGiftTaskButtons();  // Hide gift-specific buttons
            changeBackgroundImage("/images/main-gameplay-background.png"); // Restore original background
        });
        bunnyPanel.add(backButton);
        backButton.setVisible(true);
    }

    private void hideGiftTaskButtons() {
        if (giveBunnyGiftButton != null) {
            giveBunnyGiftButton.setVisible(false);
        }
        if (backButton != null) {
            backButton.setVisible(false);
        }
    }

    private void performGiveBunnyGiftTask() {
        new Thread(() -> {
            try {
                isAnimationPaused = true; // Pause animations

                // Simulate 10 seconds for the task
                Thread.sleep(10000);

                // Apply changes
                SwingUtilities.invokeLater(() -> {
                    modifyProgressBar(pointsBar, 5);  // Increase pointsBar by 5
                    modifyProgressBar(happinessBar, 10); // Increase happinessBar by 10
                });

                isAnimationPaused = false; // Resume animations
                startGiftCooldown(); // Start cooldown after task
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startGiftCooldown() {
        isGiftCooldownActive = true; // Activate cooldown
        SwingUtilities.invokeLater(() -> giveBunnyGiftButton.setEnabled(false)); // Disable button

        Timer cooldownTimer = new Timer(20000, e -> { // 20-second cooldown
            isGiftCooldownActive = false; // Deactivate cooldown
            SwingUtilities.invokeLater(() -> giveBunnyGiftButton.setEnabled(true)); // Re-enable button
        });
        cooldownTimer.setRepeats(false);
        cooldownTimer.start();
    }

    // Feed button action


    private void showFeedTaskButtons() {
        giveBunnyFoodButton = createButton("Give Bunny Food", 250, 400, e -> {
            if (!isFeedCooldownActive) {
                performGiveBunnyFoodTask(); // Perform the feed task
            } else {
                showMessage("Please wait before feeding again!"); // Notify the user
            }
        });
        giveBunnyFoodButton.setEnabled(!isFeedCooldownActive); // Enable or disable based on cooldown
        bunnyPanel.add(giveBunnyFoodButton);
        giveBunnyFoodButton.setVisible(true);

        backButton = createButton("Back", 480, 400, e -> {
            showOriginalButtons();  // Restore original buttons
            hideFeedTaskButtons();  // Hide feed-specific buttons
            changeBackgroundImage("/images/main-gameplay-background.png"); // Restore original background
        });
        bunnyPanel.add(backButton);
        backButton.setVisible(true);
    }

    private void hideFeedTaskButtons() {
        if (giveBunnyFoodButton != null) {
            giveBunnyFoodButton.setVisible(false);
        }
        if (backButton != null) {
            backButton.setVisible(false);
        }
    }

    private void performGiveBunnyFoodTask() {
        new Thread(() -> {
            try {
                isAnimationPaused = true; // Pause animations

                Thread.sleep(10000); // Simulate 10 seconds for the task

                SwingUtilities.invokeLater(() -> {
                    modifyProgressBar(pointsBar, 5);  // Increase pointsBar by 5
                    modifyProgressBar(hungerBar, 10); // Increase fullnessBar by 10
                });

                isAnimationPaused = false; // Resume animations
                startFeedCooldown(); // Start cooldown after task
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startFeedCooldown() {
        isFeedCooldownActive = true; // Activate cooldown
        SwingUtilities.invokeLater(() -> giveBunnyFoodButton.setEnabled(false)); // Disable button

        Timer cooldownTimer = new Timer(20000, e -> { // 20-second cooldown
            isFeedCooldownActive = false; // Deactivate cooldown
            SwingUtilities.invokeLater(() -> giveBunnyFoodButton.setEnabled(true)); // Re-enable button
        });
        cooldownTimer.setRepeats(false);
        cooldownTimer.start();
    }








    // Override the paintComponent method to draw the background image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the parent method to ensure proper painting behavior

        // Create a Graphics2D object for advanced drawing
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing for smoother drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background image, scaled to fit the panel
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Clean up by disposing of the Graphics2D object
        g2d.dispose();
    }

    // Change background color
    private void changeBackground(Color color) {
        setBackground(color);
        repaint();
    }

    // Change background image
    private void changeBackgroundImage(String imagePath) {
        backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        repaint();
    }

    private PixelArtProgressBar createProgressBar(int x, int y, String label, int initialValue, int maxValue, JPanel panel) {
        PixelArtProgressBar bar = new PixelArtProgressBar(0, maxValue);
        bar.setBounds(x, y, 100, 20);
        bar.setValue(initialValue); // Start with the provided initial value
        panel.add(bar);

        JLabel barLabel = new JLabel(label, SwingConstants.CENTER);
        barLabel.setBounds(x, y + 25, 100, 20);
        barLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        panel.add(barLabel);

        return bar;
    }

//    private JButton createButton(String text, int x, int y, ActionListener action) {
//        JButton button = new JButton(text);
//        button.setBounds(x, y, 180, 45);
//        button.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
//        button.setForeground(Color.WHITE); // Default text color
//        button.setFocusPainted(false);
//        button.setContentAreaFilled(false); // Disable default background
//        button.setBorderPainted(false); // Disable default border
//        button.setOpaque(false); // Ensure transparency
//        button.addActionListener(action);
//        return button;
//    }

    private static JButton createButton(String text, int x, int y, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Button background color
                if (getModel().isPressed()) {
                    g2.setColor(new Color(200, 200, 150));
                } else {
                    g2.setColor(new Color(135, 135, 135, 80));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Draw the text manually
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() + textHeight) / 2 - 4; // Adjust to center vertically
                g2.drawString(getText(), textX, textY);

                // Avoid default painting
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int borderThickness = 3; // Adjust this value for thicker borders
                g2.setStroke(new BasicStroke(borderThickness));

                g2.setColor(new Color(117, 101, 81));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
            }
        };

        button.setBounds(x, y, 120, 45); // Adjusted button size
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        button.setForeground(Color.white); // Text color
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Disable default background
        button.setBorderPainted(false); // Disable default border
        button.setOpaque(false); // Ensure transparency
        button.addActionListener(action);

        return button;
    }



    private void modifyProgressBar(JProgressBar progressBar, int value) {
        int currentValue = progressBar.getValue(); // Get the current value
        int maxValue = progressBar.getMaximum(); // Get the maximum value
        int newValue = Math.max(0, Math.min(currentValue + value, maxValue)); // Ensure new value is within bounds
        progressBar.setValue(newValue); // Update the progress bar
    }



    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // Start automatic animation for a specific bar
    private void startAnimation(PixelArtProgressBar bar, int increaseRate, int decreaseRate) {
        new Thread(() -> {
            boolean increasing = true;
            try {
                while (true) {
                    if (!isAnimationPaused) { // Only proceed if animation is not paused
                        if (increasing) {
                            if (bar.getValue() < bar.getMaximum()) {
                                bar.setValue(bar.getValue() + increaseRate);
                            } else {
                                increasing = false; // Start decreasing after hitting max value
                            }
                        } else {
                            if (bar.getValue() > 0) {
                                bar.setValue(bar.getValue() + decreaseRate);
                            }
                        }
                    }
                    Thread.sleep(100); // Speed of animation
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
            int max = getMaximum();
            double percentage = (value / (double) max) * 100;
            if (percentage <= 30) {
                return Color.RED;
            } else if (percentage <= 60) {
                return Color.ORANGE;
            } else if (percentage <= 90) {
                return Color.YELLOW;
            } else {
                return Color.GREEN;
            }
        }
    }
}
