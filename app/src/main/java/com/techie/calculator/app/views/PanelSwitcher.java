package com.techie.calculator.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
/**
 * @author: larry.pham
 * @date: 2014.04.04
 * <p/>
 * Description:
 * Copyright (C) 2014 TechieDB Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class PanelSwitcher extends FrameLayout implements Animation.AnimationListener {

    private static final int MAJOR_MOVE= 60;
    private static final int ANIM_DURATION= 400;

    private GestureDetector mDetector;
    private int mCurrentView;
    private View mChildren[] = new View[0];
    private int mWidth;

    private TranslateAnimation inLeft;
    private TranslateAnimation outLeft;

    private TranslateAnimation inRight;
    private TranslateAnimation outRight;
    private int mPreviousMode;
    private static final int LEFT= 1;
    private static final int RIGHT= 2;

    private Listener mListener;
    public interface Listener{
        void onChanged();
    }

    public PanelSwitcher(Context context) {
        this(context, null);
    }

    public PanelSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCurrentView = 0;
        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int dx = (int) ( e2.getX() - e1.getX());
                if (Math.abs(dx) > MAJOR_MOVE && Math.abs(velocityX) > Math.abs(velocityY)){
                    if (velocityX > 0){
                        onMoveToRight();
                    }else {
                        onMoveToLeft();
                    }
                    return true;
                }else{
                    return false;
                }
            }
        });
    }

    public void onMoveToRight(){
        if (mCurrentView > 0 && mPreviousMode != RIGHT){
            mChildren[mCurrentView - 1].setVisibility(View.VISIBLE);
            mChildren[mCurrentView - 1].startAnimation(inRight);
            mChildren[mCurrentView].startAnimation(outRight);

            mChildren[mCurrentView].setVisibility(View.GONE);
            mCurrentView ++;
            mPreviousMode = RIGHT;
        }
    }

    public void onMoveToLeft(){
        if (mCurrentView > 0 && mPreviousMode != RIGHT){
            mChildren[mCurrentView +1].setVisibility(View.VISIBLE);
            mChildren[mCurrentView +1].startAnimation(inLeft);
            mChildren[mCurrentView].startAnimation(outLeft);

            mChildren[mCurrentView].setVisibility(View.GONE);
            mCurrentView --;
            mPreviousMode = LEFT;
        }
    }

    public void setListener(Listener listener){
        this.mListener = listener;
    }

    public void setCurrentIndex(int current){
        boolean blnChanged = mCurrentView != current;
        mCurrentView = current;
        updateCurrentView();
        if (blnChanged && mListener != null){
            mListener.onChanged();
        }
    }

    public int getCurrentIndex(){
        return mCurrentView;
    }

    public void updateCurrentView(){
        for (int i= mChildren.length -1; i>=0; --i){
            mChildren[i].setVisibility(i == mCurrentView ? View.VISIBLE : View.GONE);
        }
    }

    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight){
        this.mWidth = width;
        inLeft = new TranslateAnimation(mWidth, 0, 0, 0);
        inLeft.setAnimationListener(this);
        outLeft = new TranslateAnimation(0, -mWidth, 0, 0);

        inRight = new TranslateAnimation(-mWidth, 0, 0, 0);
        inRight.setAnimationListener(this);
        outRight = new TranslateAnimation(0, mWidth, 0, 0);

        inLeft.setDuration(ANIM_DURATION);
        outLeft.setDuration(ANIM_DURATION);
        inRight.setDuration(ANIM_DURATION);
        outRight.setDuration(ANIM_DURATION);
    }

    @Override
    protected void onFinishInflate() {
        int count = getChildCount();
        mChildren = new View[count];
        for (int i=0; i< count; i++){
            mChildren[i] = getChildAt(i);
        }
        updateCurrentView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDetector.onTouchEvent(ev);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mListener != null){
            mListener.onChanged();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
