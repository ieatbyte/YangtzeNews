package com.yangtze.ieatbyte.yangtzenews.ui.widgets.swiperefresh;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.yangtze.ieatbyte.yangtzenews.R;

public class MySwipeRefreshLayout extends FrameLayout {

    final static String TAG = "MySwipeRefreshLayout";

    private static final int INVALID_POINTER = -1;

    private static final float DRAG_RATE = .5f;

    private static final int ANIMATE_TO_START_DURATION = 200;

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private View mPromptWrapperLayout;
    private View mContentView;

    private int mTouchSlop;

    private float mInitialDownY;

    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsBeingDragged;
    private float mInitialMotionY;
    private float mTotalDragDistance = -1;

    private float mSpinnerFinalOffset;

    private boolean mRefreshing = false;

    protected int mOriginalOffsetTop;
    private int mCurrentTargetOffsetTop;

    private DecelerateInterpolator mDecelerateInterpolator;

    private OnRefreshListener mListener;

    public MySwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        createPromptWrapperLayout();
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mOriginalOffsetTop = 0;
        mCurrentTargetOffsetTop = 0;
        mTotalDragDistance = getContext().getResources().getDisplayMetrics().density * DEFAULT_CIRCLE_TARGET;
        mSpinnerFinalOffset = mTotalDragDistance;
    }

    private void createPromptWrapperLayout() {
        mPromptWrapperLayout = LayoutInflater.from(getContext()).inflate(R.layout.swipe_refresh_prompt, this, false);
        addView(mPromptWrapperLayout);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "child count:" + getChildCount());
        if (mContentView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mPromptWrapperLayout)) {
                    mContentView = child;
                    break;
                }
            }
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mContentView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mContentView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mContentView, -1) || mContentView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mContentView, -1);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (canChildScrollUp()) {
            return false;
        }

        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:{
                mIsBeingDragged = false;
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:{
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            }
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:{
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                mIsBeingDragged = false;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                final float y = MotionEventCompat.getY(event, pointerIndex);
                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    float originalDragPercent = overscrollTop / mTotalDragDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
                    float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
                    float slingshotDist = mSpinnerFinalOffset;
                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;

                    int targetY = mOriginalOffsetTop
                            + (int) ((slingshotDist * dragPercent) + extraMove);

                    mContentView.offsetTopAndBottom((int)(targetY - mCurrentTargetOffsetTop));
                    mCurrentTargetOffsetTop = mContentView.getTop();
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(event);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(event);
                mActivePointerId = MotionEventCompat.getPointerId(event, index);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:{
                if (mActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                        Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    }
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
//                if (overscrollTop > mTotalDragDistance) {
//                    setRefreshing();
//                } else {
                    // cancel refresh
                    mRefreshing = false;
                    animateOffsetToStartPosition(mCurrentTargetOffsetTop);
//                }
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private void setRefreshing() {
        if (mListener != null) {
            mListener.onRefresh();
        }
    }

    private void animateOffsetToStartPosition(int from) {
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mContentView.clearAnimation();
        mContentView.startAnimation(mAnimateToStartPosition);
    }

    private void moveToStart(float interpolatedTime) {
        int targetTop = 0;
        targetTop = (mCurrentTargetOffsetTop + (int) ((mOriginalOffsetTop - mCurrentTargetOffsetTop) * interpolatedTime));
        int offset = targetTop - mContentView.getTop();
        setTargetOffsetTopAndBottom(offset, false /* requires update */);
    }

    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mContentView.bringToFront();
        mContentView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mContentView.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }
}
