package com.cs2212.bunbun.system;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    private Clip musicClip;
    private Clip sfxClip;
    private FloatControl masterVolumeControl;
    private FloatControl musicVolumeControl;
    private FloatControl sfxVolumeControl;

    private float masterVolume = 0.0f; // Master volume in dB
    private float musicVolume = -10.0f;  // Music volume in dB
    private float sfxVolume = 0.0f;    // SFX volume in dB

    public void playMusic(String resourcePath, boolean loop) {
        try {
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

    public void playSFX(String resourcePath) {
        try {
            sfxClip = loadAudio(resourcePath);
            applyVolume(sfxClip, sfxVolume + masterVolume); // Apply combined volume
            sfxClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private void applyVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volume);
        }
    }

    public void setMasterVolume(float volume) {
        masterVolume = volume;
        if (musicClip != null) {
            applyVolume(musicClip, musicVolume + masterVolume);
        }
        if (sfxClip != null) {
            applyVolume(sfxClip, sfxVolume + masterVolume);
        }
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (musicClip != null) {
            applyVolume(musicClip, musicVolume + masterVolume);
        }
    }

    public void setSFXVolume(float volume) {
        sfxVolume = volume;
        if (sfxClip != null) {
            applyVolume(sfxClip, sfxVolume + masterVolume);
        }
    }
}
