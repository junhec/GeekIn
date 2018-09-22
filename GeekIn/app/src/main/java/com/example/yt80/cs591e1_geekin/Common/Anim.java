package com.example.yt80.cs591e1_geekin.Common;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Animation class
 */

public class Anim {
    /**
     * Constructor
     */
    public Anim() {
    }

    /**
     *
     * @param view
     * @param height
     */
    public void slideToBottom(View view, int height){
        if (height == 0) {
            height = view.getHeight();
        }
        TranslateAnimation animate = new TranslateAnimation(0,0,0,height);
        animate.setDuration(150);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    /**
     *
     * @param view
     * @param height
     */
    public void slideToTop(View view, int height){
        if (height == 0) {
            height = view.getHeight();
        }
        TranslateAnimation animate = new TranslateAnimation(0,0,height,0);
        animate.setDuration(150);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

}
