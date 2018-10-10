package com.ldcc.pliss.deliveryadvisor.advisor.naver;

// 네이버 음성합성 Open API 활용한 TTS.
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import static android.speech.tts.TextToSpeech.ERROR;


public class ClovaTTS {

    private static final String[]   TTS_GOODBYE   = {"감사합니다.","See you next time!","알겠습니다.","조금 있다 뵙겠습니다."};
    private static final String[]   TTS_HELLO     = {"무엇을 도와드릴까요?","안녕하세요?","무엇을 도와드릴까요?","무엇이든 시켜만 주세요!","Delivery Advisor 입니다."};
    private static final String     TTS_HOW_TO_USE  = "저는 배송 처리, 전화 연결, 현재 목적지 확인을 할 수 있어요. 배송해 줘, 전화 연결 부탁해, 등으로 저에게 명령하세요. 이후 대화는 제가 리드 할께요!";
    private static final String     TTS_CALL        = "전화를 연결하겠습니다.";
    private static final String     TTS_NAVIGATION  = "길 안내를 시작하겠습니다.";

    //private static final String clientId = "kblV6jh_v77ThvCjC256";    //애플리케이션 클라이언트 아이디값";
    //private static final String clientSecret = "_NaRGVFHkO";          //애플리케이션 클라이언트 시크릿값";

    private static File fileDir;
    private File myFile;

    private MediaPlayer mediaPlayer;
    private int helloMsg;
    private int byeMsg;

    private TextToSpeech tts;
    public Context context;

    public interface Listener {


        void onSpeakingFinished(boolean isFinal);

    }

    private Listener mListener;

    public void addListener(Listener mListener) {
        this.mListener  = mListener;
    }

    public ClovaTTS(final Context context){
        this.context = context;
        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                Log.d("test","발음 종료");
                mListener.onSpeakingFinished(true);
//                new Handler().postDelayed(new Runnable(){
//                    @Override
//                    public void run(){
//                        mListener.onSpeakingFinished(true);
//                    }
//                },1000);

            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        helloMsg  = new Random().nextInt(TTS_HELLO.length);
        byeMsg = new Random().nextInt(TTS_GOODBYE.length);
    }

    /**
     * parameter로 들어온 문장을 재생합니다.
     *
     * @param path      문장 오디오의 파일명. 로컬에 저장하여, 두 번 재생시 낭비되는 자원이 없도록 합니다.
     * @param sentence  재생할 문장. 해당 문장이 재생됩니다.
     */
    public void sayThis(final String path, String sentence){
        final String sentences = sentence;

//        final Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//            }
//        };

        new Thread() {
            public void run() {
                tryNormalTTS(path, sentences);
//                Bundle bun = new Bundle();
//                Message msg = handler.obtainMessage();
//                msg.setData(bun);
//                handler.sendMessage(msg);
            }
        }.start();
    }

    public void sayNoVoice() {
        final String path = "tts_no_voice";

//        final Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//            }
//        };

        new Thread() {
            public void run() {
                tryNormalTTS(path, "필요할 때 불러주세요.");
//                Bundle bun = new Bundle();
//                Message msg = handler.obtainMessage();
//                msg.setData(bun);
//                handler.sendMessage(msg);
            }
        }.start();
    }


    /**
     * 사용자가 어드바이저를 취소했을 때, 종료 문장을 재생합니다.
     */
    public void sayGoodBye(){

        final String path = "tts_exit" + byeMsg;

//        final Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//            }
//        };

        new Thread() {
            public void run() {
                tryNormalTTS(path, TTS_GOODBYE[byeMsg]);
//                Bundle bun = new Bundle();
//                Message msg = handler.obtainMessage();
//                msg.setData(bun);
//                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 어드바이저를 최초 실행했을 때 사용하는, 인사 문장을 재생합니다.
     *
     * @return 화면에 표시할 수 있는, 인사 문장을 반환합니다.
     */
    public void sayHello(){
        final String path = "tts_welcome" + helloMsg;


        new Thread() {
            public void run() {
                tryNormalTTS(path, TTS_HELLO[helloMsg]);
            }
        }.start();

    }

    public String getHelloMsg(){return TTS_HELLO[helloMsg];}

    /**
     * 사용 방법을 알려주는 도움말 문장을 재생합니다.
     */
    public void sayHelp(){
        final String path = "tts_help";

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
            }
        };

        new Thread() {
            public void run() {
                tryNormalTTS(path, TTS_HOW_TO_USE);
                Bundle bun = new Bundle();
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();

    }

    private void tryNormalTTS(String path, String sentences){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            tts.speak(str,TextToSpeech.QUEUE_FLUSH,null,"talking");
//        else
//            tts.speak(str,TextToSpeech.QUEUE_FLUSH,null);
        mediaPlayer = new MediaPlayer();
        myFile = new File(context.getFilesDir().getPath().toString()+"/"+path+".mp3");
        try {
            Log.d("테스트: 경로 확인 : ",context.getFilesDir().getPath().toString()+"/"+path+".mp3");
            if(!myFile.exists()){
                myFile.createNewFile();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    tts.synthesizeToFile(sentences, null, myFile,null);
                else{
                    HashMap<String, String> myHashRender = new HashMap();
                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, sentences);
                    tts.synthesizeToFile(sentences, myHashRender, myFile.getAbsolutePath());
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable(){
            @Override
            public void run(){
                try {
                    final FileInputStream MyFile = new FileInputStream(myFile);
                    mediaPlayer.setDataSource(MyFile.getFD());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mListener.onSpeakingFinished(true);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },1500);



    }


    private void tryClovaTTS(String path,String sentences){
        mediaPlayer = new MediaPlayer();
        try {
            myFile = new File(fileDir,path + ".mp3");
            if(!myFile.exists()){
                Log.d("테스트","파일이 존재하지 않음");
                String text = URLEncoder.encode(sentences, "UTF-8"); // 13자
                String apiURL = "https://openapi.naver.com/v1/voice/tts.bin";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                //con.setRequestProperty("X-Naver-Client-Id", clientId);
                //con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "speaker=mijin&speed=0&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;

                if(responseCode==200){
                    InputStream is = con.getInputStream();
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    if(!myFile.exists()){
                        myFile.createNewFile();
                        OutputStream outputStream = new FileOutputStream(myFile);
                        while ((read =is.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                        is.close();
                    }
                }else{
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    Log.d("TTS 에러 : ", response.toString());
                }
            }

            FileInputStream MyFile = new FileInputStream(myFile);
            mediaPlayer.setDataSource(MyFile.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
//                    mListener.onSpeakingFinished(true);
                }
            });


        } catch (Exception e) {
            Log.d("에러 : ", e.toString());
        }
    }

    // Clova 는 12345 의 경우 일만이천삼백사십오..라고 읽기 때문에, "1 2 3 4 5"와 같이 변환이 필요
    public String convertNumberToClova(String numbers){
        if(numbers.length()<5){
            String results = "";
            for(int i =0 ; i<4 ;i++){
                results += (numbers.charAt(i)+" ");
            }
            return results;
        }else
            return numbers;
    }

    public void stopClovaTTS(){
        tts.stop();
//        if(mediaPlayer!=null) {
//            if(mediaPlayer.isPlaying())
//                mediaPlayer.stop();
//            mediaPlayer.reset();
//            mediaPlayer.release();
//            mediaPlayer=null;
//        }
    }
}