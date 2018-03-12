package com.ldcc.pliss.deliveryadvisor.advisor;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by pliss on 2018. 3. 9..
 */

public class MediaButtonIntentReceiver extends BroadcastReceiver {

    private static final long DOUBLE_CLICK_INTERVAL = 700;
    private Context mContext;
    private static boolean sDoubleClick = false;
    private static long sLastClickTime = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"touched!",Toast.LENGTH_SHORT).show();
        Intent popupIntent = new Intent(context, AdvisorDialog.class);
        popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pie= PendingIntent.getActivity(context, 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pie.send();
        } catch (PendingIntent.CanceledException e) {
            //LogUtil.degug(e.getMessage());
        }
        mContext = context;
//        this.abortBroadcast();
        KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if(key.getAction() == KeyEvent.ACTION_DOWN) {
            Toast.makeText(context,"touched!",Toast.LENGTH_SHORT).show();
            int keycode = key.getKeyCode();
            switch(keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    Toast.makeText(context,"touched!",Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    Toast.makeText(context,"touched!",Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    handleHeadsetHook(context, key);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    Toast.makeText(context,"touched!",Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Toast.makeText(context,"touched!",Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Toast.makeText(context,"touched!",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void handleHeadsetHook(Context context, KeyEvent event) {
        final long eventtime = event.getEventTime();
//        ULog.v(getClass().getSimpleName(),"handleHeadsetHook repeat interval="+(eventtime - sLastClickTime));
        if(eventtime - sLastClickTime < DOUBLE_CLICK_INTERVAL) {
            sDoubleClick = true;
            Toast.makeText(context,"touched!",Toast.LENGTH_SHORT).show();
            sLastClickTime = 0;
        }
        else {
            sDoubleClick = false;
            sLastClickTime = eventtime;
            mHandler.sendEmptyMessageDelayed(0, DOUBLE_CLICK_INTERVAL);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(sDoubleClick == false) {
                //play(mContext);
            }
        }

    };
//
//    void next(Context context) {
//        ComponentName service = new ComponentName(context, NotificationPlayerService.class);
//        Intent next = new Intent(NotificationPlayerService.NEXT);
//        next.setComponent(service);
//        context.startService(next);
//    }
//
//    void prev(Context context) {
//        ComponentName service = new ComponentName(context, NotificationPlayerService.class);
//        Intent previous = new Intent(NotificationPlayerService.PREVIOUS);
//        previous.setComponent(service);
//        context.startService(previous);
//    }
//
//    void play(Context context) {
//        ComponentName service = new ComponentName(context, NotificationPlayerService.class);
//        Intent play = new Intent(NotificationPlayerService.PLAY);
//        play.setComponent(service);
//        context.startService(play);
//    }
}

