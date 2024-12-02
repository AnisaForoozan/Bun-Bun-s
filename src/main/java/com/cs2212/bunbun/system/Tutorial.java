package com.cs2212.bunbun.system;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Tutorial extends JPanel {
    private AudioPlayer audioPlayer;
    private Image backgroundImage;
    private int currentPage = 0;
    private JPanel contentPanel;


    private String[] imagePaths = {
            "/images/Inventory.png", "/images/ItemShop.png", "/images/Progress.png", "/images/Go To Bed.png", "/images/Give Gift.png", "/images/Feed.png","/images/Play.png", "/images/Take To Vet.png", "/images/Exercise.png","/images/white-bunny-dead.png", "/images/white-bunny-sleep.png", "/images/white-bunny-angry.png", "/images/white-bunny-hungry.png", "/images/white-bunny-normal.png"
    };

    private String[] titles = {
            "Inventory", "Item Shop", "Progress", "Go To Bed", "Give Gift", "Feed", "Play", "Take to Vet", "Exercise", "Health = 0", "Sleep = 0", "Happiness = 0", "Hunger = 0", "Ready to Start Game"
    };

    private String[] descriptions = {
            "View the food and gift items you currently own.",
            " Purchase food and gift items using points.",
            "<html>Sleep: Restedness of your bunny.<br><br><html>" + "Happiness: Emotional state of your bunny.<br><br><html>" + "Hunger: How hungry your bunny is.<br><br><html>" + "Health: Overall health of your bunny.<br><br><html>" + "Points: Earned points to spend in the shop.<br><br><html>",
            "<html>Makes the bunny sleep.<br><br>" + "Effect: Sleep levels ↑, Hunger levels ↓, <b>+2 Points.<br><br></html>",
            "<html>Opens inventory to select a gift item for the bunny.<br><br>" + "Effect: Happiness levels ↑,<b>+6 Points.<br><html>",
            "<html>Opens inventory to select food for the bunny.<br><br>" + "Effect: Hunger levels ↑, <b>+3 Points.<br><html>",
            "<html>Bunny plays.<br><br>" + "Effect: Happiness levels ↑, <b>+5 Points.<br><html>",
            "<html>Bunny visits the vet.<br><br>" + "Effect: Health levels ↑, <b>+3 Points.<br><html>",
            "<html>Bunny goes for a walk.<br><br>" + "Effect: Health levels ↑, Sleep levels ↓, Hunger levels ↓, <b>+5 Points.<br><html>",
            "<html>The bunny has died.<br><br>" + "Effect: You must load a saved game or start a new game. No actions can be taken.<br><html>",
            "<html>The bunny is too tired.<br><br>" + "Effect: Health penalty applied. Only the Go to Bed button is available. <b>-2 Points.<br><html>",
            "<html>The bunny is angry.<br><br>" + "Effect: Only the Give Gift or Play buttons are available. <b>-2 Points.<br><html>",
            "<html>The bunny is hungry.<br><br>" + "Effect: Only Feed button is available. Happiness and health levels decline. <b>-3 Points.<br><html>",
            "<html>Hop Along and Have Fun!"

};

    public Tutorial(CardLayout cardLayout, JPanel mainPanel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;

        URL resource = getClass().getResource("/images/dimbackground.png"); // Replace with your image path
        if (resource != null) {
            backgroundImage = new ImageIcon(resource).getImage();
        } else {
            System.err.println("Background image not found!");
        }

        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false); // Ensure transparency to show the background

        // Back Button
        JButton backButton = createButton("⬅", e -> cardLayout.show(mainPanel, "MainMenu"));
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false); // Transparent background
        topLeftPanel.add(backButton);
        northPanel.add(topLeftPanel, BorderLayout.WEST);

        // Title Panel
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false); // Transparent background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Tutorial");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, gbc);

        northPanel.add(titlePanel, BorderLayout.CENTER);

        // Add the combined panel to the top of the main panel
        add(northPanel, BorderLayout.NORTH);

        // Content Panel (tutorial content area)

        JPanel tutorialPanel = new JPanel(new GridBagLayout());
        tutorialPanel.setOpaque(false); // Transparent background
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Make content panel transparent
        fillRectangle(contentPanel);
        tutorialPanel.add(contentPanel, new GridBagConstraints());
        add(tutorialPanel, BorderLayout.CENTER);

        // Bottom Navigation Panel (← and → buttons)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the buttons at the bottom
        bottomPanel.setOpaque(false); // Transparent background
        // Left Arrow Button (←)
        JButton leftButton = createButton("←", e -> navigateLeft());
        // Right Arrow Button (→)
        JButton rightButton = createButton("→", e -> navigateRight());
        bottomPanel.add(leftButton);
        bottomPanel.add(rightButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    private void navigateLeft() {
        if (currentPage > 0) {
            currentPage--;
            updateContent(); // Update the content when navigating left
            repaint();
        }
    }

    private void navigateRight() {
        if (currentPage < 13) { // Assuming there are 3 tutorial pages (0 to 2)
            currentPage++;
            updateContent(); // Update the content when navigating right
            repaint();
        }
    }

    private void updateContent() {
        // Update the content based on the currentPage index
        contentPanel.removeAll(); // Clear the old content
        fillRectangle(contentPanel); // Add new content based on the updated currentPage
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rectWidth = 900;
        int rectHeight = 600;
        int rectX = (getWidth() - rectWidth) / 2;
        int rectY = (getHeight() - rectHeight) / 2;

        // Set transparency (alpha)
        Color rectColor = new Color(0, 0, 0, 150); // Black with 150 alpha for transparency
        g2.setColor(rectColor);
        g2.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 50, 50);

        String title = titles[currentPage]; // Fetch the title for the current page
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        g2.setColor(Color.WHITE);

        // Calculate position for the title
        FontMetrics metrics = g2.getFontMetrics();
        int titleX = rectX + (rectWidth - metrics.stringWidth(title)) / 2; // Center horizontally
        int titleY = rectY + metrics.getHeight(); // Position slightly below the top edge of the rectangle
        g2.drawString(title, titleX, titleY);


        g2.dispose();

    }

    private void fillRectangle(JPanel tutorialPanel) {
        int rectWidth = 1000;
        int rectHeight = 600;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for precise positioning
        mainPanel.setOpaque(false); // Transparent background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between components
        gbc.gridx = 0; // Single column layout



        // Image Label
        gbc.gridy = 1; // Second row for the image
        gbc.anchor = GridBagConstraints.CENTER; // Center the image horizontally
        JLabel imageLabel = new JLabel();

        // Load the image resource
        URL resource = getClass().getResource(imagePaths[currentPage]);
        if (resource != null) {
            // Scale the image while preserving the aspect ratio
            ImageIcon icon = new ImageIcon(resource);
            Image originalImage = icon.getImage();
            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);

            // Calculate the scaling factor to fit the image within the rectangle
            double aspectRatio = (double) originalWidth / originalHeight;
            int scaledWidth, scaledHeight;

            if (originalWidth > originalHeight) {
                scaledWidth = rectWidth / 2; // Set a fixed width
                scaledHeight = (int) (scaledWidth / aspectRatio); // Calculate height based on aspect ratio
            } else {
                scaledHeight = rectHeight / 2; // Set a fixed height
                scaledWidth = (int) (scaledHeight * aspectRatio); // Calculate width based on aspect ratio
            }

            // Scale the image
            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            // If the image doesn't load, display an error message or placeholder
            System.err.println("Image not found: " + imagePaths[currentPage]);
            imageLabel.setText("Image not available");
            imageLabel.setForeground(Color.RED); // Make the text stand out
        }
        mainPanel.add(imageLabel, gbc);

        // Description Label
        gbc.gridy = 2; // Third row for the description
        gbc.anchor = GridBagConstraints.CENTER; // Center the description
        JLabel descriptionLabel = new JLabel("<html><div style='text-align: center; width: 400px;'>" + descriptions[currentPage] + "</div></html>");
        descriptionLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(descriptionLabel, gbc);

        // Add the main panel to the tutorial panel
        tutorialPanel.add(mainPanel, new GridBagConstraints());


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
}