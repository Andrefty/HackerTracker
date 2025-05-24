package com.sdm3.hackertracker;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class StepCounterService extends Service {
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private StepCountListener stepCountListener;

    private HandlerThread sensorThread;
    private Handler sensorHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        // Start a background thread for the sensor listener
        sensorThread = new HandlerThread("SensorThread");
        sensorThread.start();
        sensorHandler = new Handler(sensorThread.getLooper());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor != null) {
            stepCountListener = new StepCountListener(getApplicationContext());
            sensorManager.registerListener(stepCountListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL, sensorHandler);
            Toast.makeText(this, "StepCounterService started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Step Counter not available", Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (stepCountListener != null && sensorManager != null) {
            sensorManager.unregisterListener(stepCountListener);
        }

        if (sensorThread != null) {
            sensorThread.quitSafely();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
