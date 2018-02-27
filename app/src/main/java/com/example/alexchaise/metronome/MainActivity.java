package com.example.alexchaise.metronome;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
    private static final int CAMERA_REQUEST = 50;
    private View.OnClickListener modeFinderLis;
    private View.OnKeyListener onKeyLis;
    private SeekBar.OnSeekBarChangeListener seekBarLis;
    boolean hasCameraFlash, isEnabled, isEdited, wasEdit;

    Intent intent;
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

        hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

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
                        break;
                    case R.id.flash:
                        if (hasCameraFlash) {
                            if (!isEnabled) {
                                if (flash.getAlpha() == 0.5) {
                                    flash.setAlpha(1);

                                    enableStart();

                                    intent.putExtra("flash", true);

                                    System.out.println("FLASH");

                                } else {
                                    disableStart();
                                    intent.putExtra("flash", false);
                                }

                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                                break;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Flashlight doesn't supported", Toast.LENGTH_SHORT).show();
                            break;
                        }

                    case R.id.sound:
                        break;

                    case R.id.trackValue:
                        System.out.println("CLICK EDIT TEXT");

                        trackValue.setHint("");
                        trackValue.setCursorVisible(true);
                        isEdited = true;


                        break;

                    case R.id.btnStart:
                        if (btnStart.getText().equals("START")) {


                            if (!isMyServiceRunning(MainService.class) && !isEdited) {
                                intent.putExtra("getProgress", String.valueOf(seekBar.getProgress()));
                                trackValue.setHint(String.valueOf(seekBar.getProgress()));
                                startService(intent);
                            }

                            //Manual bmp
                            if (!isMyServiceRunning(MainService.class) && isEdited) {
                                //avoiding zero inputs
                                if (trackValue.getText().charAt(0) == '0' ||
                                        trackValue.getText().charAt(0) == '0' && trackValue.getText().charAt(1) == '0' ||
                                        trackValue.getText().charAt(0) == '0' && trackValue.getText().charAt(1) == '0' && trackValue.getText().charAt(2) == '0') {
                                    Toast.makeText(getApplicationContext(), "Please type correct value", Toast.LENGTH_LONG).show();
                                } else {
                                    trackValue.setCursorVisible(false);
                                    intent.putExtra("getProgress", trackValue.getText().toString());
                                    startService(intent);
                                }
                            }
                            btnStart.setText("STOP");
                        } else {
                            btnStart.setText("START");

                            stopService(intent);

                            trackValue.setCursorVisible(false);
                            trackValue.setEnabled(true);

                            if (isEdited) {
                                if (trackValue.getText().length() > 0) {
                                    trackValue.setHint(trackValue.getText());
                                    trackValue.setText("");
                                }
                            }

                            isEdited = false;
                        }
                        break;
                }
            }
        };

        seekBarLis = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress % 10 == 0) {
                    trackValue.setHint((progress + ""));
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
                if (String.valueOf(seekBar.getProgress()).length() == 2) {
                    if (Integer.parseInt(String.valueOf(String.valueOf(seekBar.getProgress()).charAt(1))) >= 5) {
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
                    trackValue.setHint((pro + ""));

                    if (btnStart.isEnabled() && flash.getAlpha() != 0.5 && btnStart.getText() == "STOP") {
                        if (!isMyServiceRunning(MainService.class)) {
                            intent.putExtra("getProgress", String.valueOf(pro));
                            startService(intent);
                        } else {
                            stopService(intent);
                            intent.putExtra("getProgress", String.valueOf(pro));
                            startService(intent);
                        }
                    }
                }
            }
        };


        onKeyLis = new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    stopService(intent);
                    intent.putExtra("getProgress", trackValue.getText());
                    startService(intent);
                    wasEdit = true;
                    isEdited = true;
                    return true;
                }
                return false;
            }
        };
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("CIRCLE CHANGE");

            if (intent.getStringExtra("setIndicator").equals("ON")) {
                colored.setAlpha(1);
            } else {
                colored.setAlpha(0.5f);
                colored.setAlpha(0.2f);
                colored.setAlpha(0);
            }
        }
    };

    @Override
    public void onDestroy() {

        unregisterReceiver(mMessageReceiver);
        super.onDestroy();

    }

    private void enableStart() {
        btnStart.setAlpha(1);
        btnStart.setEnabled(true);
    }

    private void disableStart() {
        btnStart.setAlpha(0.5f);
        btnStart.setEnabled(false);
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
