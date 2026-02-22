package com.example.fliptype;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class AchievementManager {

    public static class Achievement {
        public final String id;
        public final String title;
        public final String description;

        Achievement(String id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }
    }

    public static final Achievement[] ALL_ACHIEVEMENTS = {
            new Achievement("first_game", "First Steps", "Complete your first game"),
            new Achievement("score_1000", "Getting Good", "Score 1,000+ in a single game"),
            new Achievement("score_5000", "Word Master", "Score 5,000+ in a single game"),
            new Achievement("combo_5", "On Fire", "Reach a 5\u00d7 combo streak"),
            new Achievement("combo_10", "Unstoppable", "Reach a 10\u00d7 combo streak"),
            new Achievement("sharp", "Sharpshooter", "100% accuracy with 8+ words"),
            new Achievement("speed", "Speed Demon", "Answer 20+ words correctly in one game"),
            new Achievement("streak_3", "Streak Starter", "Maintain a 3-day daily streak"),
            new Achievement("streak_7", "Weekly Warrior", "Maintain a 7-day daily streak"),
            new Achievement("shopper", "Big Spender", "Buy 10 power-ups total"),
    };

    private static final String PREFS = "achievement_prefs";
    private final SharedPreferences prefs;

    public AchievementManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isUnlocked(String id) {
        return prefs.getBoolean(id, false);
    }

    private void unlock(String id) {
        prefs.edit().putBoolean(id, true).apply();
    }

    public int getUnlockedCount() {
        int count = 0;
        for (Achievement a : ALL_ACHIEVEMENTS) {
            if (isUnlocked(a.id)) count++;
        }
        return count;
    }

    /**
     * Check all achievements after a game and return newly unlocked ones.
     */
    public List<Achievement> checkAfterGame(int score, int highestCombo,
                                             int correctCount, int totalCount,
                                             int dailyStreak, int totalPowerUpsBought) {
        List<Achievement> newlyUnlocked = new ArrayList<>();

        // First Steps - complete a game
        if (!isUnlocked("first_game")) {
            unlock("first_game");
            newlyUnlocked.add(findById("first_game"));
        }

        // Score 1000+
        if (!isUnlocked("score_1000") && score >= 1000) {
            unlock("score_1000");
            newlyUnlocked.add(findById("score_1000"));
        }

        // Score 5000+
        if (!isUnlocked("score_5000") && score >= 5000) {
            unlock("score_5000");
            newlyUnlocked.add(findById("score_5000"));
        }

        // Combo 5
        if (!isUnlocked("combo_5") && highestCombo >= 5) {
            unlock("combo_5");
            newlyUnlocked.add(findById("combo_5"));
        }

        // Combo 10
        if (!isUnlocked("combo_10") && highestCombo >= 10) {
            unlock("combo_10");
            newlyUnlocked.add(findById("combo_10"));
        }

        // Sharpshooter - 100% with 8+ words
        if (!isUnlocked("sharp") && correctCount >= 8 && correctCount == totalCount) {
            unlock("sharp");
            newlyUnlocked.add(findById("sharp"));
        }

        // Speed Demon - 20+ correct
        if (!isUnlocked("speed") && correctCount >= 20) {
            unlock("speed");
            newlyUnlocked.add(findById("speed"));
        }

        // Daily streaks
        if (!isUnlocked("streak_3") && dailyStreak >= 3) {
            unlock("streak_3");
            newlyUnlocked.add(findById("streak_3"));
        }
        if (!isUnlocked("streak_7") && dailyStreak >= 7) {
            unlock("streak_7");
            newlyUnlocked.add(findById("streak_7"));
        }

        // Shopper
        if (!isUnlocked("shopper") && totalPowerUpsBought >= 10) {
            unlock("shopper");
            newlyUnlocked.add(findById("shopper"));
        }

        return newlyUnlocked;
    }

    public Achievement findById(String id) {
        for (Achievement a : ALL_ACHIEVEMENTS) {
            if (a.id.equals(id)) return a;
        }
        return null;
    }
}
