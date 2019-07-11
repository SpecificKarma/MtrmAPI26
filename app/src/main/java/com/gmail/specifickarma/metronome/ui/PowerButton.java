package com.gmail.specifickarma.metronome.ui;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.Button;

import com.gmail.specifickarma.metronome.R;

public class PowerButton {


    private Context context;
    private Button power, cursor_off;
    private SliderButton cursor_on;
    private Switch switch_click;
    private ConstraintLayout p_on, c_off, c_on;
    private boolean aBoolean;

    public PowerButton(Context context, View.OnClickListener l) {
        power = ((Activity) context).findViewById(R.id.power);
        p_on = ((Activity) context).findViewById(R.id.on);
        power.setOnClickListener(l);

    }

    public void power_trans() {
        TransitionSet set = new TransitionSet()
                .addTransition(new AutoTransition())
                .setDuration(200)
                .setInterpolator(new LinearOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(p_on, set);
        p_on.setVisibility(aBoolean ? View.INVISIBLE : View.VISIBLE);
        aBoolean = !aBoolean;
    }

    public boolean isVisiable() {
        return aBoolean;
    }
}
