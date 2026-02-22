package com.example.fliptype;

import android.content.Context;
import android.content.SharedPreferences;

public class CoinManager {

    private static final String PREFS = "coin_prefs";
    private static final String KEY_BALANCE = "coin_balance";
    private static final String KEY_TOTAL_EARNED = "total_earned";

    private final SharedPreferences prefs;

    public CoinManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public int getBalance() {
        return prefs.getInt(KEY_BALANCE, 0);
    }

    public int getTotalEarned() {
        return prefs.getInt(KEY_TOTAL_EARNED, 0);
    }

    public void addCoins(int amount) {
        prefs.edit()
                .putInt(KEY_BALANCE, getBalance() + amount)
                .putInt(KEY_TOTAL_EARNED, getTotalEarned() + amount)
                .apply();
    }

    public boolean spendCoins(int amount) {
        int bal = getBalance();
        if (bal >= amount) {
            prefs.edit().putInt(KEY_BALANCE, bal - amount).apply();
            return true;
        }
        return false;
    }

    public static int calculateGameCoins(int correctCount, int highestCombo) {
        int coins = correctCount;
        coins += highestCombo / 3;
        coins += 2; // completion bonus
        return Math.max(coins, 0);
    }
}
