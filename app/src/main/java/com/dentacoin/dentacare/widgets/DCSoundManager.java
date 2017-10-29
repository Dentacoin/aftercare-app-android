package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.dentacoin.dentacare.utils.DCSharedPreferences;

import java.io.IOException;

/**
 * Created by Atanas Chervarov on 10/18/17.
 */

public class DCSoundManager {

    private static DCSoundManager instance;
    private MediaPlayer musicPlayer;
    private MediaPlayer voicePlayer;
    private boolean isFemale;
    private boolean soundEnabled;
    private boolean musicEnabled;

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

    public boolean isVoiceMale() {
        return !isFemale;
    }

    public boolean isVoiceFemale() {
        return isFemale;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

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

    public enum VOICE {
        BRUSH_EVENING_1("sounds/voice/male/evening/brush/1.mp3", "sounds/voice/female/evening/brush/1.mp3"),
        BRUSH_EVENING_2("sounds/voice/male/morning/brush/2.mp3", "sounds/voice/female/morning/brush/2.mp3"),
        BRUSH_EVENING_3("sounds/voice/male/morning/brush/3.mp3", "sounds/voice/female/morning/brush/3.mp3"),
        BRUSH_EVENING_4("sounds/voice/male/morning/brush/4.mp3", "sounds/voice/female/morning/brush/4.mp3"),
        BRUSH_EVENING_5("sounds/voice/male/morning/brush/5.mp3", "sounds/voice/female/morning/brush/5.mp3"),
        BRUSH_EVENING_6("sounds/voice/male/morning/brush/6.mp3", "sounds/voice/female/morning/brush/6.mp3"),
        FLOSS_EVENING_1("sounds/voice/male/evening/floss/1.mp3", "sounds/voice/female/evening/floss/1.mp3"),
        FLOSS_EVENING_2("sounds/voice/male/evening/floss/2.mp3", "sounds/voice/female/evening/floss/2.mp3"),
        FLOSS_EVENING_3("sounds/voice/male/evening/floss/3.mp3", "sounds/voice/female/evening/floss/3.mp3"),
        RINSE_EVENING_1("sounds/voice/male/evening/rinse/1.mp3", "sounds/voice/female/evening/rinse/1.mp3"),
        RINSE_EVENING_2("sounds/voice/male/morning/rinse/2.mp3", "sounds/voice/female/morning/rinse/2.mp3"),
        RINSE_EVENING_3("sounds/voice/male/morning/rinse/3.mp3", "sounds/voice/female/morning/rinse/3.mp3"),
        RINSE_EVENING_4("sounds/voice/male/morning/rinse/4.mp3", "sounds/voice/female/morning/rinse/4.mp3"),
        RINSE_EVENING_5("sounds/voice/male/evening/rinse/5.mp3", "sounds/voice/female/evening/rinse/5.mp3"),
        EVENING_GREETING("sounds/voice/male/evening/greeting.mp3", "sounds/voice/female/evening/greeting.mp3"),
        BRUSH_MORNING_1("sounds/voice/male/morning/brush/1.mp3", "sounds/voice/female/morning/brush/1.mp3"),
        BRUSH_MORNING_2("sounds/voice/male/morning/brush/2.mp3", "sounds/voice/female/morning/brush/2.mp3"),
        BRUSH_MORNING_3("sounds/voice/male/morning/brush/3.mp3", "sounds/voice/female/morning/brush/3.mp3"),
        BRUSH_MORNING_4("sounds/voice/male/morning/brush/4.mp3", "sounds/voice/female/morning/brush/4.mp3"),
        BRUSH_MORNING_5("sounds/voice/male/morning/brush/5.mp3", "sounds/voice/female/morning/brush/5.mp3"),
        BRUSH_MORNING_6("sounds/voice/male/morning/brush/6.mp3", "sounds/voice/female/morning/brush/6.mp3"),
        RINSE_MORNING_1("sounds/voice/male/morning/rinse/1.mp3", "sounds/voice/female/morning/rinse/1.mp3"),
        RINSE_MORNING_2("sounds/voice/male/morning/rinse/2.mp3", "sounds/voice/female/morning/rinse/2.mp3"),
        RINSE_MORNING_3("sounds/voice/male/morning/rinse/3.mp3", "sounds/voice/female/morning/rinse/3.mp3"),
        RINSE_MORNING_4("sounds/voice/male/morning/rinse/4.mp3", "sounds/voice/female/morning/rinse/4.mp3"),
        RINSE_MORNING_5("sounds/voice/male/morning/rinse/5.mp3", "sounds/voice/female/morning/rinse/5.mp3"),
        MORNING_GREETING("sounds/voice/male/morning/greeting.mp3", "sounds/voice/female/morning/greeting.mp3");

        private String male;
        private String female;

        VOICE(String male, String female) {
            this.male = male;
            this.female = female;
        }

        public String getMale() { return male; }
        public String getFemale() { return female; }
    }

    public enum MUSIC {
        //TODO add song titles
        SONG1("", "sounds/music/1.mp3"),
        SONG2("", "sounds/music/2.mp3"),
        SONG3("", "sounds/music/3.mp3"),
        SONG4("", "sounds/music/4.mp3"),
        SONG5("", "sounds/music/5.mp3"),
        SONG6("", "sounds/music/6.mp3"),
        SONG7("", "sounds/music/7.mp3"),
        SONG8("", "sounds/music/8.mp3"),
        SONG9("", "sounds/music/9.mp3");

        private String title;
        private String path;

        MUSIC(String title, String path) {
            this.title = title;
            this.path = path;
        }

        public String getTitle() { return title; }
        public String getPath() { return path; }
    }

    public void playVoice(Context context, VOICE sound) {
        if (!soundEnabled)
            return;

        try {
            if (voicePlayer != null) {
                voicePlayer.stop();
                voicePlayer.release();
            }
            voicePlayer = new MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd(isFemale ? sound.getFemale() : sound.getMale());
            voicePlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            voicePlayer.prepare();
            voicePlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playMusic(Context context, MUSIC music) {
        if (!musicEnabled)
            return;

        try {
            if (musicPlayer != null) {
                musicPlayer.stop();
                musicPlayer.release();
            }

            musicPlayer = new MediaPlayer();

            AssetFileDescriptor afd = context.getAssets().openFd(music.getPath());
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            musicPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelSounds() {
        if (voicePlayer != null) {
            voicePlayer.stop();
            voicePlayer.release();
        }
    }

    public void cancelMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
        }
    }
}
