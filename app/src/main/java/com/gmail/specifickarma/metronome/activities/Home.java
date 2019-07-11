package com.gmail.specifickarma.metronome.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.specifickarma.metronome.R;
import com.gmail.specifickarma.metronome.services.FeaturesCheck;
import com.gmail.specifickarma.metronome.services.FlashSoundLoop;
import com.gmail.specifickarma.metronome.ui.PowerButton;
import com.gmail.specifickarma.metronome.ui.SliderButton;
import com.gmail.specifickarma.metronome.ui.Star;
import com.gmail.specifickarma.metronome.ui.Switch;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private BroadcastReceiver onTextChange, onServiceStart;
    private Intent startService, sentBPM, sentKill, sentModes;
    private Welcome trans;
    private SliderButton cursor;
    private PowerButton power;
    private Switch switch_;
    private Star star;
    private TextView text;
    boolean isOn = true;
    boolean switchOn = true;
    private FeaturesCheck fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        trans = new Welcome(Home.this);
        star = new Star(Home.this, this);
        cursor = new SliderButton(Home.this, R.id.cursor_on);
        power = new PowerButton(Home.this, this);
        switch_ = new Switch(Home.this, this);
        text = findViewById(R.id.textView);

        fc = new FeaturesCheck(Home.this);

        sentBPM = new Intent("onBPM");
        sentKill = new Intent("onKILL");
        sentModes = new Intent("onMODES");

        startService = new Intent(getApplicationContext(), FlashSoundLoop.class);

        broadcastOnReceive();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.power:
                fc.vibration();
                if (fc.hasSpeaker()) {
                    power.power_trans();
                    cursor.cursor_trans();
                    switch_.switch_OnOff(power.isVisiable());

                    startStopService(isOn);
                    isOn = !isOn;
                } else {
                    Toast.makeText(getApplicationContext(), "Device doesn't have the Speaker", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.switch_click:
                if (fc.hasCameraFlash()) {
                    switch_.switch_trans(power.isVisiable());
                    switchOn = !switchOn;
                    switchModeStart(switchOn);
                } else {
                    Toast.makeText(getApplicationContext(), "Device doesn't have the Flashlight", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.star:
                star.rateMe();
                break;
        }
    }

    private void broadcastOnReceive() {
        onTextChange = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                text.setText(String.valueOf(" " + cursor.getBPM()));
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onTextChange,
                new IntentFilter("toHome"));

        onServiceStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sentBroadcast(sentModes.putExtra(switch_.getMode() ? "SOUND" : "FLASH", true));
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onServiceStart,
                new IntentFilter("onStart"));
    }

    void startStopService(boolean isOn) {
        if (isOn) {
            startService(startService);
        } else {
            sentBroadcast(sentKill.putExtra("KILL", false));
            stopService(startService);
        }
    }

    void switchModeStart(boolean switchOn) {
        if (switchOn) {
            sentBroadcast(sentModes.putExtra("SOUND", true));
            sentBroadcast(sentModes.putExtra("FLASH", false));
        } else {
            sentBroadcast(sentModes.putExtra("SOUND", false));
            sentBroadcast(sentModes.putExtra("FLASH", true));
        }
    }

    private void sentBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver((onTextChange));
        LocalBroadcastManager.getInstance(this).unregisterReceiver((onServiceStart));
    }
}
