package com.example.inspection;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

public class SubButton extends FloatingActionButton{
    public SubButton(Context ctx) {
        super(ctx);
    }

    public SubButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onShow(Float x, Float y) {
        this.setVisibility(View.VISIBLE);
        //AnimationSet anSet = new AnimationSet(true);
//        Animation an = new RotateAnimation(0f, 360f, this.getPivotX(), this.getPivotY());
//        an.setDuration(5000);
//        Animation an2 = new TranslateAnimation(Animation.ABSOLUTE, x, Animation.ABSOLUTE, 0f,
//                                                Animation.ABSOLUTE, y, Animation.ABSOLUTE, 0f);
        Animation an2 = new TranslateAnimation( x, 0f,
                y, 0f);
        an2.setDuration(300);
//        this.startAnimation(an);

        this.startAnimation(an2);
        //anSet.addAnimation(an);
        //anSet.addAnimation(an2);
        //this.setRotation(360f);

//        Log.d("HIHI2", "PX :" + this.getPivotX() + " PY :" + this.getPivotY());
//        Log.d("HIHI2", "px:" + this.getPivotX() + "  py:" + this.getPivotY());
//        Log.d("HIHI2", " x:" + this.getX() + "  y:" + this.getY());
//        Log.d("HIHI2", " tx:" + this.getTranslationX() + "  ty:" + this.getTranslationY());
//        Log.d("HIHI2", " rx:" + this.getRotationX() + "  ry:" + this.getRotationY());
//        Log.d("HIHI2", " l:" + this.getLeft() + "  t:" + this.getTop());
//        Log.d("HIHI2", " r:" + this.getRight() + "  b:" + this.getBottom());
        //Log.d("HIHI", "R :" + this.getRotation() + " RX :" + this.getRotationX() + "RY :" + this.getRotationY());
    }
//    @Override
//    protected void onDraw(Canvas canvas) {
//        canvas.save();
//        canvas.rotate(45, getWidth() / 2, getHeight() / 2);
//        super.onDraw(canvas);
//        canvas.restore();
//    }
}
