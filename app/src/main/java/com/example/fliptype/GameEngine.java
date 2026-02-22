package com.example.fliptype;

public class GameEngine {

    public interface GameListener {
        void onScoreUpdated(int score, int pointsEarned);
        void onComboUpdated(int combo, int multiplier);
        void onCorrectAnswer();
        void onWrongAnswer();
        void onNewWord(String word, int timeMs);
        void onGameTimeUpdated(long remainingMs);
        void onGameOver(int finalScore, int highestCombo, int correctCount, int totalCount);
    }

    private static final long GAME_DURATION = 60000;
    private static final int BASE_POINTS = 100;
    private static final int MAX_SPEED_BONUS = 100;

    private final WordBank wordBank;
    private final GameListener listener;

    private String currentWord;
    private int score;
    private int combo;
    private int highestCombo;
    private int correctCount;
    private int totalCount;
    private long wordStartTime;

    // Power-up flags
    private boolean extraLifeActive = false;
    private boolean doublePointsActive = false;

    public GameEngine(WordBank.Difficulty difficulty, GameListener listener) {
        this.wordBank = new WordBank(difficulty);
        this.listener = listener;
        this.score = 0;
        this.combo = 0;
        this.highestCombo = 0;
        this.correctCount = 0;
        this.totalCount = 0;
    }

    public GameEngine(WordBank.Difficulty difficulty, WordBank.Category category,
                      Long seed, GameListener listener) {
        this.wordBank = new WordBank(difficulty, category, seed);
        this.listener = listener;
        this.score = 0;
        this.combo = 0;
        this.highestCombo = 0;
        this.correctCount = 0;
        this.totalCount = 0;
    }

    public void nextWord() {
        currentWord = wordBank.getNextWord();
        wordStartTime = System.currentTimeMillis();
        listener.onNewWord(currentWord, wordBank.getTimePerWord());
    }

    public void submitAnswer(String answer) {
        totalCount++;
        String expected = WordBank.reverse(currentWord);

        if (answer.equalsIgnoreCase(expected)) {
            combo++;
            if (combo > highestCombo) {
                highestCombo = combo;
            }
            correctCount++;

            long elapsed = System.currentTimeMillis() - wordStartTime;
            int timePerWord = wordBank.getTimePerWord();
            double speedRatio = Math.max(0, 1.0 - (double) elapsed / timePerWord);
            int speedBonus = (int) (MAX_SPEED_BONUS * speedRatio);
            int multiplier = getMultiplier();
            int pointsEarned = (BASE_POINTS + speedBonus) * multiplier;
            if (doublePointsActive) pointsEarned *= 2;

            score += pointsEarned;

            listener.onScoreUpdated(score, pointsEarned);
            listener.onComboUpdated(combo, multiplier);
            listener.onCorrectAnswer();
        } else {
            handleWrongAnswer();
        }
    }

    public void onWordTimeout() {
        totalCount++;
        handleWrongAnswer();
    }

    private void handleWrongAnswer() {
        if (extraLifeActive) {
            // Extra life absorbs the mistake — combo preserved
            extraLifeActive = false;
            listener.onComboUpdated(combo, getMultiplier());
            listener.onWrongAnswer();
        } else {
            combo = 0;
            listener.onComboUpdated(combo, 1);
            listener.onWrongAnswer();
        }
    }

    public void setExtraLifeActive(boolean active) {
        this.extraLifeActive = active;
    }

    public boolean isExtraLifeActive() {
        return extraLifeActive;
    }

    public void setDoublePointsActive(boolean active) {
        this.doublePointsActive = active;
    }

    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }

    public int getMultiplier() {
        if (combo >= 10) return 4;
        if (combo >= 5) return 3;
        if (combo >= 3) return 2;
        return 1;
    }

    public long getGameDuration() {
        return GAME_DURATION;
    }

    public int getScore() {
        return score;
    }

    public int getHighestCombo() {
        return highestCombo;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getTimePerWord() {
        return wordBank.getTimePerWord();
    }

    public int getAccuracyPercent() {
        if (totalCount == 0) return 0;
        return (int) ((correctCount * 100.0) / totalCount);
    }
}
