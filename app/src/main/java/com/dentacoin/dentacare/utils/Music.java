package com.dentacoin.dentacare.utils;

import java.util.Random;

/**
 * Created by Atanas Chervarov on 10/29/17.
 */

public enum Music {
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

    Music(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public String getTitle() { return title; }
    public String getPath() { return path; }

    public static Music getRandomSong() {
        Music[] songs = { SONG1, SONG2, SONG3, SONG4, SONG5, SONG6, SONG7, SONG8, SONG9 };
        Random random = new Random();
        int index = random.nextInt((songs.length - 1));
        return songs[index];
    }
}