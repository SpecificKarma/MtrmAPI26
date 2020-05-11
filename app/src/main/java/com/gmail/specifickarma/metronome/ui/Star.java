package com.gmail.specifickarma.metronome.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.gmail.specifickarma.metronome.R;

public class Star {
    private Context context;
    private ConstraintLayout star;

    public Star(Context context, View.OnClickListener l) {
        this.context = context;
        star = ((Activity) context).findViewById(R.id.star);
        star.setOnClickListener(l);
    }

    public void rateMe() {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }
}
