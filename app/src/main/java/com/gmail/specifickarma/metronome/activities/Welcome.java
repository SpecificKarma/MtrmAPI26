package com.gmail.specifickarma.metronome.activities;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.gmail.specifickarma.metronome.R;
import com.gmail.specifickarma.metronome.transitions.Scaale;

public class Welcome {
    private Context context;
    private LinearLayout metro, nome, star, cursor;
    private ConstraintLayout switch_, power, text, ramp;
    private boolean aBoolean;

    public Welcome(Context context) {
        this.context = context;
        metro = ((Activity) context).findViewById(R.id.metro_trans);
        nome = ((Activity) context).findViewById(R.id.nome_trans);
        star = ((Activity) context).findViewById(R.id.star_trans);
        power =  ((Activity) context).findViewById(R.id.power_trans);
        ramp =  ((Activity) context).findViewById(R.id.ramp);
        cursor =  ((Activity) context).findViewById(R.id.c_trans);
        switch_ = ((Activity) context).findViewById(R.id.switch_trans);
        text =  ((Activity) context).findViewById(R.id.text_trans);

        transRun();
    }


    private void transRun(){
//        aBoolean = !aBoolean;
        metro.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(false);
                txt_trans();
                controls_trans();
            }
        });
    }

    private void txt_trans(){
        Slide s = new Slide(aBoolean ? Gravity.RIGHT : Gravity.LEFT);
        TransitionSet set = new TransitionSet()
                .addTransition(s)
                .setStartDelay(2000)
                .setDuration(1000)
                .setInterpolator(new LinearOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(metro, set);
        metro.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
        set = new TransitionSet()
                .addTransition(s)
                .setStartDelay(2100)
                .setDuration(1000)
                .setInterpolator(new LinearOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(nome, set);
        nome.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
    }

    private void controls_trans(){
        aBoolean = !aBoolean;
        Slide s = new Slide(aBoolean ? Gravity.RIGHT : Gravity.LEFT);
        TransitionSet set = new TransitionSet()
                .addTransition(new Scaale(0f))
                .setDuration(800)
                .setStartDelay(2800)
                .setInterpolator(new FastOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(star, set);
        star.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);

        set = new TransitionSet().addTransition(s)
                .setStartDelay(2600)
                .setDuration(900)
                .setInterpolator(new FastOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(ramp, set);
        ramp.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);

        set = new TransitionSet().addTransition(s).addTransition(new Fade())
                .setStartDelay(2600)
                .setDuration(900)
                .setInterpolator(new FastOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(cursor, set);
        cursor.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);

        set = new TransitionSet().addTransition(s)
                .setStartDelay(2670)
                .setDuration(900)
                .setInterpolator(new FastOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(power, set);
        power.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);

        set = new TransitionSet().addTransition(s)
                .setStartDelay(2720)
                .setDuration(900)
                .setInterpolator(new LinearOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(text, set);
        text.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);

        set = new TransitionSet().addTransition(s)
                .setStartDelay(2750)
                .setDuration(900)
                .setInterpolator(new OvershootInterpolator(1f));
        TransitionManager.beginDelayedTransition(switch_, set);
        switch_.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
    }

    private void setVisibility(boolean v){
        star.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
        ramp.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
        power.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
        cursor.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
        switch_.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
        text.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
    }
}
