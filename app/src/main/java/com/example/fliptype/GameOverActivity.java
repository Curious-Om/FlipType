package com.example.fliptype;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent data = getIntent();
        int score = data.getIntExtra(GameActivity.EXTRA_SCORE, 0);
        int highestCombo = data.getIntExtra(GameActivity.EXTRA_HIGHEST_COMBO, 0);
        int accuracy = data.getIntExtra(GameActivity.EXTRA_ACCURACY, 0);
        int correctCount = data.getIntExtra(GameActivity.EXTRA_CORRECT_COUNT, 0);
        int totalCount = data.getIntExtra(GameActivity.EXTRA_TOTAL_COUNT, 0);
        String difficulty = data.getStringExtra(GameActivity.EXTRA_DIFFICULTY);
        boolean isNewHighScore = data.getBooleanExtra(GameActivity.EXTRA_IS_NEW_HIGH_SCORE, false);
        int coinsEarned = data.getIntExtra(GameActivity.EXTRA_COINS_EARNED, 0);
        ArrayList<String> newAchievements = data.getStringArrayListExtra(GameActivity.EXTRA_NEW_ACHIEVEMENTS);
        boolean isDaily = data.getBooleanExtra(GameActivity.EXTRA_IS_DAILY, false);
        String category = data.getStringExtra(GameActivity.EXTRA_CATEGORY);

        TextView tvFinalScore = findViewById(R.id.tvFinalScore);
        TextView tvNewHighScore = findViewById(R.id.tvNewHighScore);
        TextView tvHighestCombo = findViewById(R.id.tvHighestCombo);
        TextView tvAccuracy = findViewById(R.id.tvAccuracy);
        TextView tvWordsAnswered = findViewById(R.id.tvWordsAnswered);
        TextView tvDifficulty = findViewById(R.id.tvDifficulty);
        TextView tvCoinsEarned = findViewById(R.id.tvCoinsEarned);
        LinearLayout achievementsContainer = findViewById(R.id.achievementsUnlockedContainer);
        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        Button btnHome = findViewById(R.id.btnHome);

        tvFinalScore.setText(String.valueOf(score));
        tvNewHighScore.setVisibility(isNewHighScore ? View.VISIBLE : View.GONE);
        tvHighestCombo.setText(String.valueOf(highestCombo));
        tvAccuracy.setText(accuracy + "%");
        tvWordsAnswered.setText(correctCount + " / " + totalCount);

        // Difficulty / category / daily label
        if (isDaily) {
            tvDifficulty.setText("\uD83D\uDCC5 Daily Challenge");
            tvDifficulty.setTextColor(getColor(R.color.combo_gold));
        } else if (category != null && !category.equals("GENERAL")) {
            String catLabel = category.substring(0, 1) + category.substring(1).toLowerCase();
            if (difficulty != null) {
                tvDifficulty.setText(catLabel + " \u2022 " + difficulty.substring(0, 1) + difficulty.substring(1).toLowerCase());
            } else {
                tvDifficulty.setText(catLabel);
            }
        } else if (difficulty != null) {
            tvDifficulty.setText(difficulty.substring(0, 1) + difficulty.substring(1).toLowerCase());
        }

        // Coins earned
        tvCoinsEarned.setText("+" + coinsEarned + " \uD83E\uDE99");

        // New achievements
        if (newAchievements != null && !newAchievements.isEmpty()) {
            achievementsContainer.setVisibility(View.VISIBLE);
            for (String title : newAchievements) {
                TextView tv = new TextView(this);
                tv.setText("\uD83C\uDFC6 " + title);
                tv.setTextColor(getColor(R.color.combo_gold));
                tv.setTextSize(14);
                tv.setGravity(Gravity.CENTER);
                tv.setPadding(0, 4, 0, 4);
                achievementsContainer.addView(tv);
            }
        } else {
            achievementsContainer.setVisibility(View.GONE);
        }

        // Animate score in
        tvFinalScore.setScaleX(0f);
        tvFinalScore.setScaleY(0f);
        tvFinalScore.animate().scaleX(1f).scaleY(1f).setDuration(500).setStartDelay(200).start();

        if (isNewHighScore) {
            tvNewHighScore.setAlpha(0f);
            tvNewHighScore.animate().alpha(1f).setDuration(500).setStartDelay(600).start();
        }

        btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.EXTRA_DIFFICULTY, difficulty);
            if (category != null) intent.putExtra(GameActivity.EXTRA_CATEGORY, category);
            if (isDaily) intent.putExtra(GameActivity.EXTRA_IS_DAILY, true);
            startActivity(intent);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
