package com.sdm3.hackertracker;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class StepCountListener implements SensorEventListener  {

    Context context;

    int initialSteps = 0;

    public StepCountListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Calculate the step count based on the initial value and the current event value
            if (initialSteps == 0) {
                initialSteps = (int) event.values[0];
            } else {
                int stepIncrement = (int) (event.values[0] - initialSteps);
                initialSteps += stepIncrement;

                Intent updateIntent = new Intent(context, StepUpdateManager.class);
                updateIntent.putExtra("stepsIncrement", stepIncrement);
                context.sendBroadcast(updateIntent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
