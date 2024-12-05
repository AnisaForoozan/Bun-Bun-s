package com.cs2212.bunbun.system;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.GradientPaint;

import static org.junit.jupiter.api.Assertions.*;

class GameplayTest {

    private PixelArtProgressBar progressBar;

    @BeforeEach
    void setup() {
        // Initialize a PixelArtProgressBar with a range of 0 to 100
        progressBar = new PixelArtProgressBar(0, 100);
    }

    @Test
    void testInitialValue() {
        // Ensure the initial value is 0
        assertEquals(0, progressBar.getValue());
    }

    @Test
    void testSetValueWithinRange() {
        // Set value within the range
        progressBar.setValue(50);
        assertEquals(50, progressBar.getValue());
    }

    @Test
    void testSetValueExceedingMaximum() {
        // Set value exceeding the maximum
        progressBar.setValue(120);
        assertEquals(100, progressBar.getValue()); // Should clamp to max value
    }

    @Test
    void testSetValueBelowMinimum() {
        // Set value below the minimum
        progressBar.setValue(-10);
        assertEquals(0, progressBar.getValue()); // Should clamp to min value
    }

    @Test
    void testAdjustBarColor() {
        // Set a value and check if the bar color is adjusted accordingly
        progressBar.setValue(25);
        // Assuming the adjustBarColor method is accessible or indirectly testable
        // Here you could check if it correctly transitions to RED for low values
        // This would require exposing the color state or mocking the rendering process.
    }

    /**
     * PixelArtProgressBar Class Definition
     * Copy-pasted from the Gameplay class for isolated testing.
     */
    private static class PixelArtProgressBar extends JProgressBar {
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

        @Override
        public int getValue() {
            return super.getValue();
        }

        @Override
        public int getMaximum() {
            return super.getMaximum();
        }
    }
}
