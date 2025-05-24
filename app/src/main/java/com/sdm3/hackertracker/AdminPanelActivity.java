package com.sdm3.hackertracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        Button btnResetSteps = findViewById(R.id.resetStepsButton);
        Button btnAccessPremium = findViewById(R.id.accessPremiumButton);

        btnResetSteps.setOnClickListener(v -> {
            Intent updateIntent = new Intent("com.sdm3.hackertracker.intent.UPDATE_STEPS");
            updateIntent.putExtra("stepsIncrement", -1);
            getApplicationContext().sendBroadcast(updateIntent);
        });

        btnAccessPremium.setOnClickListener(v ->
                Toast.makeText(this, "Accessing Premium Features...", Toast.LENGTH_SHORT).show());
    }
}
