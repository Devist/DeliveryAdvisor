package com.ldcc.pliss.deliveryadvisor.advisor.google;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.ldcc.pliss.deliveryadvisor.advisor.VoiceRecorder;
import com.ldcc.pliss.deliveryadvisor.analyzer.Analyzer;
import com.ldcc.pliss.deliveryadvisor.databases.AppLogsHelper;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.ldcc.pliss.deliveryadvisor.MainActivity.fa;

public class SpeechHelper {

    AppLogsHelper appLogsHelper;

    // 음성 문장 분석을 통한 취해야 할 액션을 얻기 위해, AdvisorDialog에서 구현됩니다.
    public interface Listener {
        void onVoiceAnalyed(int analyzeResult, List<String> invoiceKeywords);
    }

    Listener mListener;
    public void addListener(@NonNull SpeechHelper.Listener listener) {
        mListener = listener;
    }

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechService mSpeechService;
    private Activity activity;
    private static VoiceRecorder mVoiceRecorder;

    public SpeechHelper(Activity activity){
        this.activity=activity;
        appLogsHelper = new AppLogsHelper(activity);
    }

    private int analyzerMode = Analyzer.POPUP_HELLO_MODE ;  // 1: 전체 분석, 2: 배송 처리 분석


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

    public void setAnalyzerMode(int mode){
        mSpeechService.setVoiceAnalyzerMode(mode);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            Log.d("처리커넥션",analyzerMode+"");
            mSpeechService.setVoiceAnalyzerMode(analyzerMode);
            mSpeechService.addListener(mSpeechServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    /**
     * SpeechService 의 콜백
     */
    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal, final int analyzeResult, final List<String> invoiceKeywords) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (!TextUtils.isEmpty(text)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    Toast.makeText(activity,"인식된 문서는 : "+text,Toast.LENGTH_SHORT).show();
                                    mListener.onVoiceAnalyed(analyzeResult, invoiceKeywords);
                                    appLogsHelper.addAppLogs("처리번호 : " + analyzeResult);
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

    public void startVoiceRecognition(int mode){
        analyzerMode = mode;
        // Cloud Speech API 준비.
        activity.bindService(new Intent(activity, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

        // 목소리 듣기 시작.
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

    public void stopVoiceRecognition(){
        // Prepare Cloud Speech API
        stopVoiceRecorder();        // 이 코드로 인해 앱 지연

        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        activity.unbindService(mServiceConnection);
        mSpeechService = null;
        //return thread;

    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
//        Thread thread = mVoiceRecorder.getThread();
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
//        return thread;
    }
}
