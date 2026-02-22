package com.example.fliptype;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements GameEngine.GameListener {

    public static final String EXTRA_DIFFICULTY = "difficulty";
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_IS_DAILY = "is_daily";
    public static final String EXTRA_SCORE = "score";
    public static final String EXTRA_HIGHEST_COMBO = "highest_combo";
    public static final String EXTRA_ACCURACY = "accuracy";
    public static final String EXTRA_CORRECT_COUNT = "correct_count";
    public static final String EXTRA_TOTAL_COUNT = "total_count";
    public static final String EXTRA_IS_NEW_HIGH_SCORE = "is_new_high_score";
    public static final String EXTRA_COINS_EARNED = "coins_earned";
    public static final String EXTRA_NEW_ACHIEVEMENTS = "new_achievements";

    private TextView tvWord, tvScore, tvCombo, tvPointsPopup, tvGameTime;
    private ProgressBar pbGameTimer, pbWordTimer;
    private EditText etInput;
    private View flashOverlay;

    // Power-up buttons and labels
    private LinearLayout btnFreeze, btnShield, btnDouble, btnHint;
    private TextView tvFreezeCount, tvShieldCount, tvDoubleCount, tvHintCount;
    private TextView tvPowerUpStatus;

    private GameEngine engine;
    private SoundManager soundManager;
    private HighScoreManager highScoreManager;
    private CoinManager coinManager;
    private PowerUpManager powerUpManager;
    private AchievementManager achievementManager;
    private DailyChallengeManager dailyChallengeManager;

    private CountDownTimer gameTimer;
    private CountDownTimer wordTimer;
    private CountDownTimer doublePointsTimer;

    private WordBank.Difficulty difficulty;
    private WordBank.Category category;
    private boolean isDaily = false;
    private boolean gameActive = false;
    private boolean submitting = false;
    private long gameTimeRemaining = 0;
    private long wordTimeRemaining = 0;
    private int currentWordTimeMax = 0;
    private boolean freezeActive = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String diffStr = getIntent().getStringExtra(EXTRA_DIFFICULTY);
        difficulty = WordBank.Difficulty.valueOf(diffStr != null ? diffStr : "MEDIUM");

        String catStr = getIntent().getStringExtra(EXTRA_CATEGORY);
        category = catStr != null ? WordBank.Category.valueOf(catStr) : WordBank.Category.GENERAL;

        isDaily = getIntent().getBooleanExtra(EXTRA_IS_DAILY, false);
        if (isDaily) {
            difficulty = WordBank.Difficulty.MEDIUM;
            category = WordBank.Category.GENERAL;
        }

        initViews();
        soundManager = new SoundManager(this);
        highScoreManager = new HighScoreManager(this);
        coinManager = new CoinManager(this);
        powerUpManager = new PowerUpManager(this);
        achievementManager = new AchievementManager(this);
        dailyChallengeManager = new DailyChallengeManager(this);

        setupInput();
        setupBackHandler();
        updatePowerUpCounts();
        startGame();
    }

    private void initViews() {
        tvWord = findViewById(R.id.tvWord);
        tvScore = findViewById(R.id.tvScore);
        tvCombo = findViewById(R.id.tvCombo);
        tvPointsPopup = findViewById(R.id.tvPointsPopup);
        tvGameTime = findViewById(R.id.tvGameTime);
        pbGameTimer = findViewById(R.id.pbGameTimer);
        pbWordTimer = findViewById(R.id.pbWordTimer);
        etInput = findViewById(R.id.etInput);
        flashOverlay = findViewById(R.id.flashOverlay);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> submitAnswer());

        ImageButton btnQuit = findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(v -> showQuitConfirmation());

        // Power-up buttons
        btnFreeze = findViewById(R.id.btnPowerFreeze);
        btnShield = findViewById(R.id.btnPowerShield);
        btnDouble = findViewById(R.id.btnPowerDouble);
        btnHint = findViewById(R.id.btnPowerHint);
        tvFreezeCount = findViewById(R.id.tvPowerFreezeCount);
        tvShieldCount = findViewById(R.id.tvPowerShieldCount);
        tvDoubleCount = findViewById(R.id.tvPowerDoubleCount);
        tvHintCount = findViewById(R.id.tvPowerHintCount);
        tvPowerUpStatus = findViewById(R.id.tvPowerUpStatus);

        btnFreeze.setOnClickListener(v -> activateFreeze());
        btnShield.setOnClickListener(v -> activateShield());
        btnDouble.setOnClickListener(v -> activateDoublePoints());
        btnHint.setOnClickListener(v -> activateHint());
    }

    private void showQuitConfirmation() {
        if (!gameActive) return;
        if (wordTimer != null) wordTimer.cancel();
        if (gameTimer != null) gameTimer.cancel();

        new MaterialAlertDialogBuilder(this)
                .setTitle("Give Up?")
                .setMessage("Your current score is " + engine.getScore() + ". End this round?")
                .setPositiveButton("QUIT", (dialog, which) -> endGame())
                .setNegativeButton("KEEP PLAYING", (dialog, which) -> resumeTimers())
                .setOnCancelListener(dialog -> resumeTimers())
                .setCancelable(true)
                .show();
    }

    private void setupInput() {
        etInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                submitAnswer();
                return true;
            }
            return false;
        });
    }

    private void setupBackHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showQuitConfirmation();
            }
        });
    }

    private void startGame() {
        Long seed = isDaily ? dailyChallengeManager.getTodaySeed() : null;
        engine = new GameEngine(difficulty, category, seed, this);
        gameActive = true;
        tvScore.setText("0");
        tvCombo.setText("");

        long duration = engine.getGameDuration();
        gameTimeRemaining = duration;
        pbGameTimer.setMax(1000);
        pbGameTimer.setProgress(1000);

        gameTimer = new CountDownTimer(duration, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameTimeRemaining = millisUntilFinished;
                int seconds = (int) Math.ceil(millisUntilFinished / 1000.0);
                tvGameTime.setText(seconds + "s");
                int progress = (int) ((millisUntilFinished * 1000) / duration);
                pbGameTimer.setProgress(progress);
            }

            @Override
            public void onFinish() {
                tvGameTime.setText("0s");
                pbGameTimer.setProgress(0);
                endGame();
            }
        }.start();

        engine.nextWord();
    }

    private void submitAnswer() {
        if (!gameActive || submitting) return;
        String answer = etInput.getText().toString().trim();
        if (answer.isEmpty()) return;

        submitting = true;
        if (wordTimer != null) wordTimer.cancel();

        engine.submitAnswer(answer);

        handler.postDelayed(() -> {
            submitting = false;
            if (gameActive) {
                engine.nextWord();
            }
        }, 350);
    }

    private void startWordTimer(int timeMs) {
        if (wordTimer != null) wordTimer.cancel();
        currentWordTimeMax = timeMs;

        pbWordTimer.setMax(1000);
        pbWordTimer.setProgress(1000);

        wordTimer = new CountDownTimer(timeMs, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                wordTimeRemaining = millisUntilFinished;
                int progress = (int) ((millisUntilFinished * 1000) / timeMs);
                pbWordTimer.setProgress(progress);
            }

            @Override
            public void onFinish() {
                pbWordTimer.setProgress(0);
                if (!gameActive || submitting) return;
                submitting = true;
                engine.onWordTimeout();
                handler.postDelayed(() -> {
                    submitting = false;
                    if (gameActive) engine.nextWord();
                }, 350);
            }
        }.start();
    }

    // ── Power-Up Actions ────────────────────────────────────────

    private void activateFreeze() {
        if (!gameActive || freezeActive) return;
        if (!powerUpManager.usePowerUp(PowerUpManager.PowerUp.TIME_FREEZE)) {
            Toast.makeText(this, "No Time Freeze available!", Toast.LENGTH_SHORT).show();
            return;
        }
        updatePowerUpCounts();
        freezeActive = true;
        if (wordTimer != null) wordTimer.cancel();

        showPowerUpStatus("\u2744 FROZEN!", Color.parseColor("#03A9F4"));

        handler.postDelayed(() -> {
            freezeActive = false;
            hidePowerUpStatus();
            if (gameActive && wordTimeRemaining > 0) {
                // Resume word timer from where it was
                int timeMs = currentWordTimeMax;
                wordTimer = new CountDownTimer(wordTimeRemaining, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        wordTimeRemaining = millisUntilFinished;
                        int progress = (int) ((millisUntilFinished * 1000) / timeMs);
                        pbWordTimer.setProgress(progress);
                    }

                    @Override
                    public void onFinish() {
                        pbWordTimer.setProgress(0);
                        if (!gameActive || submitting) return;
                        submitting = true;
                        engine.onWordTimeout();
                        handler.postDelayed(() -> {
                            submitting = false;
                            if (gameActive) engine.nextWord();
                        }, 350);
                    }
                }.start();
            }
        }, 3000);
    }

    private void activateShield() {
        if (!gameActive) return;
        if (engine.isExtraLifeActive()) {
            Toast.makeText(this, "Shield already active!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!powerUpManager.usePowerUp(PowerUpManager.PowerUp.EXTRA_LIFE)) {
            Toast.makeText(this, "No Extra Life available!", Toast.LENGTH_SHORT).show();
            return;
        }
        updatePowerUpCounts();
        engine.setExtraLifeActive(true);
        showPowerUpStatus("\uD83D\uDEE1 SHIELD ACTIVE", Color.parseColor("#4CAF50"));
    }

    private void activateDoublePoints() {
        if (!gameActive) return;
        if (engine.isDoublePointsActive()) {
            Toast.makeText(this, "Double Points already active!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!powerUpManager.usePowerUp(PowerUpManager.PowerUp.DOUBLE_POINTS)) {
            Toast.makeText(this, "No Double Points available!", Toast.LENGTH_SHORT).show();
            return;
        }
        updatePowerUpCounts();
        engine.setDoublePointsActive(true);
        showPowerUpStatus("\u26A1 DOUBLE POINTS! 10s", Color.parseColor("#FFC107"));

        if (doublePointsTimer != null) doublePointsTimer.cancel();
        doublePointsTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int sec = (int) Math.ceil(millisUntilFinished / 1000.0);
                tvPowerUpStatus.setText("\u26A1 2\u00d7 POINTS! " + sec + "s");
            }

            @Override
            public void onFinish() {
                engine.setDoublePointsActive(false);
                hidePowerUpStatus();
            }
        }.start();
    }

    private void activateHint() {
        if (!gameActive || submitting) return;
        if (!powerUpManager.usePowerUp(PowerUpManager.PowerUp.HINT)) {
            Toast.makeText(this, "No Hints available!", Toast.LENGTH_SHORT).show();
            return;
        }
        updatePowerUpCounts();
        String reversed = WordBank.reverse(engine.getCurrentWord());
        String hint = reversed.substring(0, Math.min(2, reversed.length()));
        etInput.setText(hint);
        etInput.setSelection(hint.length());
    }

    private void showPowerUpStatus(String text, int color) {
        tvPowerUpStatus.setText(text);
        tvPowerUpStatus.setTextColor(color);
        tvPowerUpStatus.setVisibility(View.VISIBLE);
    }

    private void hidePowerUpStatus() {
        tvPowerUpStatus.setVisibility(View.GONE);
    }

    private void updatePowerUpCounts() {
        int fc = powerUpManager.getCount(PowerUpManager.PowerUp.TIME_FREEZE);
        int sc = powerUpManager.getCount(PowerUpManager.PowerUp.EXTRA_LIFE);
        int dc = powerUpManager.getCount(PowerUpManager.PowerUp.DOUBLE_POINTS);
        int hc = powerUpManager.getCount(PowerUpManager.PowerUp.HINT);

        tvFreezeCount.setText(String.valueOf(fc));
        tvShieldCount.setText(String.valueOf(sc));
        tvDoubleCount.setText(String.valueOf(dc));
        tvHintCount.setText(String.valueOf(hc));

        btnFreeze.setAlpha(fc > 0 ? 1f : 0.4f);
        btnShield.setAlpha(sc > 0 ? 1f : 0.4f);
        btnDouble.setAlpha(dc > 0 ? 1f : 0.4f);
        btnHint.setAlpha(hc > 0 ? 1f : 0.4f);
    }

    // ── End Game ────────────────────────────────────────────────

    private void endGame() {
        if (!gameActive) return;
        gameActive = false;
        cleanupTimers();

        soundManager.playGameOver();

        // Calculate and award coins
        int coinsEarned = CoinManager.calculateGameCoins(
                engine.getCorrectCount(), engine.getHighestCombo());
        coinManager.addCoins(coinsEarned);

        // Save scores
        boolean isNewHighScore;
        if (isDaily) {
            isNewHighScore = dailyChallengeManager.saveDailyScore(engine.getScore());
        } else {
            isNewHighScore = highScoreManager.saveHighScore(difficulty, engine.getScore());
        }

        // Check achievements
        int dailyStreak = dailyChallengeManager.getStreak();
        int totalBought = powerUpManager.getTotalBought();
        List<AchievementManager.Achievement> newAchievements = achievementManager.checkAfterGame(
                engine.getScore(), engine.getHighestCombo(),
                engine.getCorrectCount(), engine.getTotalCount(),
                dailyStreak, totalBought);

        ArrayList<String> achievementNames = new ArrayList<>();
        for (AchievementManager.Achievement a : newAchievements) {
            achievementNames.add(a.title);
        }

        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra(EXTRA_SCORE, engine.getScore());
        intent.putExtra(EXTRA_HIGHEST_COMBO, engine.getHighestCombo());
        intent.putExtra(EXTRA_ACCURACY, engine.getAccuracyPercent());
        intent.putExtra(EXTRA_CORRECT_COUNT, engine.getCorrectCount());
        intent.putExtra(EXTRA_TOTAL_COUNT, engine.getTotalCount());
        intent.putExtra(EXTRA_DIFFICULTY, difficulty.name());
        intent.putExtra(EXTRA_CATEGORY, category.name());
        intent.putExtra(EXTRA_IS_DAILY, isDaily);
        intent.putExtra(EXTRA_IS_NEW_HIGH_SCORE, isNewHighScore);
        intent.putExtra(EXTRA_COINS_EARNED, coinsEarned);
        intent.putStringArrayListExtra(EXTRA_NEW_ACHIEVEMENTS, achievementNames);
        startActivity(intent);
        finish();
    }

    private void cleanupTimers() {
        if (gameTimer != null) gameTimer.cancel();
        if (wordTimer != null) wordTimer.cancel();
        if (doublePointsTimer != null) doublePointsTimer.cancel();
        handler.removeCallbacksAndMessages(null);
    }

    private void resumeTimers() {
        if (!gameActive) return;

        long duration = engine.getGameDuration();
        gameTimer = new CountDownTimer(gameTimeRemaining, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameTimeRemaining = millisUntilFinished;
                int seconds = (int) Math.ceil(millisUntilFinished / 1000.0);
                tvGameTime.setText(seconds + "s");
                int progress = (int) ((millisUntilFinished * 1000) / duration);
                pbGameTimer.setProgress(progress);
            }

            @Override
            public void onFinish() {
                tvGameTime.setText("0s");
                pbGameTimer.setProgress(0);
                endGame();
            }
        }.start();

        if (wordTimeRemaining > 0 && !freezeActive) {
            int timeMs = currentWordTimeMax;
            wordTimer = new CountDownTimer(wordTimeRemaining, 50) {
                @Override
                public void onTick(long millisUntilFinished) {
                    wordTimeRemaining = millisUntilFinished;
                    int progress = (int) ((millisUntilFinished * 1000) / timeMs);
                    pbWordTimer.setProgress(progress);
                }

                @Override
                public void onFinish() {
                    pbWordTimer.setProgress(0);
                    if (!gameActive || submitting) return;
                    submitting = true;
                    engine.onWordTimeout();
                    handler.postDelayed(() -> {
                        submitting = false;
                        if (gameActive) engine.nextWord();
                    }, 350);
                }
            }.start();
        }
    }

    // ── GameEngine.GameListener ─────────────────────────────────

    @Override
    public void onScoreUpdated(int score, int pointsEarned) {
        tvScore.setText(String.valueOf(score));

        tvPointsPopup.setText("+" + pointsEarned);
        tvPointsPopup.setAlpha(1f);
        tvPointsPopup.setTranslationY(0f);
        tvPointsPopup.setVisibility(View.VISIBLE);
        tvPointsPopup.animate()
                .alpha(0f)
                .translationYBy(-80f)
                .setDuration(700)
                .withEndAction(() -> tvPointsPopup.setVisibility(View.INVISIBLE))
                .start();
    }

    @Override
    public void onComboUpdated(int combo, int multiplier) {
        if (combo > 1) {
            String comboText = combo + "\u00d7 COMBO";
            if (multiplier > 1) {
                comboText += "  \u00d7" + multiplier;
            }
            tvCombo.setText(comboText);
            tvCombo.setVisibility(View.VISIBLE);
            tvCombo.animate().scaleX(1.15f).scaleY(1.15f).setDuration(100)
                    .withEndAction(() ->
                            tvCombo.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
        } else {
            tvCombo.setText("");
            tvCombo.setVisibility(View.INVISIBLE);
        }

        // Clear shield status when extra life is consumed
        if (!engine.isExtraLifeActive() && tvPowerUpStatus.getVisibility() == View.VISIBLE) {
            String status = tvPowerUpStatus.getText().toString();
            if (status.contains("SHIELD")) {
                hidePowerUpStatus();
            }
        }
    }

    @Override
    public void onCorrectAnswer() {
        soundManager.playCorrect();
        flashScreen(Color.parseColor("#3FB950"));
        tvWord.setTextColor(Color.parseColor("#4CAF50"));
        etInput.setEnabled(false);
    }

    @Override
    public void onWrongAnswer() {
        soundManager.playWrong();
        flashScreen(Color.parseColor("#FF5252"));
        tvWord.setTextColor(Color.parseColor("#FF5252"));
        etInput.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        etInput.setEnabled(false);
    }

    @Override
    public void onNewWord(String word, int timeMs) {
        tvWord.setText(word);
        tvWord.setTextColor(Color.WHITE);
        tvWord.setScaleX(0.7f);
        tvWord.setScaleY(0.7f);
        tvWord.setAlpha(0f);
        tvWord.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(200).start();

        etInput.setText("");
        etInput.setEnabled(true);
        etInput.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etInput, InputMethodManager.SHOW_IMPLICIT);
        }

        startWordTimer(timeMs);
    }

    @Override
    public void onGameTimeUpdated(long remainingMs) { }

    @Override
    public void onGameOver(int finalScore, int highestCombo, int correctCount, int totalCount) {
        endGame();
    }

    private void flashScreen(int color) {
        flashOverlay.setBackgroundColor(color);
        flashOverlay.setAlpha(0.25f);
        flashOverlay.animate().alpha(0f).setDuration(350).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupTimers();
        soundManager.release();
    }
}
