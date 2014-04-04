package com.techie.calculator.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import com.techie.calculator.app.Calculator;

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
public class ColorButton extends Button  implements OnClickListener{
    public int CLICK_FEEDBACK_COLOR;
    public static final int CLICK_FEEDBACK_INTERVAL= 10;
    public static final int CLICK_FEEDBACK_DURATION= 350;

    protected float mTextX;
    protected float mTextY;
    protected OnClickListener mClickListener;

    public ColorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Calculator calc = (Calculator) context;

    }

    @Override
    public void onClick(View v) {

    }
}
