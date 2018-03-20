package com.ldcc.pliss.deliveryadvisor.advisor;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Created by pliss on 2018. 3. 9..
 */

public class MediaButtonIntentReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent popupIntent = new Intent(context, AdvisorDialog.class);
        popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pie= PendingIntent.getActivity(context, 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pie.send();
        } catch (PendingIntent.CanceledException e) {
            //LogUtil.degug(e.getMessage());
        }
        mContext = context;
        this.abortBroadcast();
        KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if(key.getAction() == KeyEvent.ACTION_DOWN) {

            int keycode = key.getKeyCode();
            switch(keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:

                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:

                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    //handleHeadsetHook(context, key);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:

                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:

                    break;
                case KeyEvent.KEYCODE_CALL:

                    break;
            }
        }
    }
}

