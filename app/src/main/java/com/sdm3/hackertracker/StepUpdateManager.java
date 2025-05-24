package com.sdm3.hackertracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class StepUpdateManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int steps = intent.getIntExtra("stepsIncrement", 0);
        if (steps >= 0) {
            Intent updateIntent = new Intent("com.sdm3.hackertracker.intent.UPDATE_STEPS");
            updateIntent.putExtra("stepsIncrement", steps);
            context.getApplicationContext().sendBroadcast(updateIntent);
        }
    }
}
