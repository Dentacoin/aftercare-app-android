package com.dentacoin.dentacare.utils;

/**
 * Created by Atanas Chervarov on 10/29/17.
 */

public enum Voice {
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

    Voice(String male, String female) {
        this.male = male;
        this.female = female;
    }

    public String getMale() { return male; }
    public String getFemale() { return female; }
}