package com.gmail.specifickarma.metronome.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gmail.specifickarma.metronome.R;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceMain extends Service {

    private ExecutorService es;

    private Intent sentMessage;

    private BroadcastReceiver mMessageReceiver;
    private MediaPlayer mMediaPlayer;
    private long t;
    boolean isWork = true;
    static int BPM = 100;
    static boolean FLASH, SOUND;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sentMessage = new Intent("toActivityMain");
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.hit);
        es = Executors.newFixedThreadPool(1);
        broadcastOnReceive();
        Log.i("SERVICE", "STARTED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SERVICE", "onStartCommand");
        es.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                while (isWork) {
                    try {
                        double res = 60.0 / BPM;
                        Log.i("BPM", String.valueOf(BPM));
                        String tmp = new DecimalFormat("#0.000").format(res);

                        if (BPM < 1.0) {
                            try {
                                t = Long.parseLong((tmp.split("\\.")[1])) * 10;
                            } catch (NumberFormatException e) {
                                t = Long.parseLong(tmp.split(",")[1]) * 10;
                            }
                        } else {
                            try {
                                t = Long.parseLong(tmp.replaceAll("\\.", ""));
                            } catch (NumberFormatException e) {
                                t = Long.parseLong(tmp.replaceAll(",", ""));
                            }
                        }

                        Thread.sleep(t - 50);

                        if (FLASH && cameraManager != null) {
                            flashLightOn(cameraManager);
                        }
                        if (SOUND) {
                            mMediaPlayer.start();
                        }

                        Thread.sleep(50);

                        if (FLASH && cameraManager != null) {
                            flashLightOff(cameraManager);
                        }
                        sentBroadcast(sentMessage.putExtra("WAVE", true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
        return START_NOT_STICKY;
    }

    private void broadcastOnReceive(){

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    BPM = intent.getIntExtra("BPM", 100);
                } catch (Exception ignored) {}

                FLASH = intent.getBooleanExtra("FLASH", false);
                SOUND = intent.getBooleanExtra("SOUND", false);
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("toServiceMain"));

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

    private void sentBroadcast(Intent intent){
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        isWork = false;
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver((mMessageReceiver));
    }
}
