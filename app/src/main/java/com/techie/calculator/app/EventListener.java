package com.techie.calculator.app;

import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

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

    private Logic mHandler;
    private ViewPager mPager;

    public void setHandler(Logic inHandler, ViewPager pager){
        mHandler = inHandler;
        mPager = pager;
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            //case R.id.del:

        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
