package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.Music;
import com.dentacoin.dentacare.utils.Voice;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Atanas Chervarov on 10/18/17.
 */

public class DCSoundManager {

    private static final String TAG = DCSoundManager.class.getSimpleName();

    private static DCSoundManager instance;

    private MediaPlayer musicPlayer;
    private MediaPlayer voicePlayer;

    private boolean isFemale;
    private boolean soundEnabled;
    private boolean musicEnabled;

    private Music[] music = {
            Music.SONG1,
            Music.SONG2,
            Music.SONG3,
            Music.SONG4,
            Music.SONG5,
            Music.SONG6,
            Music.SONG7,
            Music.SONG8,
            Music.SONG9
    };

    private Music currentMusic;

    public static synchronized DCSoundManager getInstance() {
        if (instance == null)
            instance = new DCSoundManager();
        return instance;
    }

    DCSoundManager() {
        isFemale = DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.FEMALE_VOICE, false);
        soundEnabled = DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.SOUND_ENABLED, true);
        musicEnabled = DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.MUSIC_ENABLED, true);
    }

    public boolean isVoiceMale() { return !isFemale; }
    public boolean isVoiceFemale() { return isFemale; }
    public boolean isSoundEnabled() { return soundEnabled; }
    public boolean isMusicEnabled() { return musicEnabled; }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.SOUND_ENABLED, soundEnabled);
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.MUSIC_ENABLED, musicEnabled);
    }

    public void setIsFemale(boolean isFemale) {
        this.isFemale = isFemale;
        DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.FEMALE_VOICE, isFemale);
    }

    /**
     * Plays a VOICE sound if the sounds are enabled
     * @param context
     * @param voice
     */
    public void playVoice(Context context, Voice voice) {
        if (!soundEnabled)
            return;

        try {
            if (voicePlayer != null) {
                voicePlayer.release();
            }

            voicePlayer = new MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd(isFemale ? voice.getFemale() : voice.getMale());
            voicePlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (musicPlayer != null) {
                        try {
                            fadeIn();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            voicePlayer.prepare();
            voicePlayer.start();

            if (musicPlayer != null) {
                try {
                    fadeOut();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | IllegalStateException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private float volume = 1.0f;

    private void fadeOut() {
        if (musicPlayer != null) {
            final Timer timer = new Timer();
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (musicPlayer != null) {
                        volume -= 0.1f;
                        if (volume < 0.1f) {
                            timer.cancel();
                            timer.purge();
                        }
                        musicPlayer.setVolume(volume, volume);
                    }
                }
            };
            timer.schedule(task, 0, 100);
        }
    }

    private void fadeIn() {
        if (musicPlayer != null) {
            final Timer timer = new Timer();
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (musicPlayer != null) {
                        volume += 0.1f;
                        if (volume > 0.9f) {
                            timer.cancel();
                            timer.purge();
                        }
                        musicPlayer.setVolume(volume, volume);
                    }
                }
            };
            timer.schedule(task, 0, 100);
        }
    }

    /**
     * Plays MUSIC if music is enabled
     * @param context
     * @param music
     */
    public void playMusic(Context context, Music music) {
        if (!musicEnabled)
            return;

        try {

            if (musicPlayer != null) {
                musicPlayer.release();
            }

            musicPlayer = new MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd(music.getPath());
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            musicPlayer.prepare();
            musicPlayer.start();

        } catch (IOException | IllegalStateException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pause music if currently playing
     */
    public void pauseMusic() {
        if (musicPlayer != null) {
            try {
                if (musicPlayer.isPlaying()) {
                    musicPlayer.pause();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Resume music if currently playing
     */
    public void resumeMusic() {
        if (musicPlayer != null && soundEnabled) {
            try {
                if (!musicPlayer.isPlaying()) {
                    musicPlayer.start();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops any sound if currently playing
     */
    public void cancelSounds() {
        if (voicePlayer != null) {
            voicePlayer.release();
        }

        if (musicPlayer != null) {
            fadeIn();
        }
    }

    /**
     * Stops any music if currently playing
     */
    public void cancelMusic() {
        if (musicPlayer != null) {
            musicPlayer.release();
        }
    }

    /**
     * Cancels all music & sounds
     */
    public void cancelAll() {
        cancelMusic();
        cancelSounds();
    }
}
