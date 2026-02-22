package com.example.fliptype;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class SoundManager {

    private ToneGenerator toneGenerator;
    private final Vibrator vibrator;

    public SoundManager(Context context) {
        try {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 40);
        } catch (Exception e) {
            toneGenerator = null;
        }
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void playCorrect() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 80);
        }
        vibrate(25);
    }

    public void playWrong() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, 120);
        }
        vibrate(60);
    }

    public void playGameOver() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250);
        }
        vibrate(150);
    }

    @SuppressWarnings("deprecation")
    private void vibrate(long ms) {
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(ms);
        }
    }

    public void release() {
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }
    }
}
