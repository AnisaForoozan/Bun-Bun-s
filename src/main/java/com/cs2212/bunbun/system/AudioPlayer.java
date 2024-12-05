package com.cs2212.bunbun.system;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AudioPlayer {
    private Clip musicClip; // For background music
    private final Set<Clip> activeSFXClips = new HashSet<>(); // For managing all active SFX

    private float masterVolume = 0.0f; // Master volume in dB
    private float musicVolume = -10.0f; // Music volume in dB
    private float sfxVolume = 0.0f; // SFX volume in dB

    /**
     * Plays background music.
     */
    public void playMusic(String resourcePath, boolean loop) {
        try {
            if (musicClip != null && musicClip.isRunning()) {
                musicClip.stop(); // Stop any existing music
            }

            musicClip = loadAudio(resourcePath);
            applyVolume(musicClip, musicVolume + masterVolume); // Apply combined volume
            if (loop) {
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                musicClip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays a sound effect (SFX).
     */
    public void playSFX(String resourcePath) {
        try {
            Clip sfxClip = loadAudio(resourcePath);
            activeSFXClips.add(sfxClip); // Track active SFX clips
            applyVolume(sfxClip, sfxVolume + masterVolume); // Apply combined volume
            sfxClip.setFramePosition(0); // Reset clip to the beginning
            sfxClip.start();

            // Remove clip from active set once it finishes playing
            sfxClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    activeSFXClips.remove(sfxClip);
                    sfxClip.close(); // Ensure proper cleanup
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an audio clip.
     */
    private Clip loadAudio(String resourcePath) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        URL audioUrl = getClass().getClassLoader().getResource(resourcePath);
        if (audioUrl == null) {
            throw new IOException("Audio file not found: " + resourcePath);
        }

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioUrl);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        return clip;
    }

    /**
     * Applies volume settings to a clip.
     */
    private void applyVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(Math.max(volume, volumeControl.getMinimum())); // Ensure volume doesn't exceed min
        }
    }

    /**
     * Updates the master volume and applies it to all active audio clips.
     */
    public void setMasterVolume(float volume) {
        masterVolume = volume;

        // Update volume for music
        if (musicClip != null) {
            applyVolume(musicClip, musicVolume + masterVolume);
        }

        // Update volume for all active SFX clips
        for (Clip clip : activeSFXClips) {
            applyVolume(clip, sfxVolume + masterVolume);
        }
    }

    /**
     * Updates the music volume and applies it to the music clip.
     */
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (musicClip != null) {
            applyVolume(musicClip, musicVolume + masterVolume);
        }
    }

    /**
     * Updates the SFX volume and applies it to all active SFX clips.
     */
    public void setSFXVolume(float volume) {
        sfxVolume = volume;

        // Update volume for active SFX clips
        for (Clip clip : activeSFXClips) {
            applyVolume(clip, sfxVolume + masterVolume);
        }
    }
}
