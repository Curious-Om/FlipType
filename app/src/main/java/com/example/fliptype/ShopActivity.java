package com.example.fliptype;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    private CoinManager coinManager;
    private PowerUpManager powerUpManager;
    private TextView tvCoinBalance;

    // Counts
    private TextView tvFreezeCount, tvLifeCount, tvDoubleCount, tvHintCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        coinManager = new CoinManager(this);
        powerUpManager = new PowerUpManager(this);

        tvCoinBalance = findViewById(R.id.tvShopCoins);
        tvFreezeCount = findViewById(R.id.tvFreezeCount);
        tvLifeCount = findViewById(R.id.tvLifeCount);
        tvDoubleCount = findViewById(R.id.tvDoubleCount);
        tvHintCount = findViewById(R.id.tvHintCount);

        Button btnBuyFreeze = findViewById(R.id.btnBuyFreeze);
        Button btnBuyLife = findViewById(R.id.btnBuyLife);
        Button btnBuyDouble = findViewById(R.id.btnBuyDouble);
        Button btnBuyHint = findViewById(R.id.btnBuyHint);
        Button btnBack = findViewById(R.id.btnShopBack);

        btnBuyFreeze.setOnClickListener(v -> buyPowerUp(PowerUpManager.PowerUp.TIME_FREEZE));
        btnBuyLife.setOnClickListener(v -> buyPowerUp(PowerUpManager.PowerUp.EXTRA_LIFE));
        btnBuyDouble.setOnClickListener(v -> buyPowerUp(PowerUpManager.PowerUp.DOUBLE_POINTS));
        btnBuyHint.setOnClickListener(v -> buyPowerUp(PowerUpManager.PowerUp.HINT));
        btnBack.setOnClickListener(v -> finish());

        updateUI();
    }

    private void buyPowerUp(PowerUpManager.PowerUp type) {
        if (coinManager.spendCoins(type.cost)) {
            powerUpManager.addPowerUp(type);
            updateUI();
            Toast.makeText(this, type.displayName + " purchased!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        tvCoinBalance.setText(String.valueOf(coinManager.getBalance()));
        tvFreezeCount.setText("Owned: " + powerUpManager.getCount(PowerUpManager.PowerUp.TIME_FREEZE));
        tvLifeCount.setText("Owned: " + powerUpManager.getCount(PowerUpManager.PowerUp.EXTRA_LIFE));
        tvDoubleCount.setText("Owned: " + powerUpManager.getCount(PowerUpManager.PowerUp.DOUBLE_POINTS));
        tvHintCount.setText("Owned: " + powerUpManager.getCount(PowerUpManager.PowerUp.HINT));
    }
}
