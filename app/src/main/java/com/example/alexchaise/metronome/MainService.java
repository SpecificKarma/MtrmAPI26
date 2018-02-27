package com.example.alexchaise.metronome;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainService extends Service {

    private ExecutorService es;
    private boolean isWork = true;
    private boolean isFlashWork = false;
    private long t;


    @Override
    public void onCreate() {
        es = Executors.newFixedThreadPool(1);
        System.out.println("SERVICE STARTED");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        int time = 0;
        try {
            isFlashWork = intent.getExtras().getBoolean("flash");
        } catch (Exception e) {
            System.out.println("FlashInent is't transferred");
        }

        time = Integer.parseInt(intent.getExtras().getString("getProgress"));

        double res = 60.0 / time;
        String tmp = new DecimalFormat("#0.000").format(res);

        t = 0;
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

        final Intent toActivity = new Intent("getServiceData");

        es.execute(new Thread(new Runnable() {
            @Override
            public void run() {

                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                if (t != 0) {
                    while (isWork) {

                        try {
                            Thread.sleep(t - 20);

                            if (isFlashWork && cameraManager != null) {
                                toActivity.putExtra("setIndicator", "ON");
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(toActivity);
                                System.out.println("FLASH ON");
                                flashLightOn(cameraManager);
                                Thread.sleep(20);
                                flashLightOff(cameraManager);
                                System.out.println("FLASH OFF");
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(toActivity);
                                System.out.println("PAUSE - " + t);
                            }
                        } catch (InterruptedException ignored) {
                        }
                        toActivity.putExtra("setIndicator", "OFF");

                    }
                } else {
                    if (isFlashWork) {
                        flashLightOn(cameraManager);
                        System.out.println("CONSTANT LIGHT ON");
                    }
                }
            }
        }));

        return START_STICKY;
    }

    private void flashLightOn(CameraManager cameraManager) {

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);

//            isFlashWork = true;

        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff(CameraManager cameraManager) {

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
//            isFlashWork = false;

        } catch (CameraAccessException e) {
        }
    }


    public void onDestroy() {
        isWork = false;
//        Toast.makeText(getApplicationContext(), "service done", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
