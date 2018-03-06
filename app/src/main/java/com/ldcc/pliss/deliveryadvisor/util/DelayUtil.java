package com.ldcc.pliss.deliveryadvisor.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by pliss on 2018. 2. 27..
 */

public class DelayUtil {


    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000);
    }
}
