package com.example.fliptype;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyChallengeManager {

    private static final String PREFS = "daily_prefs";
    private static final String KEY_LAST_DATE = "last_date";
    private static final String KEY_BEST_SCORE = "best_score";
    private static final String KEY_STREAK = "streak";
    private static final String KEY_TOTAL_DAILY = "total_daily";

    private final SharedPreferences prefs;

    public DailyChallengeManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public String getTodayString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public long getTodaySeed() {
        return getTodayString().hashCode();
    }

    public int getTodayBestScore() {
        String lastDate = prefs.getString(KEY_LAST_DATE, "");
        if (getTodayString().equals(lastDate)) {
            return prefs.getInt(KEY_BEST_SCORE, 0);
        }
        return 0;
    }

    /**
     * Save a daily score. Returns true if it's a new daily best.
     */
    public boolean saveDailyScore(int score) {
        String today = getTodayString();
        String lastDate = prefs.getString(KEY_LAST_DATE, "");
        int currentBest = 0;
        int streak = prefs.getInt(KEY_STREAK, 0);

        if (today.equals(lastDate)) {
            currentBest = prefs.getInt(KEY_BEST_SCORE, 0);
        } else {
            // New day — check if streak continues
            if (isYesterday(lastDate)) {
                streak++;
            } else {
                streak = 1;
            }
        }

        boolean isNewBest = score > currentBest;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LAST_DATE, today);
        editor.putInt(KEY_STREAK, streak);
        editor.putInt(KEY_TOTAL_DAILY, prefs.getInt(KEY_TOTAL_DAILY, 0) + 1);
        if (isNewBest) {
            editor.putInt(KEY_BEST_SCORE, score);
        }
        editor.apply();
        return isNewBest;
    }

    public int getStreak() {
        String lastDate = prefs.getString(KEY_LAST_DATE, "");
        String today = getTodayString();
        if (today.equals(lastDate) || isYesterday(lastDate)) {
            return prefs.getInt(KEY_STREAK, 0);
        }
        return 0; // streak broken
    }

    private boolean isYesterday(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d = sdf.parse(dateStr);
            if (d == null) return false;
            long diffMs = new Date().getTime() - d.getTime();
            long diffDays = diffMs / (1000 * 60 * 60 * 24);
            return diffDays == 1;
        } catch (Exception e) {
            return false;
        }
    }
}
