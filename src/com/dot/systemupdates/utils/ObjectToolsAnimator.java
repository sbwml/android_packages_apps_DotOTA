package com.dot.systemupdates.utils;


import android.view.View;

public class ObjectToolsAnimator {
    ObjectToolsAnimator() {}

    public static void moveAndAnimate(View v, String direction, long start, long end, long duration) {
        android.animation.ObjectAnimator mover = android.animation.ObjectAnimator.ofFloat(v, direction, start, end);
        mover.setDuration(duration);
        mover.start();
    }
    public static void moveAndAnimate(View v, String direction, long start, long end) {
        android.animation.ObjectAnimator mover = android.animation.ObjectAnimator.ofFloat(v, direction, start, end);
        mover.setDuration(500);
        mover.start();
    }
    public static void rotate(View v, float from, float to) {
        android.animation.ObjectAnimator mover = android.animation.ObjectAnimator.ofFloat(v, "rotation", from, to);
        mover.setDuration(500);
        mover.start();
    }
    public static void rotate(View v, float from, float to, long duration) {
        android.animation.ObjectAnimator mover = android.animation.ObjectAnimator.ofFloat(v, "rotation", from, to);
        mover.setDuration(duration);
        mover.start();
    }
}
