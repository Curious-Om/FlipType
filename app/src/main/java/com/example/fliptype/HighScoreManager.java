package com.example.fliptype;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreManager {

    private static final String PREFS_NAME = "reverse_rush_prefs";
    private static final String KEY_HIGH_SCORE_EASY = "high_score_easy";
    private static final String KEY_HIGH_SCORE_MEDIUM = "high_score_medium";
    private static final String KEY_HIGH_SCORE_HARD = "high_score_hard";

    private final SharedPreferences prefs;

    public HighScoreManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private String getKey(WordBank.Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return KEY_HIGH_SCORE_EASY;
            case HARD:
                return KEY_HIGH_SCORE_HARD;
            case MEDIUM:
            default:
                return KEY_HIGH_SCORE_MEDIUM;
        }
    }

    public int getHighScore(WordBank.Difficulty difficulty) {
        return prefs.getInt(getKey(difficulty), 0);
    }

    public boolean saveHighScore(WordBank.Difficulty difficulty, int score) {
        int current = getHighScore(difficulty);
        if (score > current) {
            prefs.edit().putInt(getKey(difficulty), score).apply();
            return true;
        }
        return false;
    }
}
