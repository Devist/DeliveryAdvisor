package com.ldcc.pliss.deliveryadvisor.advisor;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.ldcc.pliss.deliveryadvisor.advisor.naver.ClovaTTS;


/**
 * Created by pliss on 2018. 3. 9..
 */

public class AdvisorService extends Service {
    public static final String TAG = "MPS";
    private MediaSessionCompat mediaSession;
    public static ClovaTTS clovaTTS;

    private final MediaSessionCompat.Callback mMediaSessionCallback
            = new MediaSessionCompat.Callback() {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            final String intentAction = mediaButtonEvent.getAction();
            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                final KeyEvent event = mediaButtonEvent.getParcelableExtra(
                        Intent.EXTRA_KEY_EVENT);
                if (event == null) {
                    return super.onMediaButtonEvent(mediaButtonEvent);
                }
                final int keycode = event.getKeyCode();
                final int action = event.getAction();
                if (event.getRepeatCount() == 0 && action == KeyEvent.ACTION_DOWN) {
                    Intent popupIntent = new Intent(getApplicationContext(), AdvisorDialog.class);
                    popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
                    try {
                        pie.send();
                    } catch (PendingIntent.CanceledException e) {
                        //LogUtil.degug(e.getMessage());
                    }
                    switch (keycode) {
                        // Do what you want in here
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            Log.d("KEY","KEYCODE_MEDIA_PLAY_PAUSE");
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            Log.d("KEY","KEYCODE_MEDIA_PAUSE");
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            Log.d("KEY","KEYCODE_MEDIA_PLAY");
                            break;
                    }
                    startService(new Intent(getApplicationContext(), AdvisorService.class));
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("테스트","블루투스 버튼 서비스 시작");
        ComponentName receiver = new ComponentName(getPackageName(), MediaButtonIntentReceiver.class.getName());
        mediaSession = new MediaSessionCompat(this, "PlayerService", receiver, null);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0)
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build());
//        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Test Artist")
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Test Album")
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Test Track Name")
//                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 10000)
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
//                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .build());
        mediaSession.setCallback(mMediaSessionCallback);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                // Ignore
                Log.d("KEY","focusChange=" + focusChange);
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(0);
        mediaSession.setActive(true);

        clovaTTS = new ClovaTTS(getFilesDir());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            Log.d("KEY","mediaSession set PAUSED state");
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE).build());
        } else {
            Log.d("KEY","mediaSession set PLAYING state");
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE).build());
        }
        return START_NOT_STICKY; // super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
    }
}

