package com.sdm3.hackertracker;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ClipboardMonitorService extends Service {

    private static final String TAG = "ClipboardMonitorService";
    private ClipboardManager clipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener onPrimaryClipChangedListener;

    @Override
    public void onCreate() {
        super.onCreate();
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        onPrimaryClipChangedListener = () -> {
            if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClip() != null) {
                CharSequence text = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                if (text != null) {
                    Log.i(TAG, "Clipboard content: " + text);
                    // In a real attack scenario, this data might be stored or exfiltrated.
                    // For this educational app, we just log it.
                }
            }
        };
        clipboardManager.addPrimaryClipChangedListener(onPrimaryClipChangedListener);
        Log.d(TAG, "Clipboard listener added.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (clipboardManager != null && onPrimaryClipChangedListener != null) {
            clipboardManager.removePrimaryClipChangedListener(onPrimaryClipChangedListener);
            Log.d(TAG, "Clipboard listener removed.");
        }
        Log.d(TAG, "ClipboardMonitorService destroyed.");
    }
}
