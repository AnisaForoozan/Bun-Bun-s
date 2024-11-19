package com.cs2212.bunbun.system;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings extends JFrame implements ActionListener {
    // Instance Variables
    private float masterVolume;
    private float musicVolume;
    private float sfxVolume;
    //private static AudioSettings instance;

    JButton saveGame;
    // Constructor
    public Settings() {
        // Initialize the JFrame properties inside the constructor
        saveGame = new JButton();
        saveGame.setBounds(295, 100, 200, 50);

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==saveGame) {

        }
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