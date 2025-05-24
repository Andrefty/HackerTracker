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

import kotlin.Suppress;

public class StepCounterActivity extends AppCompatActivity {

    private static final int SENSOR_PERMISSION_CODE = 1;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private StepCountListener stepCountListener;
    private TextView stepCountTextView;
    private boolean isSensorRunning = false;

    private int initialSteps = 0;
    private int stepCount = 0;

    BroadcastReceiver stepReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        stepCountTextView = findViewById(R.id.stepCount);

        stepCountListener = new StepCountListener(getApplicationContext());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_CODE);
            }
        } else {
            initializeSensors();
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
                editor.putInt("step_count", prefs.getInt("step_count", 0) + steps);
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

        if (stepCounterSensor != null) {
            sensorManager.registerListener(stepCountListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            isSensorRunning = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(stepReceiver);

        if (isSensorRunning) {
            sensorManager.unregisterListener(stepCountListener, stepCounterSensor);
            isSensorRunning = false;
        }
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        if (stepCounterSensor == null) {
            Toast.makeText(this, "Step counting sensor is not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SENSOR_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSensors();
            } else {
                Toast.makeText(this, "Permission denied, step counting will not work", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setSteps(int steps) {
        if (stepCountTextView != null) {
            // Update step count and progress
            stepCountTextView.setText(String.valueOf(stepCount));
        }
        stepCount = steps;
    }

    public void updateSteps(int increment) {
        if (stepCountTextView != null) {
            // Update step count and progress
            stepCountTextView.setText(String.valueOf(stepCount));
        }
        stepCount += increment;
    }

    public void clearSteps() {
        stepCount = 0;
        if (stepCountTextView != null) {
            // Update step count and progress
            stepCountTextView.setText(String.valueOf(stepCount));
        }
    }
}