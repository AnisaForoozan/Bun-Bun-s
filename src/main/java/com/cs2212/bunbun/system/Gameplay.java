    package com.cs2212.bunbun.system;

    import com.cs2212.bunbun.gameplay.GameSaveManager;

    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.ActionListener;
    import java.awt.event.KeyAdapter;
    import java.awt.event.KeyEvent;
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
        private JLabel scoreLabel; // Label to display the score
        private int score = 0;


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

            // Load stats from save file or use defaults
            int savedSleep = GameSaveManager.getStat(slotKey + "_sleep", 120);
            int savedHappiness = GameSaveManager.getStat(slotKey + "_happiness", 200);
            int savedFullness = GameSaveManager.getStat(slotKey + "_fullness", 150);
            int savedHealth = GameSaveManager.getStat(slotKey + "_health", GameSaveManager.getMaxHealth());
            int savedScore = GameSaveManager.getStat(slotKey + "_score", 0);

            // Initialize progress bars with saved or default values
            sleepBar = createProgressBar(20, 80, "Sleep", savedSleep, 120, bunnyPanel);
            happinessBar = createProgressBar(150, 80, "Happiness", savedHappiness, 200, bunnyPanel);
            hungerBar = createProgressBar(280, 80, "Fullness", savedFullness, 150, bunnyPanel);
            healthBar = createProgressBar(410, 80, "Health", savedHealth, GameSaveManager.getMaxHealth(), bunnyPanel);

            scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setBounds(900, 20, 200, 30); // Position at the top center
            bunnyPanel.add(scoreLabel);

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
    //        startAnimation(sleepBar, 2, -1);
    //        startAnimation(happinessBar, 3, -2);
    //        startAnimation(hungerBar, 4, -3);
            startHealthDecay();
            startStateMonitor();

            // Timer
            gameTimer = new Timer(60000, e -> { // Fires every minute
                elapsedTime++;
                enforceTimeLimit(cardLayout, mainPanel);
            });
            startTimer();

            add(bunnyPanel, BorderLayout.CENTER);

            startBarDecrement();

            // Add a KeyListener to detect key presses
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_S) { // Check if "S" key is pressed
                        saveGame();
                    }
                }
            });
            setFocusable(true); // Ensure the panel can capture key events
            requestFocusInWindow();
        }

        private void saveGame() {
            // Collect current stats
            int sleep = sleepBar.getValue();
            int happiness = happinessBar.getValue();
            int fullness = hungerBar.getValue();
            int health = healthBar.getValue();
            int currentScore = score;

            // Save stats using GameSaveManager
            GameSaveManager.saveStat(slotKey + "_sleep", sleep);
            GameSaveManager.saveStat(slotKey + "_happiness", happiness);
            GameSaveManager.saveStat(slotKey + "_fullness", fullness);
            GameSaveManager.saveStat(slotKey + "_health", health);
            GameSaveManager.saveStat(slotKey + "_score", currentScore);

            // Show confirmation message
            JOptionPane.showMessageDialog(this, "Game successfully saved!", "Save Game", JOptionPane.INFORMATION_MESSAGE);
        }



        private void handleDeadState() {
            if (isDeadStateHandled) return; // Skip if already handled

            isDeadStateHandled = true; // Mark the dead state as handled
            setBunnyState(BunnyState.DEAD); // Set state to DEAD
            stopAllTimers(); // Stop all running timers

            // Automatically save the pet's state upon death
            GameSaveManager.savePetHealth(slotKey, 0); // Save health as 0
            saveGame(); // Save all other stats

            // Update UI for death
            SwingUtilities.invokeLater(() -> {
                for (Component comp : mainPanel.getComponents()) {
                    if (comp instanceof LoadGame) {
                        ((LoadGame) comp).updateSlots(); // Update LoadGame UI
                        break;
                    }
                }

                JOptionPane.showMessageDialog(this, "Your bunny has died. Returning to Main Menu.", "Pet Died", JOptionPane.ERROR_MESSAGE);
                cardLayout.show(mainPanel, "MainMenu");
            });
        }




        // Method to update the score
        private void updateScore(int points) {
            score += points; // Increment the score
            scoreLabel.setText("Score: " + score); // Update the score label
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
            } else if (happinessBar.getValue() == 20) {
                setBunnyState(BunnyState.ANGRY);
            } else if (hungerBar.getValue() == 20) {
                setBunnyState(BunnyState.HUNGRY);
            } else {
                setBunnyState(BunnyState.NORMAL);
            }
        }

        private void setBunnyState(BunnyState newState) {
            if (currentState == newState) return; // No change, skip updates
            currentState = newState;

            // Determine the base image path for the current pet type
            String petImageBasePath;
            switch (GameSaveManager.getPetType(slotKey)) {
                case "Black Bunny":
                    petImageBasePath = "/images/black-bunny-";
                    break;
                case "Brown Bunny":
                    petImageBasePath = "/images/brown-bunny-";
                    break;
                case "White Bunny":
                    petImageBasePath = "/images/white-bunny-";
                    break;
                default:
                    petImageBasePath = "/images/placeholder-";
                    break;
            }

            // Determine the specific image for the current state
            String imagePath = ""; // Initialize the image path based on state
            switch (currentState) {
                case SLEEPING:
                    imagePath = petImageBasePath + "sleep.png";
                    break;
                case ANGRY:
                    imagePath = petImageBasePath + "angry.png";
                    break;
                case HUNGRY:
                    imagePath = petImageBasePath + "hungry.png";
                    break;
                case NORMAL:
                    imagePath = petImageBasePath + "normal.png";
                    break;
                case DEAD:
                    imagePath = petImageBasePath + "dead.png";
                    break;
            }

            // Load and display the selected image
            ImageIcon petIcon = new ImageIcon(getClass().getResource(imagePath));
            Image scaledImage = petIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            petPlaceholder.setIcon(new ImageIcon(scaledImage));
            petPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
            petPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
            petPlaceholder.setVerticalTextPosition(SwingConstants.BOTTOM);
            petPlaceholder.setHorizontalTextPosition(SwingConstants.CENTER);
            petPlaceholder.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
            petPlaceholder.setBounds(893, 400, 300, 400);

            // Handle specific actions for the DEAD state
            if (currentState == BunnyState.DEAD) {
                if (!isDeadStateHandled) { // Show popup only once
                    isDeadStateHandled = true;
                    JOptionPane.showMessageDialog(this, "Your bunny has died.", "Bunny State", JOptionPane.ERROR_MESSAGE);
                    disableAllButtons(); // Disable actions when the bunny is dead
                }
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

            progressBar.repaint(); // Ensure the bar visually updates
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

        private void showExerciseTaskButtons() {
            bunnyWorkoutButton = createButton("Workout", 250, 400, e -> {
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
            modifyProgressBar(sleepBar, -10); // Decrease Sleep
            modifyProgressBar(hungerBar, -5); // Decrease Fullness
            modifyProgressBar(healthBar, 10); // Increase Health
            updateScore(100);
            showMessage("Workout complete! Sleep and fullness decreased, health improved.");
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
            giveBunnyGiftButton = createButton("Give Gift", 250, 400, e -> {
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
            modifyProgressBar(happinessBar, 10); // Increase Happiness
            updateScore(100);
            showMessage("Your pet is happy with the gift! Happiness increased.");
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

        private void showFeedTaskButtons() {
            giveBunnyFoodButton = createButton("Give Food", 250, 400, e -> {
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

        private void performGiveBunnyFoodTask() {
            modifyProgressBar(hungerBar, 20); // Increase Fullness
            updateScore(100);
            showMessage("Your pet is well-fed! Fullness increased.");
        }


        private void hideFeedTaskButtons() {
            if (giveBunnyFoodButton != null) {
                giveBunnyFoodButton.setVisible(false);
            }
            if (backButton != null) {
                backButton.setVisible(false);
            }
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
            modifyProgressBar(happinessBar, 15); // Increase Happiness
            updateScore(100);
            showMessage("Your pet enjoyed playing! Happiness increased.");
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

        private void showVetTaskButtons() {
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
            modifyProgressBar(healthBar, healthBar.getMaximum() - healthBar.getValue()); // Set Health to full
            updateScore(100);
            showMessage("Your pet is now fully healthy!");
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
            petSleepButton = createButton("Go to Sleep", 250, 400, e -> {
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
                    // Set state to SLEEPING and deduct health
                    SwingUtilities.invokeLater(() -> {
                        setBunnyState(BunnyState.SLEEPING);
                        modifyProgressBar(healthBar, -5); // Deduct 5 health
                        disableAllButtons(); // Disable other actions during sleep
                    });

                    isAnimationPaused = true; // Pause animations

                    // Sleep animation for 10 seconds (1000ms * 10)
                    Thread.sleep(10000);

                    // Refill sleep bar and reset to NORMAL state
                    SwingUtilities.invokeLater(() -> {
                        modifyProgressBar(sleepBar, sleepBar.getMaximum() - sleepBar.getValue()); // Refill Sleep to max
                        setBunnyState(BunnyState.NORMAL);
                        showOriginalButtons(); // Enable buttons
                    });

                    isAnimationPaused = false; // Resume animations
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
            bar.setValue(initialValue); // Initialize with the provided initial value
            panel.add(bar);

            JLabel barLabel = new JLabel(label, SwingConstants.CENTER);
            barLabel.setBounds(x, y + 25, 100, 20);
            barLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
            barLabel.setForeground(Color.WHITE);
            panel.add(barLabel);

            return bar;
        }


        private void startAnimation(PixelArtProgressBar bar) {
            new Thread(() -> {
                try {
                    while (true) {
                        if (!isAnimationPaused) {
                            SwingUtilities.invokeLater(() -> {
                                // Example effect: Change bar color temporarily
                                bar.setBackground(Color.YELLOW);
                                Timer resetColor = new Timer(500, e -> bar.setBackground(Color.DARK_GRAY)); // Restore color
                                resetColor.setRepeats(false);
                                resetColor.start();
                            });
                        }
                        Thread.sleep(1000); // Animation interval
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }




        private void startHealthDecay() {
            healthDecayTimer = new Timer(1000, e -> {
                SwingUtilities.invokeLater(() -> {
                    if (hungerBar.getValue() == 0) { // Continuous health loss
                        modifyProgressBar(healthBar, -1);
                    }
                });
            });
            healthDecayTimer.start();
        }


        private void startBarDecrement() {
            Timer barTimer = new Timer(1000, e -> { // Update every second
                SwingUtilities.invokeLater(() -> {
                    if (!isAnimationPaused) {
                        // Decrease Fullness, Happiness, and Sleep steadily
                        modifyProgressBar(hungerBar, -1);
                        modifyProgressBar(happinessBar, -1);
                        modifyProgressBar(sleepBar, -1);

                        // Trigger sleep behavior if Sleep reaches 0
                        if (sleepBar.getValue() == 0 && currentState != BunnyState.SLEEPING) {
                            handleSleepZero();
                        }

                        // Trigger health decay if Fullness reaches 0
                        if (hungerBar.getValue() == 0) {
                            modifyProgressBar(healthBar, -1);
                        }
                    }
                });
            });
            barTimer.start();
        }


        private void handleSleepZero() {
            setBunnyState(BunnyState.SLEEPING); // Set to sleeping state
            modifyProgressBar(healthBar, -5); // Deduct 5 health

            // Lock actions and animate sleep
            disableAllButtons();
            isAnimationPaused = true;

            new Timer(10000, e -> { // Sleep for 10 seconds
                SwingUtilities.invokeLater(() -> {
                    isAnimationPaused = false;
                    modifyProgressBar(sleepBar, sleepBar.getMaximum()); // Refill Sleep
                    setBunnyState(BunnyState.NORMAL);
                    showOriginalButtons();
                });
            }).start();
            performSleepTask();
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
            private final int animationStep = 2; // Pixels to reduce per frame
            private Color barColor = Color.GREEN;

            public PixelArtProgressBar(int min, int max) {
                super(min, max);
                setBorderPainted(false);
                setBackground(Color.DARK_GRAY); // Background color
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                int value = getValue();
                int maxValue = getMaximum();

                // Calculate filled width
                int filledWidth = (int) ((value / (double) maxValue) * width);

                // Background
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, width, height);

                // Filled portion
                g2d.setColor(barColor);
                g2d.fillRect(0, 0, filledWidth, height);

                // Border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(0, 0, width - 1, height - 1);

                // Draw Gradient Effect (Optional)
                GradientPaint gradient = new GradientPaint(0, 0, barColor, width, 0, Color.WHITE);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, filledWidth, height);

                // Adjust color based on value
                adjustBarColor(value, maxValue);
            }

            private void adjustBarColor(int value, int maxValue) {
                double percentage = (value / (double) maxValue) * 100;

                if (value == 0) {
                    barColor = Color.RED; // Completely red when health is 0
                } else if (percentage <= 30) {
                    barColor = Color.RED;
                } else if (percentage <= 60) {
                    barColor = Color.ORANGE;
                } else if (percentage <= 90) {
                    barColor = Color.YELLOW;
                } else {
                    barColor = Color.GREEN;
                }
            }

        }

    }