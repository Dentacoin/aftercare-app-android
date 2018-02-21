package com.dentacoin.dentacare.utils;

/**
 * Created by Atanas Chervarov on 10/29/17.
 */

public enum Voice {

    MORNING_GREETING("sounds/voice/male/morning/greeting.mp3", "sounds/voice/female/morning/greeting.mp3"),
    EVENING_GREETING("sounds/voice/male/evening/greeting.mp3", "sounds/voice/female/evening/greeting.mp3"),

    DAY_GREETING("sounds/voice/male/day/greeting.mp3", "sounds/voice/female/evening/greeting.mp3"),

    BRUSH_EVENING_START("sounds/voice/male/evening/brush/1.mp3", "sounds/voice/female/evening/brush/1.mp3"),
    BRUSH_MORNING_START("sounds/voice/male/morning/brush/1.mp3", "sounds/voice/female/morning/brush/1.mp3"),

    BRUSH_STEP_1("sounds/voice/male/morning/brush/2.mp3", "sounds/voice/female/morning/brush/2.mp3"),
    BRUSH_STEP_2("sounds/voice/male/morning/brush/3.mp3", "sounds/voice/female/morning/brush/3.mp3"),
    BRUSH_STEP_3("sounds/voice/male/morning/brush/4.mp3", "sounds/voice/female/morning/brush/4.mp3"),
    BRUSH_STEP_4("sounds/voice/male/morning/brush/5.mp3", "sounds/voice/female/morning/brush/5.mp3"),
    BRUSH_STOP("sounds/voice/male/morning/brush/6.mp3", "sounds/voice/female/morning/brush/6.mp3"),
    BRUSH_DONE("sounds/voice/male/morning/brush/7.mp3", "sounds/voice/female/morning/brush/7.mp3"),

    FLOSS_START("sounds/voice/male/evening/floss/1.mp3", "sounds/voice/female/evening/floss/1.mp3"),
    FLOSS_STEP_1("sounds/voice/male/evening/floss/2.mp3", "sounds/voice/female/evening/floss/2.mp3"),
    FLOSS_DONE("sounds/voice/male/evening/floss/3.mp3", "sounds/voice/female/evening/floss/3.mp3"),

    RINSE_EVENING_START("sounds/voice/male/evening/rinse/1.mp3", "sounds/voice/female/evening/rinse/1.mp3"),
    RINSE_MORNING_START("sounds/voice/male/morning/rinse/1.mp3", "sounds/voice/female/morning/rinse/1.mp3"),

    RINSE_STEP_1("sounds/voice/male/morning/rinse/2.mp3", "sounds/voice/female/morning/rinse/2.mp3"),
    RINSE_STEP_2("sounds/voice/male/morning/rinse/3.mp3", "sounds/voice/female/morning/rinse/3.mp3"),
    RINSE_STOP("sounds/voice/male/morning/rinse/4.mp3", "sounds/voice/female/morning/rinse/4.mp3"),

    RINSE_EVENING_DONE("sounds/voice/male/evening/rinse/5.mp3", "sounds/voice/female/evening/rinse/5.mp3"),
    RINSE_MORNING_DONE("sounds/voice/male/morning/rinse/5.mp3", "sounds/voice/female/morning/rinse/5.mp3");

    private String male;
    private String female;

    Voice(String male, String female) {
        this.male = male;
        this.female = female;
    }

    public String getMale() { return male; }
    public String getFemale() { return female; }
}