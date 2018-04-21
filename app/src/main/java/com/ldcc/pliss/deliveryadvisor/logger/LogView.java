/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ldcc.pliss.deliveryadvisor.logger;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

/** Simple TextView which is used to output log data.
*/
public class LogView extends android.support.v7.widget.AppCompatTextView {

    public LogView(Context context) {
        super(context);
    }

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Formats the log data and prints it out to the LogView.
     * @param msg The actual message to be logged. The actual message to be logged.
     */
    public void println(final String msg, final int mode) {

        // In case this was originally called from an AsyncTask or some other off-UI thread,
        // make sure the update occurs within the UI thread.
        ((Activity) getContext()).runOnUiThread( (new Thread(new Runnable() {
            @Override
            public void run() {
                // Display the text we just generated within the LogView.
                appendToLog(msg, mode);
            }
        })));
    }

    /** Outputs the string as a new line of log data in the LogView. */
    public void appendToLog(String s, int mode) {
        if(mode==1){
            String text="";
            String textTime = "⫸⫸" + s + "⫷⫷";
            for(int i = 0 ; i < textTime.length()+1;i++){
                text+="=";
            }
            text+="==";
            setText(text+"\n"+textTime+"\n"+text+"\n" + getText());
            setTextAlignment(TEXT_ALIGNMENT_INHERIT);

        }else{
            setText(s+"\n" + getText());

        }



    }
}
