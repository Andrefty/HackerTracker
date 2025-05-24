package com.sdm3.hackertracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

import kotlin.Suppress;

public class StepCounterActivity extends AppCompatActivity {

    private static final int SENSOR_PERMISSION_CODE = 1;

    private TextView stepCountTextView;

    private int stepCount = 0;

    BroadcastReceiver stepReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        stepCountTextView = findViewById(R.id.stepCount);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_CODE);
            }
        } else {
            startSensorService();
        }

        stepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int steps = intent.getIntExtra("stepsIncrement", 0);
                if (steps < 0) {
                    clearSteps();
                } else {
                    updateSteps(steps);
                }
                SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                if (steps < 0) {
                    editor.putInt("step_count", 0);
                } else {
                    editor.putInt("step_count", prefs.getInt("step_count", 0) + steps);
                }
                editor.apply();
            }
        };
    }

    @SuppressWarnings("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("step_prefs", MODE_PRIVATE);
        int savedSteps = prefs.getInt("step_count", 0);
        setSteps(savedSteps);

        IntentFilter intentFilter = new IntentFilter("com.sdm3.hackertracker.intent.UPDATE_STEPS");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stepReceiver, intentFilter, RECEIVER_EXPORTED);
        } else {
            registerReceiver(stepReceiver, intentFilter);
        }

      TextView premiumTextView = findViewById(R.id.checkPremiumTextView);
        if (premiumTextView != null) {
            if (isPremiumEnabled()) {
                premiumTextView.setText("You are a premium user!");
            } else {
                premiumTextView.setText("");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(stepReceiver);
    }

    private void startSensorService() {
        Intent stepServiceIntent = new Intent(this, StepCounterService.class);
        startService(stepServiceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SENSOR_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSensorService();
            } else {
                Toast.makeText(this, "Permission denied, step counting will not work", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setSteps(int steps) {
        stepCount = steps;
        if (stepCountTextView != null) {
            // Update step count and progress
            stepCountTextView.setText(String.valueOf(stepCount));
        }
    }

    public void updateSteps(int increment) {
        stepCount += increment;
        if (stepCountTextView != null) {
            // Update step count and progress
            stepCountTextView.setText(String.valueOf(stepCount));
        }
    }

    public void clearSteps() {
        stepCount = 0;
        if (stepCountTextView != null) {
            // Update step count and progress
            stepCountTextView.setText(String.valueOf(stepCount));
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
}