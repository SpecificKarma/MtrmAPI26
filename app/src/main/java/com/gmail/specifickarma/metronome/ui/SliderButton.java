package com.gmail.specifickarma.metronome.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gmail.specifickarma.metronome.R;

public class SliderButton extends android.support.v7.widget.AppCompatButton implements View.OnTouchListener {
    private Intent sentMessage, msgHome;
    private boolean setAorB;
    private boolean turnA = true;
    private Button button;
    private ConstraintLayout c_off, c_on;
    private Button cursor_off;
    private boolean aBoolean;

    private Context context;
    private int A, B, ID;
    private BroadcastReceiver mMessageReceiver;

    public SliderButton(Context context, int ID) {
        super(context);
        this.context = context;
        this.ID = ID;


        c_off = ((Activity) context).findViewById(R.id.c_off);
        c_on = ((Activity) context).findViewById(R.id.c_on);

        cursor_off = ((Activity) context).findViewById(R.id.cursor_off);
        button = ((Activity) context).findViewById(ID);
        button.setOnTouchListener(this);

        msgHome = new Intent("toHome");
        sentMessage = new Intent("onBPM");
        broadcastOnReceive();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        rotationController(button, event);

        return true;
    }


    private void rotationController(Button button, MotionEvent event) {
        int rotDeg;
        Rect rectf = new Rect();
        button.getGlobalVisibleRect(rectf);

        rotDeg = (int) (Math.toDegrees(Math.atan2(rectf.centerY() - event.getRawY(), rectf.centerX() - event.getRawX())));

        //angle constrains and search region
        if (Math.abs(Math.abs(rotDeg) - Math.abs(button.getRotation())) <= 30) {
            if (rotDeg >= -45) {
                button.setRotation(rotDeg);
            }
            if (rotDeg <= -135) {
                button.setRotation(rotDeg);
            }
        }

        if (setAorB) {
            setA((int) (turnA ? button.getRotation() : B));
            turnA = !turnA;
        } else {
            setB((int) button.getRotation());
        }
        setAorB = !setAorB;

        if (A == B) {
            // ON FINGER STOP
        } else {
            sentBroadcast(sentMessage.putExtra("BPM", getBPM()));
            sentBroadcast(msgHome);
        }
    }

    private void broadcastOnReceive() {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sentBroadcast(sentMessage.putExtra("BPM", getBPM()));
            }
        };

        LocalBroadcastManager.getInstance(context.getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("toSliderButton"));

    }

    private int getPercentage(int value) {
        //getting percentage from 270 angle
        if (value >= -45) {
            value += 45;
            return (int) (value / 2.7);
        } else {
            return (int) ((180 + 45 + 180 - Math.abs(value)) / 2.7);
        }
    }

    public int getBPM() {
        return (int) (getPercentage((int) button.getRotation()) * 1.8 + 40);
    }

    public void cursor_trans(){
        cursor_off.setRotation(button.getRotation());
        TransitionSet set = new TransitionSet()
                .addTransition(new AutoTransition())
                .setDuration(200)
                .setInterpolator(new LinearOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(c_off, set);
        c_off.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);

        set = new TransitionSet()
                .addTransition(new AutoTransition())
                .setDuration(200)
                .setInterpolator(new LinearOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(c_on, set);
        c_on.setVisibility(aBoolean ? View.INVISIBLE : View.VISIBLE);
        aBoolean = !aBoolean;
    }

    private void sentBroadcast(Intent intent){
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .sendBroadcast(intent);
    }

    @Override
    public float getRotation() {
        return button.getRotation();
    }

    private void setA(int a) {
        A = a;
    }

    private void setB(int b) {
        B = b;
    }


}