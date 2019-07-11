package com.gmail.specifickarma.metronome.ui;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gmail.specifickarma.metronome.R;

public class Switch{
    private Context context;
    private LinearLayout s_trans, switch_m1, switch_m2;
    ConstraintLayout switch_click;
    RelativeLayout switch_off_sound, switch_on_sound, switch_off_flash, switch_on_flash, switch_on;
    private boolean aBoolean = true;

    public Switch(Context context, View.OnClickListener l) {
        this.context = context;
        switch_click = ((Activity) context).findViewById(R.id.switch_click);
        switch_click.setOnClickListener(l);

        switch_off_sound = ((Activity) context).findViewById(R.id.switch_off_sound);
        switch_on_sound = ((Activity) context).findViewById(R.id.switch_on_sound);
        switch_off_flash = ((Activity) context).findViewById(R.id.switch_off_flash);
        switch_on_flash = ((Activity) context).findViewById(R.id.switch_on_flash);
        switch_on = ((Activity) context).findViewById(R.id.switch_on);

        switch_m1 = ((Activity) context).findViewById(R.id.switch_l);
        switch_m2 = ((Activity) context).findViewById(R.id.switch_r);
        s_trans = ((Activity) context).findViewById(R.id.s_trans);

    }


    public void switch_trans(boolean isPowerOn){
        if (isPowerOn) {

            TransitionManager.beginDelayedTransition(s_trans, new AutoTransition().setDuration(200));
            switch_m1.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            switch_m2.setVisibility(!aBoolean ? View.VISIBLE : View.GONE);

            switch_mode();

            aBoolean = !aBoolean;
        }
    }

    public void switch_OnOff(boolean isPowerOn){
        if (aBoolean) {
            if (isPowerOn) {
                TransitionManager.beginDelayedTransition(switch_off_sound, new AutoTransition().setDuration(200));
                switch_off_sound.setVisibility(View.INVISIBLE);
                TransitionManager.beginDelayedTransition(switch_on, new AutoTransition().setDuration(200));
                switch_on.setVisibility(View.VISIBLE);
                TransitionManager.beginDelayedTransition(switch_on_sound, new AutoTransition().setDuration(200));
                switch_on_sound.setVisibility(View.VISIBLE);
            } else {
                switch_on_sound.setVisibility(View.INVISIBLE);
                switch_on.setVisibility(View.INVISIBLE);
                switch_off_sound.setVisibility(View.VISIBLE);
            }
        } else {
            if (isPowerOn) {
                switch_off_flash.setVisibility(View.INVISIBLE);
                switch_on.setVisibility(View.VISIBLE);
                switch_on_flash.setVisibility(View.VISIBLE);
            } else {
                switch_on_flash.setVisibility(View.INVISIBLE);
                switch_on.setVisibility(View.INVISIBLE);
                switch_off_flash.setVisibility(View.VISIBLE);
            }
        }
    }


    public void switch_mode() {
        switch_on_sound.setVisibility(aBoolean ?  View.INVISIBLE : View.VISIBLE);
        switch_on_flash.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
    }

    public boolean getMode() {
        return aBoolean;
    }
}
