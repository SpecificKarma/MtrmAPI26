package com.gmail.specifickarma.metronome.services;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class FeaturesCheck {

    private Context context;
    private Vibrator v;

    public FeaturesCheck(Context context) {
        this.context = context;
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibration(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(50);
        }
    }

    public boolean hasCameraFlash() {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public boolean hasSpeaker() {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        AudioManager audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        // Check whether the device has a speaker.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check FEATURE_AUDIO_OUTPUT to guard against false positives.
            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
                return false;
            }

            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : devices) {
                if (device.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    return true;
                }
            }
        } else {
            if (audioManager.isSpeakerphoneOn()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVibrator() {
        Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        boolean hasVibrator = false;

        try {
            hasVibrator = mVibrator.hasVibrator();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasVibrator;
    }
}
