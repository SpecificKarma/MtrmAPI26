package com.gmail.specifickarma.metronome;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainService extends Service {

    private ExecutorService es;

    private static final String ACTION_SOUND = "com.example.action.SOUND";
    private static final String ACTION_VIBRATION = "com.example.action.VIBRATION";
    private static final String ACTION_FLASH = "com.example.action.FLASH";

    private boolean isWork = true;
    boolean isFlashWork, isVibrWork, isSoundWork;
    private long t;
    private Intent toActivity;
    private Vibrator v;
    private MediaPlayer mMediaPlayer;


    @Override
    public void onCreate() {
        es = Executors.newFixedThreadPool(1);
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.hit);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        int time = 0;

        isFlashWork = intent.hasExtra(ACTION_FLASH);
        isVibrWork = intent.hasExtra(ACTION_VIBRATION);
        isSoundWork = intent.hasExtra(ACTION_SOUND);

        time = intent.getExtras().getInt("getProgress");

        double res = 60.0 / time;
        String tmp = new DecimalFormat("#0.000").format(res);

        if (time != 0) {
            if (time < 1.0) {
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
        }

        es.execute(new Thread(new Runnable() {
            @Override
            public void run() {

                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                toActivity = new Intent("getServiceData");

                // -- • - •
                while (isWork) {
                    toActivity.putExtra("setIndicator", true);

                    try {
                        Thread.sleep(t - 50);

                        LocalBroadcastManager.getInstance(getApplicationContext())
                                .sendBroadcast(toActivity);

                        if (isSoundWork) {
//                            System.out.println("HIT");
                            mMediaPlayer.start();
                        }
                        if (isFlashWork && cameraManager != null) {
//                            System.out.println("FLASH ON");
                            flashLightOn(cameraManager);
                        }
                        if (isVibrWork) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                v.vibrate(50);
                            }
//                            System.out.println("VIBRATION ON");
                        }

                        Thread.sleep(50);
//                        Log.i("PAUSE - ", t + "");

                        if (isFlashWork && cameraManager != null) {
//                            System.out.println("FLASH OFF");
                            flashLightOff(cameraManager);
                        }

                        LocalBroadcastManager.getInstance(getApplicationContext())
                                .sendBroadcast(toActivity);

                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }));

        return START_NOT_STICKY;
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

    public void onDestroy() {
        isWork = false;
    }

    private long delDots(int time, String tmp) {
        if (time != 0) {
            if (time < 1.0) {
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
        }
        return t;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
