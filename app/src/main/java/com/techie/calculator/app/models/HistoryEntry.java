package com.techie.calculator.app.models;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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
public class HistoryEntry {
    private static final int VERSION_1= 1;

    private String mBaseContent;
    private String mModifiedContent;

    public HistoryEntry(String content){
        mBaseContent = content;
        onClearModifiedContent();
    }

    public HistoryEntry(int version, DataInput inData) throws IOException{
        if (version >= VERSION_1){
            mBaseContent = inData.readUTF();
            mModifiedContent = inData.readUTF();
        }else {
            throw new IOException("Invalid Version" +  version);
        }
    }

    public void write(DataOutput out) throws IOException{
        out.writeUTF(mBaseContent);
        out.writeUTF(mModifiedContent);
    }

    public void onClearModifiedContent(){
        this.mModifiedContent = mBaseContent;
    }

    public String getModifiedContent(){
        return mModifiedContent;
    }

    public String getBaseContent(){
        return mBaseContent;
    }

    public void setModifiedContent(String modifiedContent){
        this.mModifiedContent = modifiedContent;
    }

    public void setBaseContent(String baseContent){
        this.mBaseContent = baseContent;
    }

}
