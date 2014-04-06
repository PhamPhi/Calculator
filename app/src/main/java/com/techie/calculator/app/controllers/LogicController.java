package com.techie.calculator.app.controllers;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;

import com.techie.calculator.app.Constant;
import com.techie.calculator.app.R;
import com.techie.calculator.app.models.History;
import com.techie.calculator.app.views.CalculatorDisplay;

import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
public class LogicController {

    private History mHistory;
    private CalculatorDisplay mDisplay;
    private Symbols mSymbols = new Symbols();

    private String mResult = "";
    private boolean mIsError = false;
    private int mLineLength;

    private final String mErrorString;
    private int mDeleteMode = Constant.DELETE_MODE_BACKSPACE;

    public interface Listener {
        void onDeleteModeChanged();
    }

    private Listener mListener;
    private Context mContext;
    private Set<Map.Entry<String, String>> mTranslationSet;

    public LogicController(Context context, History history, CalculatorDisplay display) {
        mContext = context;
        mErrorString = mContext.getResources().getString(R.string.error);
        mHistory = history;
        mDisplay = display;
        mDisplay.setLogicController(this);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setDeleteMode(int mode) {
        if (mDeleteMode != mode) {
            mDeleteMode = mode;
            mListener.onDeleteModeChanged();
        }
    }

    public int getDeleteMode() {
        return mDeleteMode;
    }

    public void setLineLength(int inDigits) {
        this.mLineLength = inDigits;
    }

    public boolean removeHorizontalMove(boolean toLeft) {
        EditText editText = mDisplay.getEditText();
        int cursorPos = editText.getSelectionStart();
        return toLeft ? cursorPos == 0 : cursorPos >= editText.length();
    }

    private String getText() {
        return mDisplay.getText().toString();
    }

    public void insert(String delta) {
        mDisplay.insert(delta);
        setDeleteMode(Constant.DELETE_MODE_BACKSPACE);
    }

    public void onTextChanged() {
        setDeleteMode(Constant.DELETE_MODE_BACKSPACE);
    }

    public void resumeWithHistory() {
        clearWithHistory(false);
    }

    private void clearWithHistory(boolean scroll) {
        String content = mHistory.getText();
        if (Constant.MARKER_EVALUATE_ON_RESUME.equals(content)) {
            if (!mHistory.moveToPrevious()) {
                content = "";
            }
            content = mHistory.getText();
            evaluateAndShowResult(content, Constant.Scroll.NONE);
        } else {
            mResult = "";
            mDisplay.setText(content, scroll ? Constant.Scroll.UP : Constant.Scroll.NONE);
            mIsError = false;
        }
    }

    public boolean acceptInsert(String delta) {
        String text = getText();
        //return !mIsError && (!mResult.equals(text) || )
        return true;
    }

    private void clear(boolean scroll) {
        mHistory.enter("");
        mDisplay.setText("", scroll ? Constant.Scroll.UP : Constant.Scroll.NONE);
        cleared();
    }

    public void onDelete() {
        if (getText().equals(mResult) || mIsError) {
            clear(false);
        } else {
            mDisplay.dispatchKeyEvent(new KeyEvent(0, KeyEvent.KEYCODE_DEL));
            mResult = "";
        }
    }

    public void onClear() {
        clear(mDeleteMode == Constant.DELETE_MODE_CLEAR);
    }

    public void onEnter() {
        if (mDeleteMode == Constant.DELETE_MODE_CLEAR) {
            clearWithHistory(false);
        } else {
            evaluateAndShowResult(getText(), Constant.Scroll.UP);
        }
    }

    public void evaluateAndShowResult(String text, Constant.Scroll scroll) {
        try {
            String result = evaluate(text);
            if (!text.equals(result)) {
                mHistory.enter(text);
                mResult = result;
                mDisplay.setText(mResult, scroll);
                setDeleteMode(Constant.DELETE_MODE_CLEAR);
            }
        } catch (SyntaxException e) {
            mIsError = true;
            mResult = mErrorString;
            mDisplay.setText(mResult, scroll);
            setDeleteMode(Constant.DELETE_MODE_CLEAR);
        }
    }

    public void onDown() {
        String text = getText();
        if (!text.equals(mResult)) {
            mHistory.update(text);
        }
        if (mHistory.moveToPrevious()) {
            mDisplay.setText(mHistory.getText(), Constant.Scroll.DOWN);
        }
    }

    public void onUp() {
        String text = getText();
        if (!text.equals(mResult)) {
            mHistory.update(text);
        }
        if (mHistory.moveToNext()) {
            mDisplay.setText(mHistory.getText(), Constant.Scroll.UP);
        }
    }

    public void cleared() {
        mResult = "";
        mIsError = false;
    }

    public static boolean isOperator(String text) {
        return text.length() == 1 && isOperator(text.charAt(0));
    }

    public static boolean isOperator(char c) {
        String operators = Constant.OPERATOR_CHARS.toString();
        return operators.indexOf(c) != -1;
    }

    public void updateHistory() {
        String text = getText();
        if (!TextUtils.isEmpty(text) && !TextUtils.equals(text, mErrorString) && text.equals(mResult)) {
            mHistory.update(Constant.MARKER_EVALUATE_ON_RESUME);
        } else {
            mHistory.update(getText());
        }
    }

    public String evaluate(String input) throws SyntaxException {
        if (input.trim().equals("")) {
            return "";
        }
        int size = input.length();
        while (size > 0 && isOperator(input.charAt(size - 1))) {
            input = input.substring(0, size - 1);
            --size;
        }
        input = replaceTranslations(input);
        double value = mSymbols.eval(input);
        for (int precision = mLineLength; precision > 6; precision--) {
            mResult = tryFormattingWithPrecision(value, precision);
            if (mResult.length() <= mLineLength) {
                break;
            }
        }
        return mResult.replace('-', Constant.MINUS).replace(Constant.INFINITY, Constant.INFINITY_UNICODE);
    }

    private void addTranslation(HashMap<String, String> map, int t, int m) {
        Resources res = mContext.getResources();
        String translated = res.getString(t);
        String math = res.getString(m);
        if (!TextUtils.equals(translated, math)) {
            map.put(translated, math);
        }
    }

    private String replaceTranslations(String input) {
        if (mTranslationSet == null) {
            HashMap<String, String> map = new HashMap<String, String>();
            addTranslation(map, R.string.sin, R.string.sin_mathematical_value);
            addTranslation(map, R.string.cos, R.string.cos_mathematical_value);
            addTranslation(map, R.string.tan, R.string.tan_mathematical_value);
            addTranslation(map, R.string.e, R.string.e_mathematical_value);
            addTranslation(map, R.string.ln, R.string.ln_mathematical_value);
            addTranslation(map, R.string.log, R.string.lg_mathematical_value);
            mTranslationSet = map.entrySet();
        }
        for (Map.Entry<String, String> entry : mTranslationSet) {
            input = input.replace(entry.getKey(), entry.getValue());
        }
        return input;
    }

    private String tryFormattingWithPrecision(double value, int precision) {
        String result;
        result = java.lang.String.format(Locale.US, "%" + mLineLength + "." + precision + "g", value);
        if (result.equals(Constant.NAN)) {
            mIsError = true;
            return mErrorString;
        }
        String mantissa = result;
        String exponent = null;
        int e = result.indexOf('e');
        if (e != -1) {
            mantissa = result.substring(0, e);
            exponent = result.substring(e + 1);
            if (exponent.startsWith("+")) {
                exponent = exponent.substring(1);
            }
            exponent = java.lang.String.valueOf(Integer.parseInt(exponent));
        } else {
            mantissa = result;
        }
        int period = mantissa.indexOf('.');
        if (period == -1) period = mantissa.indexOf(',');
        if (period != -1) {
            while (mantissa.length() > 0 && mantissa.endsWith("0")) {
                mantissa = mantissa.substring(0, mantissa.length() - 1);
            }
            if (mantissa.length() == period + 1) {
                mantissa = mantissa.substring(0, mantissa.length() - 1);
            }
        }
        if (exponent != null) {
            result = mantissa + 'e' + exponent;
        } else {
            result = mantissa;
        }
        return result;
    }
}
