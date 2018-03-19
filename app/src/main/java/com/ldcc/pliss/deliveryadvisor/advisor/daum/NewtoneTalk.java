package com.ldcc.pliss.deliveryadvisor.advisor.daum;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechManager;

/**
 * Created by pliss on 2018. 3. 14..
 */

public class NewtoneTalk {

    private TextToSpeechManager textToSpeechManager;
    private TextToSpeechClient ttsClient;
    private static final String APIKEY = "97655c151395785cc8a880e848ee3952";

    public void init(Context context){
        textToSpeechManager.getInstance().initializeLibrary(context);
        ttsClient = new TextToSpeechClient.Builder()
                .setApiKey(APIKEY)
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_2)     // 음성합성방식
                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .build();
    }

    public TextToSpeechClient getTTSClient(){
        return ttsClient;
    }

    public void destroy(){
        textToSpeechManager.getInstance().finalizeLibrary();
    }
}
