package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Atanas Chervarov on 10/18/17.
 */

public class DCSoundManager {

    private static DCSoundManager instance;
    private ArrayList<MediaPlayer> players;

    public static synchronized DCSoundManager getInstance() {
        if (instance == null)
            instance = new DCSoundManager();
        return instance;
    }

    DCSoundManager() {
        players = new ArrayList<>();
    }

    public enum SOUND {
        BRUSH_EVENING_1("sounds/voice/male/evening/brush/1.wav", "sounds/voice/female/evening/brush/1.mp3"),
        BRUSH_EVENING_2("sounds/voice/male/evening/brush/2.wav", "sounds/voice/female/evening/brush/2.mp3"),
        BRUSH_EVENING_3("sounds/voice/male/evening/brush/3.wav", "sounds/voice/female/evening/brush/3.mp3"),
        BRUSH_EVENING_4("sounds/voice/male/evening/brush/4.wav", "sounds/voice/female/evening/brush/4.mp3"),
        BRUSH_EVENING_5("sounds/voice/male/evening/brush/5.wav", "sounds/voice/female/evening/brush/5.mp3"),
        BRUSH_EVENING_6("sounds/voice/male/evening/brush/6.wav", "sounds/voice/female/evening/brush/6.mp3");

        private String male;
        private String female;

        SOUND(String male, String female) {
            this.male = male;
            this.female = female;
        }

        public String getMale() { return male; }
        public String getFemale() { return female; }
    }

    public void playSound(Context context, SOUND sound) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(sound.getFemale());
            MediaPlayer player = new MediaPlayer();
            players.add(player);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    players.remove(mp);
                }
            });

            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
