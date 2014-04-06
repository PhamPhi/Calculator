package com.techie.calculator.app;

import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.techie.calculator.app.controllers.LogicController;

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
public class EventListener implements View.OnClickListener, View.OnKeyListener, View.OnLongClickListener {

    public LogicController mHandler;
    public ViewPager mPager;

    public void setHandler(LogicController inHandler, ViewPager pager){
        mHandler = inHandler;
        mPager = pager;
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.del:
                mHandler.onDelete();
                break;
            case R.id.clear:
                mHandler.onClear();
                break;
            case R.id.equal:
                mHandler.onEnter();
                break;
            default:
                if (view instanceof Button){
                    String text = ((Button) view).getText().toString();
                    if (text.length() >= 2) {
                        text += '(';
                    }
                    mHandler.insert(text);
                    if (mPager != null && mPager.getCurrentItem() == Calculator.ADVANCED_PANEL){
                        mPager.setCurrentItem(Calculator.BASIC_PANEL);
                    }
                }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int action = event.getAction();
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            final boolean remove = mHandler.removeHorizontalMove(keyCode == KeyEvent.KEYCODE_DPAD_LEFT);
            return remove;
        }
        if (action == KeyEvent.ACTION_MULTIPLE && keyCode == KeyEvent.KEYCODE_UNKNOWN){
            return true;
        }
        if (event.getUnicodeChar() == '='){
            if (action == KeyEvent.ACTION_UP) {
                mHandler.onEnter();
            }
            return true;
        }
        if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER && keyCode != KeyEvent.KEYCODE_DPAD_UP && keyCode != KeyEvent.KEYCODE_DPAD_DOWN
                    && keyCode != KeyEvent.KEYCODE_ENTER){
            if (event.isPrintingKey() && action == event.ACTION_UP){
                mHandler.onTextChanged();
            }
            return false;
        }
        if (action == KeyEvent.ACTION_UP){
            switch (keyCode){
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    mHandler.onEnter();
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    mHandler.onUp();
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mHandler.onDown();
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == R.id.del){
            mHandler.onClear();
            return true;
        }
        return false;
    }
}
