package com.cs2212.bunbun.system;
import javax.swing.*;

public class Settings extends JFrame {
    // Instance Variables
    private float masterVolume;
    private float musicVolume;
    private float sfxVolume;
    //private static AudioSettings instance;

    // Constructor
    public Settings() {
        // Initialize the JFrame properties inside the constructor
        JButton saveGame = new JButton();
        saveGame.setBounds(200, 100, 250, 50);

        this.setTitle("Settings"); // Set the title of the frame
        this.setResizable(false);  // Prevent resizing of the frame
        this.setSize(800, 500); // Set the size of the frame
        this.setVisible(true); // Make the frame visible
        this.add(saveGame);
    }

    public static void main(String[] args) {
        // Create and display the Settings frame
        new Settings();
    }
}


//
//    getInstance() {
//    }
//
//    saveSettings(filename) {
//    }
//
//    setMasterVolume(newVolume) {
//    }
//
//    setMusicVolume(newVolume) {
//    }
//
//    setSFXVolume(newVolume) {
//    }