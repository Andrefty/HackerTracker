package com.sdm3.hackertracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        Button btnResetSteps = findViewById(R.id.resetStepsButton);
        Button btnAccessPremium = findViewById(R.id.accessPremiumButton);
        Button btnToggleExperimental = findViewById(R.id.toggleExperimentalButton);

        btnResetSteps.setOnClickListener(v -> {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("step_count", 0);
            editor.apply();

            Toast.makeText(this, "Step count was reset", Toast.LENGTH_SHORT).show();
        });

        btnAccessPremium.setOnClickListener(v -> {
            if (isPremiumEnabled()) {
                Toast.makeText(this, R.string.premium_unlocked, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.premium_locked, Toast.LENGTH_SHORT).show();
            }
        });

        if (isExperimentalFeatureXEnabled()) {
            btnToggleExperimental.setVisibility(View.VISIBLE);
            btnToggleExperimental.setOnClickListener(v ->
                    Toast.makeText(this, R.string.experimental_feature_x_active, Toast.LENGTH_SHORT).show());
        } else {
            btnToggleExperimental.setVisibility(View.GONE);
        }
    }

    private boolean isPremiumEnabled() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class, String.class);
            String enabled = (String) get.invoke(null, "hackertracker.premium.enabled", "false");
            return "true".equalsIgnoreCase(enabled);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isExperimentalFeatureXEnabled() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class, String.class);
            String enabled = (String) get.invoke(null, "hackertracker.exp.featureX", "false");
            return "true".equalsIgnoreCase(enabled);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
