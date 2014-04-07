package com.techie.calculator.app.controllers;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import com.techie.calculator.app.Constant;

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
public class CalculatorEditable extends SpannableStringBuilder {
    private boolean isInsideReplace = false;
    private LogicController mController;

    private CalculatorEditable(CharSequence source, LogicController controller) {
        super(source);
        this.mController = controller;
    }

    @Override
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
        if (isInsideReplace) {
            return super.replace(start, end, tb, tbstart, tbend);
        } else {
            isInsideReplace = true;
            try{
                String delta = tb.subSequence(tbstart, tbend).toString();
                return internalReplace(start, end, delta);
            }finally {
                isInsideReplace = false;
            }
        }
    }

    private SpannableStringBuilder internalReplace(int start, int end, String delta){
        if (!mController.acceptInsert(delta)){
            mController.cleared();
            start = 0;
            end = length();
        }
        for (int i= Constant.ORIGINALS.length -1; i >= 0; --i){
            delta=  delta.replace(Constant.ORIGINALS[i], Constant.REPLACEMENTS[i]);
        }
        int length = delta.length();
        if (length == 1){
            char text = delta.charAt(0);
            if (text == '.'){
                int position = start - 1;
                while (position >= 0 && Character.isDigit(charAt(position))){
                    -- position;
                }
                if (position >= 0 && charAt(position) == '.'){
                    return super.replace(start, end, "");
                }
            }
            char prevChar = start > 0 ? charAt(start -1): '\0';

            if (text == Constant.MINUS && prevChar == Constant.MINUS){
                return super.replace(start, end, "");
            }
            if (LogicController.isOperator(text)){
                while (LogicController.isOperator(text) && (text != Constant.MINUS || prevChar == '+')){
                    -- start;
                    prevChar = start > 0 ? charAt(start -1) : '\0';
                }
            }

            if (start == 0 &&  LogicController.isOperator(text) && text != Constant.MINUS){
                return super.replace(start, end, "");
            }
        }
        return super.replace(start, end, delta);
    }

    public static class Factory extends Editable.Factory{
        private LogicController controller;
        public Factory(LogicController controller){
            this.controller = controller;
        }

        @Override
        public Editable newEditable(CharSequence source) {
            return new CalculatorEditable(source, controller);
        }
    }
}
