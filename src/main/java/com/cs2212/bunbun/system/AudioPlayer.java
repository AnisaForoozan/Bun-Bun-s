package com.cs2212.bunbun.system;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    private Clip audioClip;
    private FloatControl volumeControl;

    /**
     * Plays an audio file.
     *
     * @param resourcePath The relative path to the audio file (e.g., "audio/background_music.wav").
     * @param loop         Whether the audio should loop continuously.
     */
    public void playAudio(String resourcePath, boolean loop) {
        try {
            // Load the audio file from resources
            URL audioUrl = getClass().getClassLoader().getResource(resourcePath);
            if (audioUrl == null) {
                throw new IOException("Audio file not found: " + resourcePath);
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioUrl);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);

            // Get the volume control
            if (audioClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            }

            // Start playing the audio
            if (loop) {
                audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                audioClip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the audio if it's currently playing.
     */
    public void stopAudio() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
        }
    }

    /**
     * Adjusts the audio volume.
     *
     * @param volume The desired volume level. Should be in the range supported by the audio system.
     */
    public void setVolume(float volume) {
        if (volumeControl != null) {
            // Clamp volume to the valid range
            volume = Math.max(volumeControl.getMinimum(), Math.min(volumeControl.getMaximum(), volume));
            volumeControl.setValue(volume);
        }
    }

    /**
     * Closes the audio resources when done.
     */
    public void close() {
        if (audioClip != null) {
            audioClip.close();
        }
    }
}
