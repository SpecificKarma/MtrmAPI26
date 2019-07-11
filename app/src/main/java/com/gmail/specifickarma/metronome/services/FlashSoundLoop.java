package com.gmail.specifickarma.metronome.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.gmail.specifickarma.metronome.R;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlashSoundLoop extends Service {
    private ExecutorService es;

    private BroadcastReceiver onBPM, onKILL, onMODES;
    private MediaPlayer mMediaPlayer;
    static boolean FLASH;
    static boolean SOUND;

    boolean isWork = true;
    private int BPM = 68;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.hit);
        es = Executors.newFixedThreadPool(1);

        sentBroadcast(new Intent("toSliderButton"));
        sentBroadcast(new Intent("onStart"));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("SERVICE");
        broadcastOnReceive();
        es.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                long t;
                while (isWork) {
                    try {
                        double res = 60.0 / BPM;

                        t = Long.parseLong(new DecimalFormat("#.000")
                                .format(res).replaceAll("\\.", ""));

                        Thread.sleep(t - 50);

                        if (SOUND) mMediaPlayer.start();
                        if (FLASH && cameraManager != null) flashLightOn(cameraManager);

                        Thread.sleep(50);

                        if (FLASH && cameraManager != null) flashLightOff(cameraManager);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
        return START_STICKY;
    }

    private void broadcastOnReceive() {
        onBPM = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    BPM = intent.getIntExtra("BPM", 68);
                } catch (Exception ignored) {}
            }
        };
        onMODES = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FLASH = intent.getBooleanExtra("FLASH", false);
                SOUND = intent.getBooleanExtra("SOUND", false);
            }
        };
        onKILL = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isWork = intent.getBooleanExtra("KILL", true);
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onBPM,
                new IntentFilter("onBPM"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onMODES,
                new IntentFilter("onMODES"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onKILL,
                new IntentFilter("onKILL"));
    }

    private void flashLightOn(CameraManager cameraManager) {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff(CameraManager cameraManager) {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
        }
    }

    private void sentBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        isWork = false;
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver((onBPM));
        LocalBroadcastManager.getInstance(this).unregisterReceiver((onMODES));
        LocalBroadcastManager.getInstance(this).unregisterReceiver((onKILL));
    }
}
