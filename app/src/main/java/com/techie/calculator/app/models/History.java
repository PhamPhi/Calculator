package com.techie.calculator.app.models;

import android.widget.BaseAdapter;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.util.Vector;

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
public class History {
    private static final int VERSION_1= 1;
    private static final int  MAX_ENTRIES= 100;
    private Vector<HistoryEntry> mEntries = new Vector<HistoryEntry>();

    private int mPosition;
    private BaseAdapter mObserver;

    public History(){
        onClearHistory();
    }

    public History(int version, DataInput in) throws IOException{
        if (version >= VERSION_1){
            for (HistoryEntry entry: mEntries){
                mEntries.add(new HistoryEntry(version, in));
            }
            mPosition = in.readInt();
        } else {
            throw new IOException(" invalid version " + version);
        }

    }

    public void setObserver(BaseAdapter adapter){
        this.mObserver = adapter;
    }

    public void notifyChanged(){
        if (mObserver != null){
            mObserver.notifyDataSetChanged();
        }
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(mEntries.size());
        for( HistoryEntry entry: mEntries){
            entry.write(out);
        }
        out.writeInt(mPosition);
    }

    public HistoryEntry getCurrent(){
        return mEntries.elementAt(mPosition);
    }

    public void update(String text){
        getCurrent().setModifiedContent(text);
    }

    public boolean moveToPrevious(){
        if (mPosition > 0){
            --mPosition;
            return true;
        }
        return false;
    }

    public boolean moveToNext(){
        if (mPosition < mEntries.size() - 1){
            ++mPosition;
            return true;
        }
        return false;
    }

    public void enter(String content){
        getCurrent().onClearModifiedContent();
        if (mEntries.size() >= MAX_ENTRIES){
            mEntries.remove(0);
        }
        if (mEntries.size() < 2 || !content.equals(mEntries.elementAt(mEntries.size() - 2).getBaseContent())){
            mEntries.insertElementAt(new HistoryEntry(content), mEntries.size() - 1);
        }
        mPosition = mEntries.size() -1;
        notifyChanged();
    }

    public void onClearHistory(){

    }
}
