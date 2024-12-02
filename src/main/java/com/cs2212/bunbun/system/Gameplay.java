package com.cs2212.bunbun.system;

import com.cs2212.bunbun.gameplay.GameSaveManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Gameplay extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AudioPlayer audioPlayer;
    private Image backgroundImage;
    private PixelArtProgressBar sleepBar, happinessBar, hungerBar, healthBar, pointsBar;
    private JButton takeToVetButton, exerciseButton, feedButton, playButton, giftButton, goToBedButton, backButton;
    private JButton settingsButton, inventoryButton, storeButton;
    private JPanel bunnyPanel;
    private JButton bunnyPlayButton, petSleepButton, giveMedsButton, bunnyWorkoutButton, giveBunnyGiftButton, giveBunnyFoodButton;
    private boolean isPlayCooldownActive, isSleepCooldownActive, isGiveMedsCooldownActive, isWorkoutCooldownActive, isGiftCooldownActive, isFeedCooldownActive;
    private volatile boolean isAnimationPaused = false;
    private Timer gameTimer;
    private int elapsedTime = 0;
    private JLabel petPlaceholder;
    private Timer healthDecayTimer; // Added to manage health decay
    private String slotKey;
    private boolean isDeadStateHandled = false;

    private enum BunnyState { DEAD, SLEEPING, ANGRY, HUNGRY, NORMAL }
    private BunnyState currentState = BunnyState.NORMAL;


    private boolean isFlipped = false; // Tracks if the bunny is flipped
    private Timer flipTimer;

    public Gameplay(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer, String petName, String petType, String slotKey) {
        this.cardLayout = cardLayout; // Store the CardLayout
        this.mainPanel = mainPanel;   // Store the Main Panel
        this.audioPlayer = audioPlayer;
        this.slotKey = slotKey;

        // Load background image
        backgroundImage = new ImageIcon(getClass().getResource("/images/main-gameplay-background.png")).getImage();
        setLayout(new BorderLayout());
        setOpaque(false);

        // Bunny panel setup
        bunnyPanel = new JPanel();
        bunnyPanel.setLayout(null);
        bunnyPanel.setOpaque(false);

        // Back button
        JButton backToMainMenu = createButton("<<", 20, 20, e -> cardLayout.show(mainPanel, "MainMenu"));
        backToMainMenu.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        backToMainMenu.setContentAreaFilled(false);
        backToMainMenu.setBorderPainted(false);
        backToMainMenu.setForeground(Color.WHITE);
        bunnyPanel.add(backToMainMenu);

        // Progress bars
        sleepBar = createProgressBar(20, 80, "Sleep", 0, 120, bunnyPanel);
        happinessBar = createProgressBar(150, 80, "Happiness", 0, 200, bunnyPanel);
        hungerBar = createProgressBar(280, 80, "Fullness", 0, 150, bunnyPanel);
        int savedHealth = GameSaveManager.getPetHealth(slotKey);
        healthBar = createProgressBar(410, 80, "Health", GameSaveManager.getPetHealth(slotKey), 20, bunnyPanel);
        pointsBar = createProgressBar(540, 80, "Points", 0, 50, bunnyPanel);

        // Action buttons
        addMainActionButtons();

        // inv and store
        inventoryButton = createButton("Inventory", 1200, 50, null);
        bunnyPanel.add(inventoryButton);
        storeButton = createButton("Store", 1200, 150, null);
        bunnyPanel.add(storeButton);

        // Pet image and name
        String imagePath = getImagePathForPet(petType);
        System.out.println("Gameplay Constructor - Pet Name: " + petName + ", Pet Type: " + petType);
        ImageIcon petIcon = new ImageIcon(getClass().getResource(imagePath));
        System.out.println("Gameplay Constructor - Image Path: " + imagePath);

        Image scaledImage = petIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Set up flipping animation timer
        flipTimer = new Timer(5000, e -> {
            isFlipped = !isFlipped;
            repaint();
        });
        flipTimer.start();

        petPlaceholder = new JLabel(petName, SwingConstants.CENTER);
        petPlaceholder.setIcon(scaledIcon);
        petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
        petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM);
        petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
        petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        petPlaceholder.setBounds(893, 300, 300, 400);
        bunnyPanel.add(petPlaceholder);

        // Animations
        startAnimation(sleepBar, 2, -1);
        startAnimation(happinessBar, 3, -2);
        startAnimation(hungerBar, 4, -3);
        startHealthDecay();
        startStateMonitor();

        // Timer
        gameTimer = new Timer(60000, e -> { // Fires every minute
            elapsedTime++;
            enforceTimeLimit(cardLayout, mainPanel);
        });
        startTimer();

        add(bunnyPanel, BorderLayout.CENTER);
    }


    private void handleDeadState() {
        if (isDeadStateHandled) return; // Skip if already handled

        isDeadStateHandled = true; // Mark the dead state as handled
        stopAllTimers(); // Stop all running timers

        // Update the LoadGame slots to reflect the pet's death
        SwingUtilities.invokeLater(() -> {
            // Find LoadGame instance and update its slots
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof LoadGame) {
                    ((LoadGame) comp).updateSlots();
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Your bunny has died. Returning to Main Menu.", "Pet Died", JOptionPane.ERROR_MESSAGE);
            cardLayout.show(mainPanel, "MainMenu");
            GameSaveManager.savePetHealth(slotKey, 0); // Ensure health is saved as 0
        });
    }



    private void enforceTimeLimit(CardLayout cardLayout, JPanel mainPanel) {
        if (!GameSaveManager.isTimeRestrictionEnabled()) {
            return; // Skip enforcement if restrictions are not enabled
        }

        Map<String, Integer> timeLimits = GameSaveManager.getTimeLimits();
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        String todayKey = today.toString().substring(0, 1).toUpperCase() + today.toString().substring(1).toLowerCase();
        int todayLimit = timeLimits.getOrDefault(todayKey, 0); // Get today's limit in minutes

        System.out.println("Time Limit for " + todayKey + ": " + todayLimit + " minutes");
        System.out.println("Elapsed Time: " + elapsedTime + " minutes");

        if (elapsedTime >= todayLimit) {
            JOptionPane.showMessageDialog(this, "Your time limit is reached.", "Time Limit", JOptionPane.WARNING_MESSAGE);
            gameTimer.stop();

            // Lock gameplay and save
            GameSaveManager.setGameplayLocked(true);

            // Go back to Main Menu
            cardLayout.show(mainPanel, "MainMenu");
        }
    }



    public void startTimer() {
        elapsedTime = 0; // Reset elapsed time

        if (gameTimer != null) {
            gameTimer.stop(); // Ensure no overlapping timers
        }

        // Check if restrictions are enabled before starting the timer
        if (GameSaveManager.isTimeRestrictionEnabled()) {
            gameTimer.start();
        }
    }

    public void resetTimer() {
        elapsedTime = 0;
        if (GameSaveManager.isTimeRestrictionEnabled()) {
            gameTimer.restart();
        } else {
            gameTimer.stop();
        }
    }

    private void startStateMonitor() {
        Timer stateMonitorTimer = new Timer(500, e -> updateBunnyState()); // Check state every 500ms
        stateMonitorTimer.start();
    }

    private void updateBunnyState() {
        if (healthBar.getValue() == 0) {
            setBunnyState(BunnyState.DEAD);
        } else if (isSleepCooldownActive) {
            setBunnyState(BunnyState.SLEEPING);
        } else if (happinessBar.getValue() == 0) {
            setBunnyState(BunnyState.ANGRY);
        } else if (hungerBar.getValue() == 0) {
            setBunnyState(BunnyState.HUNGRY);
        } else {
            setBunnyState(BunnyState.NORMAL);
        }
    }

    private void setBunnyState(BunnyState newState) {
        if (currentState == newState) return; // No change, skip updates
        currentState = newState;

        switch (currentState) {
//            case DEAD:
//
//                ImageIcon petIcon = new ImageIcon(getClass().getResource("/images/brown-bunny-dead.png"));
//                Image scaledImage = petIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
//                ImageIcon scaledIcon = new ImageIcon(scaledImage);
//                petPlaceholder.setIcon(scaledIcon);
//                petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
//                petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
//                petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM);
//                petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
//                petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
//                petPlaceholder.setBounds(893, 400, 300, 400);
//                JOptionPane.showMessageDialog(this, "Your bunny has died.", "Bunny State", JOptionPane.ERROR_MESSAGE);
//                disableAllButtons();
//                break;

            case SLEEPING:



                ImageIcon petIcon2 = new ImageIcon(getClass().getResource("/images/brown-bunny-sleep.png"));
                Image scaledImage2 = petIcon2.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon2 = new ImageIcon(scaledImage2);
                petPlaceholder.setIcon(scaledIcon2);
                petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
                petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
                petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM);
                petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
                petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
                petPlaceholder.setBounds(893, 400, 300, 400);

                break;
            case ANGRY:

                ImageIcon petIcon3= new ImageIcon(getClass().getResource("/images/brown-bunny-angry.png"));
                Image scaledImage3 = petIcon3.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon3 = new ImageIcon(scaledImage3);
                petPlaceholder.setIcon(scaledIcon3);
                petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
                petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
                petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM);
                petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
                petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
                petPlaceholder.setBounds(893, 400, 300, 400);
                break;

            case HUNGRY:


                ImageIcon petIcon4= new ImageIcon(getClass().getResource("/images/brown-bunny-hungry.png"));
                Image scaledImage4 = petIcon4.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon4 = new ImageIcon(scaledImage4);
                petPlaceholder.setIcon(scaledIcon4);
                petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
                petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
                petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM);
                petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
                petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
                petPlaceholder.setBounds(893, 400, 300, 400);
                break;

            case NORMAL:

                ImageIcon petIcon5 = new ImageIcon(getClass().getResource("/images/brown-bunny-normal.png"));
                Image scaledImage5  = petIcon5.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon5 = new ImageIcon(scaledImage5);
                petPlaceholder.setIcon(scaledIcon5);
                petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
                petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
                petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM);
                petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
                petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
                petPlaceholder.setBounds(893, 400, 300, 400);
                break;
        }
    }


    private void stopAllTimers() {
        if (gameTimer != null) gameTimer.stop();
        if (healthDecayTimer != null) healthDecayTimer.stop();
        if (flipTimer != null) flipTimer.stop();
    }



    private void disableAllButtons() {
        takeToVetButton.setEnabled(false);
        exerciseButton.setEnabled(false);
        feedButton.setEnabled(false);
        playButton.setEnabled(false);
        giftButton.setEnabled(false);
        goToBedButton.setEnabled(false);
    }


    private String getImagePathForPet(String petType) {
        switch (petType) {
            case "Black Bunny":
                return "/images/black-bunny-normal.png";
            case "Brown Bunny":
                return "/images/brown-bunny-normal.png";
            case "White Bunny":
            case "Snow": // Allow "Snow" to map to the white bunny image
                return "/images/white-bunny-normal.png";
            default:
                System.out.println("Unknown petType: " + petType);
                return "/images/placeholder.png"; // Use a valid placeholder path
        }
    }


    public void updatePetInfo(String petName, String petType) {
        System.out.println("Updating Gameplay with Pet Name: " + petName + ", Pet Type: " + petType);

        String petImagePath = getImagePathForPet(petType);
        ImageIcon petIcon = new ImageIcon(getClass().getResource(petImagePath));
        Image scaledImage = petIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        petPlaceholder.setIcon(new ImageIcon(scaledImage));
        petPlaceholder.setText(petName);
    }


    // Modify `modifyProgressBar` to save the health value
    private void modifyProgressBar(JProgressBar progressBar, int value) {
        int currentValue = progressBar.getValue();
        int maxValue = progressBar.getMaximum();
        int newValue = Math.max(0, Math.min(currentValue + value, maxValue));
        progressBar.setValue(newValue);

        // Save health value if the progressBar is healthBar
        if (progressBar == healthBar) {
            GameSaveManager.savePetHealth(slotKey, newValue); // Persist health

            if (newValue == 0) { // Check if health is 0
                handleDeadState(); // Trigger dead state
            }
        }

        updateBunnyState();
    }

    //     Show the original buttons after finishing the feeding task
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

    private void addMainActionButtons() {
        takeToVetButton = createButton("Take to Vet", 20, 200, e -> {
            hideOriginalButtons();
            showVetTaskButtons();
            changeBackgroundImage("/images/vet-background.jpg");
        });
        bunnyPanel.add(takeToVetButton);

        exerciseButton = createButton("Exercise", 250, 200, e -> {
            hideOriginalButtons();
            showExerciseTaskButtons();
            changeBackgroundImage("/images/exercise-background.jpg");
        });
        bunnyPanel.add(exerciseButton);

        feedButton = createButton("Feed", 250, 600, e -> {
            hideOriginalButtons();
            showFeedTaskButtons();
            changeBackgroundImage("/images/feed-background.jpg");
        });
        bunnyPanel.add(feedButton);

        playButton = createButton("Play", 20, 400, e -> {
            hideOriginalButtons();
            showPlayTaskButtons();
            changeBackgroundImage("/images/play-outside.jpg");
        });
        bunnyPanel.add(playButton);

        giftButton = createButton("Give Gift", 480, 600, e -> {
            hideOriginalButtons();
            showGiftTaskButtons();
            changeBackgroundImage("/images/gift-background.jpg");
        });
        bunnyPanel.add(giftButton);

        goToBedButton = createButton("Go To Bed", 710, 600, e -> {
            hideOriginalButtons();
            showSleepTaskButtons();
            changeBackgroundImage("/images/sleep-background.jpg");
        });
        bunnyPanel.add(goToBedButton);
    }

    // EXERCISE BUTTONS
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
                    modifyProgressBar(pointsBar, 20);  // Increase pointsBar by 7
                    modifyProgressBar(healthBar, 20); // Increase healthBar by 20
                    modifyProgressBar(sleepBar, -10);  // Decrease sleepBar by 5
                    modifyProgressBar(hungerBar, -10);
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

    // GIFT BUTTONS FUNCTIONS
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

    // FEED BUTTONS FUNCTIONS
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
                    modifyProgressBar(pointsBar, 25);  // Increase pointsBar by 5
                    modifyProgressBar(hungerBar, 20); // Increase fullnessBar by 10
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




    // PLAY BUTTONS FUNCTIONS
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
                    modifyProgressBar(happinessBar, 50); // Increase happinessBar by 10
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

    // VET BUTTON FUNCTIONS
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
                SwingUtilities.invokeLater(() -> modifyProgressBar(sleepBar,5)); // Increase points by +5 after sleep task
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







    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

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

    private PixelArtProgressBar createProgressBar(int x, int y, String label, int initialValue, int maxValue, JPanel panel) {
        PixelArtProgressBar bar = new PixelArtProgressBar(0, maxValue);
        bar.setBounds(x, y, 100, 20);
        bar.setValue(initialValue);
        panel.add(bar);

        JLabel barLabel = new JLabel(label, SwingConstants.CENTER);
        barLabel.setBounds(x, y + 25, 100, 20);
        barLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        panel.add(barLabel);

        return bar;
    }

    private void startAnimation(PixelArtProgressBar bar, int increaseRate, int decreaseRate) {
        AtomicBoolean increasing = new AtomicBoolean(true); // Use AtomicBoolean to allow modification in lambda

        new Thread(() -> {
            try {
                while (true) {
                    if (!isAnimationPaused) {
                        SwingUtilities.invokeLater(() -> {
                            if (increasing.get()) {
                                if (bar.getValue() < bar.getMaximum()) {
                                    bar.setValue(bar.getValue() + increaseRate);
                                } else {
                                    increasing.set(false); // Update AtomicBoolean
                                }
                            } else {
                                if (bar.getValue() > 0) {
                                    bar.setValue(bar.getValue() + decreaseRate);
                                } else {
                                    increasing.set(true); // Reset to increasing when reaching minimum
                                }
                            }
                        });
                    }
                    Thread.sleep(100); // Adjust animation speed as needed
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void startHealthDecay() {
        healthDecayTimer = new Timer(1000, e -> {
            if (!isAnimationPaused) {
                SwingUtilities.invokeLater(() -> modifyProgressBar(healthBar, -1)); // Decrease health
            }
        });
        healthDecayTimer.start();
    }


    private void changeBackgroundImage(String imagePath) {
        backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        g2d.dispose();
    }

    private class PixelArtProgressBar extends JProgressBar {
        private final int segmentSize = 20;

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
            int totalSegments = width / segmentSize;
            int filledSegments = (int) ((value / (float) getMaximum()) * totalSegments);

            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, width, height);

            for (int i = 0; i < totalSegments; i++) {
                int x = i * segmentSize;
                if (i < filledSegments) {
                    g2d.setColor(getColorForValue(value));
                } else {
                    g2d.setColor(Color.DARK_GRAY);
                }
                g2d.fillRect(x, 0, segmentSize - 2, height);
            }
        }

        private Color getColorForValue(int value) {
            int max = getMaximum();
            double percentage = (value / (double) max) * 100;
            if (percentage <= 30) return Color.RED;
            if (percentage <= 60) return Color.ORANGE;
            if (percentage <= 90) return Color.YELLOW;
            return Color.GREEN;
        }
    }
}