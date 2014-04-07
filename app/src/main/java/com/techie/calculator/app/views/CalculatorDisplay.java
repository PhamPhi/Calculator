package com.techie.calculator.app.views;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ViewSwitcher;
import com.techie.calculator.app.Constant;
import com.techie.calculator.app.controllers.CalculatorEditable;
import com.techie.calculator.app.controllers.LogicController;

/**
 * Copyright (C) 2011 Techie DB Inc. All rights reserved.
 * <p/>
 * Software Development Division,
 * Digital Media & Communications Business, Techie DB Inc.
 * <p/>
 * This software and its documentation are confidential and proprietary information of Techie DB Inc. No part of the software and documents may be
 * copied, reproduced, transmitted, translated, or reduced to any electronic medium or machine-readable form without the prior written consent of
 * Techie DB. Techie DB makes no representations with respect to the contents, and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject to change without notice.
 * <p/>
 * History
 * 2014.04/04/2014.04      larry.pham          Initialize version
 */
public class CalculatorDisplay extends ViewSwitcher {

    protected TranslateAnimation mInAnimUp;
    protected TranslateAnimation mOutAnimup;
    protected TranslateAnimation mInAnimDown;
    protected TranslateAnimation mOutAnimDown;

    private int maxDigits = Constant.DEFAULT_MAX_DIGITS;

    public CalculatorDisplay(Context context) {
        this(context, null);
    }

    public CalculatorDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        maxDigits = attrs.getAttributeIntValue(null, Constant.ATTR_MAX_DIGITS, Constant.DEFAULT_MAX_DIGITS);
    }

    public int getMaxDigits(){
        return maxDigits;
    }

    public void setLogicController(LogicController controller){
        NumberKeyListener calculatorKeyListener = new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return Constant.ACCEPTED_CHARS;
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            }

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return null;
            }
        };
        Editable.Factory factory = new CalculatorEditable.Factory(controller);
        for (int i=0; i< 2; i++){
            EditText text = (EditText) getChildAt(i);
            if (text != null) {
                text.setBackground(null);
                text.setEditableFactory(factory);
                text.setKeyListener(calculatorKeyListener);
                text.setSingleLine();
            }
        }
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        getChildAt(0).setOnKeyListener(l);
        getChildAt(1).setOnKeyListener(l);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mInAnimUp = new TranslateAnimation(0, 0, h, 0);
        mInAnimUp.setDuration(Constant.ANIM_DURATION);

        mOutAnimup = new TranslateAnimation(0,  0,  0, -h);
        mOutAnimup.setDuration(Constant.ANIM_DURATION);

        mInAnimDown = new TranslateAnimation(0, 0, -h, 0);
        mInAnimDown.setDuration(Constant.ANIM_DURATION);

        mOutAnimDown = new TranslateAnimation(0 ,0 , 0, h);
        mOutAnimDown.setDuration(Constant.ANIM_DURATION);
    }

    public void insert(String delta){

        EditText editor = (EditText) getCurrentView();
        if (editor != null) {
            int cursor = editor.getSelectionStart();
            assert editor.getText() != null;
            editor.getText().insert(cursor, delta);
        }
    }

    public EditText getEditText(){
        return (EditText) getCurrentView();
    }

    public Editable getText() {
        EditText text = (EditText) getCurrentView();
        if (text != null) {
            return text.getText();
        }
        return null;
    }

    public void setText(CharSequence text, Constant.Scroll dir){
        if (getText().length() == 0){
          dir = Constant.Scroll.NONE;
        }

        if (dir == Constant.Scroll.UP) {
            setInAnimation(mInAnimUp);
            setOutAnimation(mOutAnimup);
        } else if (dir == Constant.Scroll.DOWN){
            setInAnimation(mInAnimDown);
            setOutAnimation(mOutAnimDown);
        } else {
            setInAnimation(null);
            setOutAnimation(null);
        }
        EditText editText = (EditText) getNextView();
        if (editText != null) {
            editText.setText(text);
        }

        if (editText != null) {
            editText.setSelection(text.length());
        }
        showNext();
    }

    public int getSelectionStart(){
        EditText text = (EditText) getCurrentView();
        return text != null ? text.getSelectionStart() : 0;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (!gainFocus){
            requestFocus();
        }
    }
}
