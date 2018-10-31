package com.gmail.specifickarma.metronome.test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.specifickarma.metronome.R;

public class ActivityMain extends Activity {

    private Intent startService, sentMessage;
    private BroadcastReceiver mMessageReceiver;
    private Vibrator v;

    View onOff_b, slider_b, sound, flash, back_01_b, waveEffect;
    Button onOff_off, sliderTouch;
    TextView text_bpm;

    int toDegrees;
    int A, B = 0;
    int BPM = 100;

    ViewGroup scaleAndFade_02, back_01, slider_03, scaleAndFade_04, onOff_recolor;

    boolean addA, onOff_stage = true;
    boolean visible,onOff_flash, onOff_sound, turnAorB, hasFlash, hasSound, hasVibr;

    RelativeLayout main_00;
    LinearLayout scale_X3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout2);

        hasFlash = hasCameraFlash();
        hasSound = hasSpeaker();
        hasVibr = hasVibrator();

        main_00 = findViewById(R.id.main);
        main_00.post(new Runnable() {
            @Override
            public void run() {
                int width = main_00.getHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                main_00.setLayoutParams(params);
            }
        });
        String scale = getResources().getString(R.string.layout_scale);
        scale_X3 = findViewById(R.id.scale_container);
        scale_X3.setScaleX(Float.parseFloat(scale));
        scale_X3.setScaleY(Float.parseFloat(scale));

        waveEffect = findViewById(R.id.wave);

        back_01 = findViewById(R.id.back_01);
        back_01_b = findViewById(R.id.back_01_b);

        scaleAndFade_02 = findViewById(R.id.scaleAndFade);
        scaleAndFade_02.post(scaleFade_anim);

        sliderTouch = findViewById(R.id.slideTouch);
        slider_03 = findViewById(R.id.slider_group_03);
        slider_03.post(slider_03_anim);
        slider_b = findViewById(R.id.slider_c);
        onOff_off = findViewById(R.id.slider_onOff);

        onOff_recolor = findViewById(R.id.stage_off);

        scaleAndFade_04 = findViewById(R.id.scale_icons);

        text_bpm = findViewById(R.id.text_bpm);

        sound = findViewById(R.id.sound_b);
        sound.setOnClickListener(onOffFlashAndSound);
        flash = findViewById(R.id.flash_b);
        flash.setOnClickListener(onOffFlashAndSound);

        onOff_b = findViewById(R.id.onOffButton);
        onOff_b.postDelayed(new Runnable() {
            @Override
            public void run() {
                onOff_b.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_LONG).show();
            }
        }, 1500);

        onOff_b.setOnClickListener(onOffClick);

        sliderTouch.setOnTouchListener(touchListener);

        sentMessage = new Intent("toServiceMain");
        startService = new Intent(getApplicationContext(), ServiceMain.class);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        broadcastOnReceive();
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int centerX = size.x;
            int centerY = size.y;

            toDegrees = (int) (Math.toDegrees(Math.atan2(centerY / 2 - event.getRawY(), centerX / 2 - event.getRawX())));

            if (toDegrees > slider_b.getRotation() - 30 && toDegrees < slider_b.getRotation() + 30) {
                slider_b.setRotation(toDegrees);
            }
            if (slider_b.getRotation() >= 175 || slider_b.getRotation() <= -175) {
                slider_b.setRotation(toDegrees);
            }

            if (turnAorB) {
                setA((int) (addA ? slider_b.getRotation() : B));
                addA = !addA;
            } else {
                setB((int) slider_b.getRotation());
            }
            turnAorB = !turnAorB;

            if(A == B){
                // ON FINGER STOP
                if(sliderTouch.isEnabled()){
                    sentBroadcast(sentMessage.putExtra("BPM", BPM));
                }
                return true;
            } else {
                if (B == Math.max(A, B)) {
                    if (BPM < 990){
                        BPM +=10;
                        text_bpm.setText(BPM +"");
                    }
                } else {
                    if (BPM > 50){
                        BPM -=10;
                        text_bpm.setText(BPM +"");
                    }
                }
            }

            switch(event.getAction()) {
                case MotionEvent.ACTION_UP:
                    // RELEASED
                    if(sliderTouch.isEnabled()){
                        sentBroadcast(sentMessage.putExtra("BPM", BPM));
                    }
                    return true;
            }
            return true;
        }
    };

    private void broadcastOnReceive() {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                waveEffect_anim.run();
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("toActivityMain"));
    }

    private void sentBroadcast(Intent intent){
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    View.OnClickListener onOffClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            waveEffect_anim.run();
            sliderTouch.setEnabled(onOff_stage);
            text_bpm.setText(onOff_stage ? BPM + "" : "OFF");
            scaleAndFade_04_anim();

            if (onOff_stage) {
                startService(startService);
                vibrate();
            } else {
                stopService(startService);
                vibrate();
            }
            onOff_stage = !onOff_stage;
        }
    };

    View.OnClickListener onOffFlashAndSound = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.flash_b:
                    if (hasFlash) {
                        flash.setBackground(getDrawable(!onOff_flash ? R.drawable.button_flash_on : R.drawable.button_flash_off));
                        sentBroadcast(sentMessage.putExtra("FLASH", !onOff_flash));
                        sentBroadcast(sentMessage.putExtra("BPM", BPM));
                        onOff_flash = !onOff_flash;
                    } else {
                        Toast.makeText(getApplicationContext(), "No flashlight on your device", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.sound_b:
                    if (hasSound) {
                        sound.setBackground(getDrawable(!onOff_sound ? R.drawable.button_sound_on : R.drawable.button_sound_off));
                        sentBroadcast(sentMessage.putExtra("SOUND", !onOff_sound));
                        sentBroadcast(sentMessage.putExtra("BPM", BPM));
                        onOff_sound = !onOff_sound;
                    } else {
                        Toast.makeText(getApplicationContext(), "No speaker on your device", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    Runnable scaleFade_anim = new Runnable() {
        @Override
        public void run() {
            TransitionSet set = new TransitionSet()
                    .addTransition(new Fade())
                    .addTransition(new Scaale(0.5f))
                    .setStartDelay(600)
                    .setDuration(550)
                    .setInterpolator(new LinearOutSlowInInterpolator());
            TransitionManager.beginDelayedTransition(scaleAndFade_02, set);
            scaleAndFade_02.setVisibility(View.VISIBLE);
        }
    };

    Runnable slider_03_anim = new Runnable() {
        @Override
        public void run() {
            TransitionSet set = new TransitionSet()
                    .addTransition(new Fade())
                    .addTransition(new Scaale(0.3f))
                    .setStartDelay(700)
                    .setDuration(550)
                    .setInterpolator(new FastOutLinearInInterpolator());
            TransitionManager.beginDelayedTransition(slider_03, set);
            slider_03.setVisibility(View.VISIBLE);
        }
    };

    Runnable waveEffect_anim = new Runnable() {
        @Override
        public void run() {
            waveEffect.setPressed(true);
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waveEffect.setPressed(false);
            waveEffect.setLongClickable(false);
        }
    };

    public void scaleAndFade_04_anim() {
        TransitionSet set = new TransitionSet()
                .addTransition(new Fade())
                .addTransition(new Scaale(0.3f))
                .setDuration(400)
                .setInterpolator(new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(scaleAndFade_04, set);
        scaleAndFade_04.setVisibility(visible ? View.INVISIBLE : View.VISIBLE );

        TransitionSet set1 = new TransitionSet()
                .addTransition(new Fade())
                .setInterpolator(new FastOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(onOff_recolor, set1);
        onOff_off.setVisibility(!visible ? View.INVISIBLE : View.VISIBLE );

        visible = !visible;
    }

    public void setA(int a) {
        A = a;
    }

    public void setB(int b) {
        B = b;
    }

    boolean hasCameraFlash() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
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
        } else {
            if(audioManager.isSpeakerphoneOn()){
                return true;
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

    public void vibrate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
            Log.i("VIBRATION", "BRR");
        } else {
            v.vibrate(50);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver((mMessageReceiver));
    }
}
