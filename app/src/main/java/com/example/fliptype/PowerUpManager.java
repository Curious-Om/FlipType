package com.example.fliptype;

import android.content.Context;
import android.content.SharedPreferences;

public class PowerUpManager {

    public enum PowerUp {
        TIME_FREEZE("Time Freeze", "Pauses word timer for 3 seconds", 15),
        EXTRA_LIFE("Extra Life", "One mistake won't break your combo", 20),
        DOUBLE_POINTS("Double Points", "2\u00d7 score for 10 seconds", 25),
        HINT("Hint", "Reveals first 2 letters of the answer", 10);

        public final String displayName;
        public final String description;
        public final int cost;

        PowerUp(String displayName, String description, int cost) {
            this.displayName = displayName;
            this.description = description;
            this.cost = cost;
        }
    }

    private static final String PREFS = "powerup_prefs";
    private static final String KEY_TOTAL_BOUGHT = "total_bought";
    private final SharedPreferences prefs;

    public PowerUpManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public int getCount(PowerUp type) {
        return prefs.getInt(type.name(), 0);
    }

    public void addPowerUp(PowerUp type) {
        prefs.edit()
                .putInt(type.name(), getCount(type) + 1)
                .putInt(KEY_TOTAL_BOUGHT, getTotalBought() + 1)
                .apply();
    }

    public boolean usePowerUp(PowerUp type) {
        int count = getCount(type);
        if (count > 0) {
            prefs.edit().putInt(type.name(), count - 1).apply();
            return true;
        }
        return false;
    }

    public int getTotalBought() {
        return prefs.getInt(KEY_TOTAL_BOUGHT, 0);
    }
}
