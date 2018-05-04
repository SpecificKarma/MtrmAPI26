package com.example.alexchaise.metronome;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;


public class MainActivity extends Activity {

    Button btnStart, vibration, flash, sound;
    EditText trackValue;
    SeekBar seekBar;
    View colored;

    private static final String ACTION_SOUND = "com.example.action.SOUND";
    private static final String ACTION_VIBRATION = "com.example.action.VIBRATION";
    private static final String ACTION_FLASH = "com.example.action.FLASH";
    private static int getProgress = 50;

    private static final int CAMERA_REQUEST = 50;
    private View.OnClickListener modeFinderLis;
    private View.OnKeyListener onKeyLis;
    private SeekBar.OnSeekBarChangeListener seekBarLis;
    boolean isCameraPermission, isEdited, wasEdit, lastEdit;
    boolean isOnVibration, isOnFlash, isOnSound, isOnStart;
    private BroadcastReceiver mMessageReceiver;
    private Intent intent;
    int pro = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        vibration = findViewById(R.id.vibration);
        flash = findViewById(R.id.flash);
        sound = findViewById(R.id.sound);
        trackValue = findViewById(R.id.trackValue);
        seekBar = findViewById(R.id.seekBar);
        colored = findViewById(R.id.colored);
        btnStart = findViewById(R.id.btnStart);

        sound.setOnClickListener(modeFinderLis);
        flash.setOnClickListener(modeFinderLis);
        vibration.setOnClickListener(modeFinderLis);
        trackValue.setOnClickListener(modeFinderLis);
        trackValue.setOnKeyListener(onKeyLis);
        seekBar.setOnSeekBarChangeListener(seekBarLis);
        btnStart.setOnClickListener(modeFinderLis);

        intent = new Intent(getApplicationContext(), MainService.class);

        isCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;


        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getBooleanExtra("setIndicator", true)) {
                    if (colored.getAlpha() == 1) {
                        colored.setAlpha(0.5f);
                        colored.setAlpha(0.2f);
                        colored.setAlpha(0);
                        System.out.println("INDICATOR 0");
                    } else {
                        colored.setAlpha(1);
                        System.out.println("INDICATOR 1");
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("getServiceData"));

    }

    {
        modeFinderLis = new OnClickListener() {
            @Override
            public void onClick(View v) {

                pro = seekBar.getProgress();

                switch (v.getId()) {
                    case R.id.vibration:
                        if (hasVibrator()) {
                            if (!isOnVibration) {
                                isOnVibration = true;
                                setButtonOpacity(vibration);
                                enableStart();
                                if (isOnStart) {
                                    if (isOnSound) {
                                        intent.putExtra(ACTION_SOUND, "SOUND");
                                    }
                                    if (isOnFlash) {
                                        intent.putExtra(ACTION_FLASH, "FLASH");
                                    }
                                    stopService(intent);

                                    intent.putExtra("getProgress", getProgress);
                                    intent.putExtra(ACTION_VIBRATION, "VIBRATION");

                                    startService(intent);

                                } else {
                                    intent.putExtra(ACTION_VIBRATION, "VIBRATION");
                                }
                            } else {
                                if (!isMyServiceRunning(MainService.class)) {
                                    isOnVibration = false;
                                    setButtonOpacity(vibration);
                                    disableStart(isOnSound, isOnFlash);
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Vibration doesn't supported", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.flash:
                        if (hasCameraFlash()) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                            if (!isOnFlash) {
                                isOnFlash = true;
                                setButtonOpacity(flash);
                                enableStart();
                                if (isOnStart) {
                                    if (isOnSound) {
                                        intent.putExtra(ACTION_SOUND, "SOUND");
                                    }
                                    if (isOnVibration) {
                                        intent.putExtra(ACTION_VIBRATION, "VIBRATION");
                                    }
                                    stopService(intent);
                                    intent.putExtra("getProgress", getProgress);
                                    intent.putExtra(ACTION_FLASH, "FLASH");
                                    startService(intent);
                                } else {
                                    intent.putExtra(ACTION_FLASH, "FLASH");
                                }
                            } else {
                                if (!isMyServiceRunning(MainService.class)) {
                                    isOnFlash = false;
                                    setButtonOpacity(flash);
                                    disableStart(isOnSound, isOnVibration);
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Flash light doesn't supported", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.sound:
                        if (hasSpeaker()) {
                            if (!isOnSound) {
                                isOnSound = true;
                                setButtonOpacity(sound);
                                enableStart();
                                if (isOnStart) {
                                    if (isOnFlash) {
                                        intent.putExtra(ACTION_FLASH, "FLASH");
                                    }
                                    if (isOnVibration) {
                                        intent.putExtra(ACTION_VIBRATION, "VIBRATION");
                                    }
                                    stopService(intent);
                                    System.out.println("Before service " + getProgress);
                                    intent.putExtra("getProgress", getProgress);
                                    intent.putExtra(ACTION_SOUND, "SOUND");
                                    startService(intent);
                                } else {
                                    intent.putExtra(ACTION_SOUND, "SOUND");
                                }
                            } else {
                                if (!isMyServiceRunning(MainService.class)) {
                                    isOnSound = false;
                                    setButtonOpacity(sound);
                                    disableStart(isOnVibration, isOnFlash);
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Speaker doesn't supported", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.trackValue:
                        if (isOnFlash || isOnSound || isOnVibration) {
                            if (isOnVibration) {
                                if (!intent.hasExtra(ACTION_VIBRATION)) {
                                    intent.putExtra(ACTION_VIBRATION, "VIBRATION");
                                }
                            }
                            if (isOnFlash) {
                                if (!intent.hasExtra(ACTION_FLASH)) {
                                    intent.putExtra(ACTION_FLASH, "FLASH");
                                }
                            }
                            if (isOnSound) {
                                if (!intent.hasExtra(ACTION_SOUND)) {
                                    intent.putExtra(ACTION_SOUND, "SOUND");
                                }
                            }
                        }

                        trackValue.setHint("");
                        trackValue.setCursorVisible(true);
                        isEdited = true;
                        lastEdit = true;
                        break;

                    case R.id.btnStart:
                        if (btnStart.getText().equals("START")) {
                            isOnStart = true;

                            if (isOnVibration) {
                                intent.putExtra(ACTION_VIBRATION, "VIBRATION");
                            } else {
                                intent.removeExtra(ACTION_VIBRATION);
                            }
                            if (isOnFlash) {
                                intent.putExtra(ACTION_FLASH, "FLASH");
                            } else {
                                intent.removeExtra(ACTION_FLASH);
                            }
                            if (isOnSound) {
                                intent.putExtra(ACTION_SOUND, "SOUND");
                            } else {
                                intent.removeExtra(ACTION_SOUND);
                            }

                            if (!isMyServiceRunning(MainService.class) && !isEdited) {
                                System.out.println("Before service " + getProgress);
                                intent.putExtra("getProgress", getProgress);


                                trackValue.setHint(String.valueOf(seekBar.getProgress()));


                                startService(intent);
                            }

                            //Manual bmp
                            if (!isMyServiceRunning(MainService.class) && isEdited) {
                                //avoiding zero inputs
                                try {
                                    if (trackValue.getText().charAt(0) == '0' ||
                                            trackValue.getText().charAt(0) == '0' && trackValue.getText().charAt(1) == '0' ||
                                            trackValue.getText().charAt(0) == '0' && trackValue.getText().charAt(1) == '0' && trackValue.getText().charAt(2) == '0') {
                                        Toast.makeText(getApplicationContext(), "Type correct value", Toast.LENGTH_LONG).show();
                                    } else {
                                        trackValue.setCursorVisible(false);

                                        getProgress = Integer.parseInt(trackValue.getText().toString());
                                        intent.putExtra("getProgress", getProgress);

                                        startService(intent);
                                        trackValue.setHint(String.valueOf(seekBar.getProgress()));
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Type correct value", Toast.LENGTH_LONG).show();
                                }
                            }

                            btnStart.setText("STOP");
                        } else {
                            btnStart.setText("START");
                            isOnStart = false;
                            stopService(intent);

                            trackValue.setCursorVisible(false);
                            trackValue.setEnabled(true);

                            if (isEdited && !lastEdit) {
                                if (trackValue.getText().length() > 0) {
                                    trackValue.setHint(trackValue.getText());
                                    trackValue.setText("");
                                }
                            }


                        }
                        break;
                }
            }
        };

        seekBarLis = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress % 10 == 0) {
                    trackValue.setHint(progress + "");
                    pro = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int t;
                String s;
                //If progress 65 it makes 70, otherwise 60
                if (String.valueOf(seekBar.getProgress()).length() == 2) {
                    if (Integer.parseInt(String.valueOf(seekBar.getProgress()).charAt(1) + "") >= 5) {
                        t = Integer.parseInt(String.valueOf(seekBar.getProgress()).charAt(0) + "");
                        t++;
                        s = t + "0";
                        pro = Integer.parseInt(s);
                    } else {
                        t = Integer.parseInt(String.valueOf(seekBar.getProgress()).charAt(0) + "");
                        s = t + "0";
                        pro = Integer.parseInt(s);
                    }
                    trackValue.setText("");
                    getProgress = pro;
                    trackValue.setHint((pro + ""));


                    if ((isOnFlash || isOnSound || isOnVibration) && isOnStart) {
                        if (!isMyServiceRunning(MainService.class)) {

                            intent.putExtra("getProgress", getProgress);
                            startService(intent);
                        } else {
                            stopService(intent);

                            getProgress = pro;
                            intent.putExtra("getProgress", getProgress);
                            startService(intent);
                        }
                    }
                }
                isEdited = false;
                lastEdit = false;
            }
        };


        onKeyLis = new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    System.out.println("EDIT TEXT - ENTER");

                    intent.putExtra("getProgress", getProgress);

                    wasEdit = true;
                    isEdited = true;
                    lastEdit = false;
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(mMessageReceiver);
        super.onDestroy();

    }

    private void enableStart() {
        btnStart.setAlpha(1);
        btnStart.setEnabled(true);
    }

    private void setButtonOpacity(Button btn) {
        if (btn.getAlpha() == 0.5f) {
            btn.setAlpha(1);
        } else {
            btn.setAlpha(0.5f);
        }
    }

    private void disableStart(boolean btnA, boolean btnB) {
        if (btnA || btnB) {
        } else {
            btnStart.setAlpha(0.5f);
            btnStart.setEnabled(false);
        }

    }

    boolean hasSpeaker() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

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
        }
        return false;
    }

    boolean hasVibrator() {
        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        boolean hasVibrator = false;

        try {
            hasVibrator = mVibrator.hasVibrator();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasVibrator;
    }

    boolean hasCameraFlash() {

        PackageManager packageManager = getApplicationContext().getPackageManager();

        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
