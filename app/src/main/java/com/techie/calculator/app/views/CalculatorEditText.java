package com.techie.calculator.app.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;
import com.techie.calculator.app.R;

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
public class CalculatorEditText extends EditText {
    private static final String LOG_TAG = "Calculator2";
    private static final int CUT = 0;
    private static final int COPY = 1;
    private static final int PASTE = 2;
    private ImmutableMap<String, String> sReplacementTable;
    private String[] sOperators;

    private String[] mMenuItemStrings;

    public CalculatorEditText(Context context) {
        this(context, null);
    }

    public CalculatorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomSelectionActionModeCallback(new NoTextSelectionMode());
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP){
            cancelLongPress();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performLongClick() {
        showContextMenu();
        return true;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        String mathText = mathParse(getText().toString());
        if (!TextUtils.isEmpty(mathText)){
            event.getText().clear();
            event.getText().add(mathText);
            setContentDescription(mathText);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setText(mathParse(getText().toString()));
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
    }

    private String mathParse(String plainText){
        String parsedText = plainText;
        if (!TextUtils.isEmpty(parsedText)){
            initializeReplacementTable();
            for (String operator: sOperators){
                if (sReplacementTable.containsKey(operator)){
                    parsedText = parsedText.replace(operator, sReplacementTable.get(operator));
                }
            }
        }
        return parsedText;
    }
    private synchronized void initializeReplacementTable(){
        if (sReplacementTable == null){
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            Resources res = getContext().getResources();
            sOperators = res.getStringArray(R.array.operators);
            String[] operatorDescs = res.getStringArray(R.array.operatorDescs);
            int pos = 0;
            for(String key : sOperators){
                builder.put(key, operatorDescs[pos]);
                pos++;
            }
            sReplacementTable = builder.build();
        }
    }
    private class MenuHandler implements MenuItem.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return onTextContextMenuItem(item.getTitle());
        }
    }

    public boolean onTextContextMenuItem(CharSequence title) {
        boolean handled = false;
        if (TextUtils.equals(title, mMenuItemStrings[CUT])) {
            cutContent();
            handled = true;
        } else if (TextUtils.equals(title, mMenuItemStrings[COPY])) {
            copyContent();
            handled = true;
        } else if (TextUtils.equals(title, mMenuItemStrings[PASTE])) {
            pasteContent();
            handled = true;
        }
        return handled;
    }

    @Override
    public void createContextMenu(ContextMenu menu) {
        MenuHandler handler = new MenuHandler();
        if (mMenuItemStrings == null) {
            Resources res = getResources();
            mMenuItemStrings = new String[3];
            mMenuItemStrings[CUT] = res.getString(android.R.string.cut);
            mMenuItemStrings[COPY] = res.getString(android.R.string.copy);
            mMenuItemStrings[PASTE] = res.getString(android.R.string.paste);
        }
        for (int i = 0; i < mMenuItemStrings.length; i++) {
            menu.add(Menu.NONE, i, i, mMenuItemStrings[i]).setOnMenuItemClickListener(handler);
        }
        if (getText().length() == 0) {
            menu.getItem(CUT).setVisible(false);
            menu.getItem(COPY).setVisible(false);
        }
        ClipData primaryClip = getPrimaryClip();
        if (primaryClip == null || primaryClip.getItemCount() == 0 || !canPaste(primaryClip.getItemAt(0).coerceToText(getContext()))) {
            menu.getItem(PASTE).setVisible(false);
        }
    }

    private ClipData getPrimaryClip() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboard.getPrimaryClip();
    }

    private void setPrimaryClip(ClipData clip) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(clip);
    }

    private void copyContent() {
        final Editable text = getText();
        int textLength = text.length();
        setSelection(0, textLength);
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
        Toast.makeText(getContext(), R.string.copied_message_toast, Toast.LENGTH_SHORT).show();
        setSelection(textLength);
    }

    private void cutContent() {
        final Editable text = getText();
        int textLength = text.length();
        setSelection(0, textLength);
        setPrimaryClip(ClipData.newPlainText(null, text));
        ((Editable) getText()).delete(0, textLength);
        setSelection(0);
    }

    private void pasteContent() {
        ClipData clip = getPrimaryClip();
        if (clip != null) {
            for (int i = 0; i < clip.getItemCount(); i++) {
                CharSequence paste = clip.getItemAt(i).coerceToText(getContext());
                if (canPaste(paste)) {
                    assert ((Editable) getText()) != null;
                    ((Editable) getText()).insert(getSelectionEnd(), paste);
                }
            }
        }
    }

    private boolean canPaste(CharSequence paste) {
        boolean canPaste = true;
        try {
            Float.parseFloat(paste.toString());
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Error turning string to integer. Ignoring paste", e);
            canPaste = false;
        }
        return canPaste;
    }

    class NoTextSelectionMode implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}
