package com.gmail.specifickarma.metronome.activities;

import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;

public class Util {
    static ViewAction swipeSetToStartToMin() {
        final int[] location = new int[2];
        return new GeneralSwipeAction(Swipe.SLOW, new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                view.getLocationInWindow(location);
                return new float[]{
                        location[0], location[1] + view.getPivotY()
                };
            }
        }, new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                return new float[]{
                        (float) (location[0]*2.1), location[1] + view.getPivotY()*2
                };
            }
        }, Press.FINGER);
    }

    static ViewAction swipeSetToStartToMid() {
        final int[] location = new int[2];
        return new GeneralSwipeAction(Swipe.SLOW, new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                view.getLocationInWindow(location);
                return new float[]{
                        location[0], location[1] + view.getPivotY()
                };
            }
        }, new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                return new float[]{
                        location[0]+view.getWidth()/2, view.getPivotY()+20
                };
            }
        }, Press.FINGER);
    }

    static ViewAction swipeSetToMidToMax() {
        final int[] location = new int[2];
        return new GeneralSwipeAction(Swipe.SLOW, new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                view.getLocationInWindow(location);
                return new float[]{
                        view.getPivotX(), view.getPivotY()
                };
            }
        }, new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                return new float[]{
                        view.getPivotX() + view.getWidth() / 2, view.getPivotX() + view.getHeight()
                };
            }
        }, Press.FINGER);
    }
}
