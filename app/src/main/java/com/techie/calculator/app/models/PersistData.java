package com.techie.calculator.app.models;

import android.content.Context;

import java.io.*;

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
public class PersistData {

    private static final int LAST_VERSION = 2;
    //  the Lastest version of current app... Sometime, we can reuse this value for checking the
    // existence of current app
    private static final String FILE_NAME= "calapp.techie"; // the persistent directory for storing the Persistent data
    private Context context;

    private History history = new History(); // The persistent data for storing the history of operations and determinations.
    private int mDeleteMode; // The mode of the button 'delete' used to delete the excess character or returnning back the screen display..

    /**
     * constructor used to initialize the Application Context and basing on this constructor, we can reuse the PeristData throught out
     * ApplicationContext
     * @param context The Application Context.
     */
    public PersistData(Context context){
        this.context = context;
    }

    public void setDeleteMode(int mode){
        mDeleteMode = mode;
    }

    public void load(){
        try{
            InputStream inputStream = new BufferedInputStream(context.openFileInput(FILE_NAME), 1024 * 8);
            DataInputStream  in = new DataInputStream(inputStream);
            int version = in.readInt();
            if (version > 1){
                mDeleteMode= in.readInt();
            }else if(version > LAST_VERSION){
                throw new IOException("data version " + version + "; expected " + LAST_VERSION);
            }
            history = new History(version, in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        try{
            OutputStream os = new BufferedOutputStream(context.openFileOutput(FILE_NAME, 0), 8192);
            DataOutputStream out = new DataOutputStream(os);
            out.writeInt(LAST_VERSION);
            out.writeInt(mDeleteMode);
            history.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
