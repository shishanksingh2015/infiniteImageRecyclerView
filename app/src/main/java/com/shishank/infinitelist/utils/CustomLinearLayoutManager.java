package com.shishank.infinitelist.utils;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.SensorManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewConfiguration;

public class CustomLinearLayoutManager extends LinearLayoutManager implements OnFlingChangeListener {
    // Most of the variables are from android.widget.Scroller used for Scroller by recyclerview
   // The scrolling distance calculation logic originates from the same place. Want
    // to use their variables so as to approximate the look of normal Android scrolling.
    // Find the Scroller fling implementation in android.widget.Scroller.fling().
    private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)
    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));

    private double deceleration;

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        calculateDeceleration(context);
    }

    private void calculateDeceleration(Context context) {
        double FRICTION = 0.84;
        deceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.3700787 // inches per meter
                // pixels per inch. 160 is the "default" dpi, i.e. one dip is one pixel on a 160 dpi
                // screen
                * context.getResources().getDisplayMetrics().density * 160.0f * FRICTION;
    }

    @Override
    public int getPositionForVelocity(int velocityY) {
        if (getChildCount() == 0) {
            return 0;
        }

        return calcPosForVelocity(velocityY, getChildAt(0).getTop(), getChildAt(0).getHeight(),
                getPosition(getChildAt(0)));

    }

    private int calcPosForVelocity(int velocity, int scrollPos, int childSize, int currPos) {
        final double dist = getSplineFlingDistance(velocity);

        final double tempScroll = scrollPos + (velocity > 0 ? dist : -dist);

        if (velocity < 0) {
            // Not sure if I need to lower bound this here.
            return (int) Math.max(currPos + tempScroll / childSize, 0);
        } else {
            return (int) (currPos + (tempScroll / childSize) + 1);
        }
    }
    /**
     *Android also have a support library for snap helper took some help from that
     */

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {

                    // I want a behavior where the scrolling always snaps to the beginning of 
                    // the list. Snapping to end is also trivial given the default implementation. 
                    // If you need a different behavior, you may need to override more
                    // of the LinearSmoothScrolling methods.
                    protected int getHorizontalSnapPreference() {
                        return SNAP_TO_START;
                    }

                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START;
                    }

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return CustomLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    private double getSplineFlingDistance(double velocity) {
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return ViewConfiguration.getScrollFriction() * deceleration
                * Math.exp(DECELERATION_RATE / decelMinusOne * l);
    }

    private double getSplineDeceleration(double velocity) {
        return Math.log(INFLEXION * Math.abs(velocity)
                / (ViewConfiguration.getScrollFriction() * deceleration));
    }

    /**
     *This implementation was more of hit and trial
     */
    @Override
    public int getFixScrollPos() {
        if (this.getChildCount() == 0) {
            return 0;
        }

        final View child = getChildAt(0);
        final int childPos = getPosition(child);

       if (getOrientation() == VERTICAL && Math.abs(child.getTop()) > child.getMeasuredWidth() / 2) {
            // Scrolled first view more than halfway offscreen
            return childPos + 1;
        }
        return childPos;
    }

}