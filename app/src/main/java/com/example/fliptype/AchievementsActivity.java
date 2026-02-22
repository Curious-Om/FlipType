package com.example.fliptype;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AchievementsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        AchievementManager manager = new AchievementManager(this);
        LinearLayout container = findViewById(R.id.achievementsContainer);
        TextView tvProgress = findViewById(R.id.tvAchievementProgress);
        Button btnBack = findViewById(R.id.btnAchievementsBack);

        int unlocked = manager.getUnlockedCount();
        int total = AchievementManager.ALL_ACHIEVEMENTS.length;
        tvProgress.setText(unlocked + " / " + total + " Unlocked");

        for (AchievementManager.Achievement a : AchievementManager.ALL_ACHIEVEMENTS) {
            boolean isUnlocked = manager.isUnlocked(a.id);
            container.addView(createAchievementRow(a, isUnlocked));
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private LinearLayout createAchievementRow(AchievementManager.Achievement a, boolean unlocked) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(14), dp(16), dp(14));
        row.setBackgroundColor(Color.parseColor("#1E1E2E"));

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 0, 0, dp(2));
        row.setLayoutParams(rowParams);

        // Trophy icon
        TextView icon = new TextView(this);
        icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        icon.setText(unlocked ? "\uD83C\uDFC6" : "\uD83D\uDD12");
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        icon.setGravity(Gravity.CENTER);

        // Text column
        LinearLayout textCol = new LinearLayout(this);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        textCol.setPadding(dp(12), 0, 0, 0);

        TextView title = new TextView(this);
        title.setText(a.title);
        title.setTextColor(unlocked ? Color.WHITE : Color.parseColor("#666666"));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        TextView desc = new TextView(this);
        desc.setText(a.description);
        desc.setTextColor(unlocked ? Color.parseColor("#9E9E9E") : Color.parseColor("#444444"));
        desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        textCol.addView(title);
        textCol.addView(desc);

        // Status
        TextView status = new TextView(this);
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        if (unlocked) {
            status.setText("\u2713");
            status.setTextColor(Color.parseColor("#4CAF50"));
            status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        } else {
            status.setText("LOCKED");
            status.setTextColor(Color.parseColor("#555555"));
        }
        status.setGravity(Gravity.CENTER);

        row.addView(icon);
        row.addView(textCol);
        row.addView(status);

        if (!unlocked) {
            row.setAlpha(0.6f);
        }

        return row;
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
