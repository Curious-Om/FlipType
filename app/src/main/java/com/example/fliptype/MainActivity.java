package com.example.fliptype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class MainActivity extends AppCompatActivity {

    private WordBank.Difficulty selectedDifficulty = WordBank.Difficulty.MEDIUM;
    private WordBank.Category selectedCategory = WordBank.Category.GENERAL;
    private HighScoreManager highScoreManager;
    private CoinManager coinManager;
    private TextView tvHighScore;
    private TextView tvCoinBalance;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        highScoreManager = new HighScoreManager(this);
        coinManager = new CoinManager(this);
        tvHighScore = findViewById(R.id.tvHighScore);
        tvCoinBalance = findViewById(R.id.tvCoinBalance);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnDaily = findViewById(R.id.btnDaily);
        Button btnShop = findViewById(R.id.btnShop);
        Button btnAchievements = findViewById(R.id.btnAchievements);
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleDifficulty);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        // Setup category spinner
        String[] categoryNames = new String[WordBank.Category.values().length];
        for (int i = 0; i < WordBank.Category.values().length; i++) {
            String name = WordBank.Category.values()[i].name();
            categoryNames[i] = name.substring(0, 1) + name.substring(1).toLowerCase();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        updateHighScore();
        updateCoins();

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnEasy) {
                    selectedDifficulty = WordBank.Difficulty.EASY;
                } else if (checkedId == R.id.btnMedium) {
                    selectedDifficulty = WordBank.Difficulty.MEDIUM;
                } else if (checkedId == R.id.btnHard) {
                    selectedDifficulty = WordBank.Difficulty.HARD;
                }
                updateHighScore();
            }
        });

        btnPlay.setOnClickListener(v -> {
            selectedCategory = WordBank.Category.values()[spinnerCategory.getSelectedItemPosition()];
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.EXTRA_DIFFICULTY, selectedDifficulty.name());
            intent.putExtra(GameActivity.EXTRA_CATEGORY, selectedCategory.name());
            startActivity(intent);
        });

        btnDaily.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.EXTRA_DIFFICULTY, WordBank.Difficulty.MEDIUM.name());
            intent.putExtra(GameActivity.EXTRA_IS_DAILY, true);
            startActivity(intent);
        });

        btnShop.setOnClickListener(v -> {
            startActivity(new Intent(this, ShopActivity.class));
        });

        btnAchievements.setOnClickListener(v -> {
            startActivity(new Intent(this, AchievementsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHighScore();
        updateCoins();
    }

    private void updateHighScore() {
        int hs = highScoreManager.getHighScore(selectedDifficulty);
        tvHighScore.setText("HIGH SCORE: " + hs);
    }

    private void updateCoins() {
        tvCoinBalance.setText(String.valueOf(coinManager.getBalance()));
    }
}