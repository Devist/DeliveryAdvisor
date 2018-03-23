package com.ldcc.pliss.deliveryadvisor.advisor.google;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.advisor.VoiceAnalyzer;
import com.ldcc.pliss.deliveryadvisor.advisor.VoiceRecorder;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.ldcc.pliss.deliveryadvisor.MainActivity.fa;

/**
 * Created by pliss on 2018. 3. 13..
 */

public class SpeechHelper implements MessageDialogFragment.Listener{
    //private static final String STATE_RESULTS = "results";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechService mSpeechService;

    private Activity activity;
    private Context context;
    private static VoiceRecorder mVoiceRecorder;

    private TextView mStatus;
    private VoiceAnalyzer voiceAnalyzer;
    public SpeechHelper(Activity activity, VoiceAnalyzer voiceAnalyzer){
        this.activity=activity;
        this.voiceAnalyzer = voiceAnalyzer;
    }

    private static String finalMessage ="";


    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            showStatus(true);
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (!TextUtils.isEmpty(text)) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    Toast.makeText(activity,"인식된 문서는 : "+text,Toast.LENGTH_SHORT).show();

                                } else {
                                    //실시간 인식에서 사용
                                }
                            }
                        });
                    }
                }
            };

    //음성인식 여부에 따라 텍스트 변환시킴
    private void showStatus(final boolean hearingVoice) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mStatus.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
            }
        });
    }

    public void startVoiceRecognition(){
        // Prepare Cloud Speech API
        activity.bindService(new Intent(activity, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

        // Start listening to voices
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecorder();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    public Thread stopVoiceRecognition(){
        // Prepare Cloud Speech API
        Thread thread =stopVoiceRecorder();        // 이 코드로 인해 앱 지연

        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        activity.unbindService(mServiceConnection);
        mSpeechService = null;

        return thread;

    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private Thread stopVoiceRecorder() {
        Thread thread = mVoiceRecorder.getThread();
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }

        return thread;
    }

//    private void showPermissionMessageDialog() {
//        MessageDialogFragment
//                .newInstance("안녕하세요.")
//                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
//    }

    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }
}
