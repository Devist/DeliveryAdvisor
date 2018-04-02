package com.ldcc.pliss.deliveryadvisor.advisor.naver;

// 네이버 음성합성 Open API 예제
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ClovaTTS {

    private static String clientId = "kblV6jh_v77ThvCjC256";//애플리케이션 클라이언트 아이디값";
    private static String clientSecret = "_NaRGVFHkO";//애플리케이션 클라이언트 시크릿값";

    private static File fileDir;
    private File myFile;

    private MediaPlayer mediaPlayer;


    public ClovaTTS(File fileDir){
        this.fileDir=fileDir;
    }

    public void sayThis(final String path, String sentence){
        final String sentences = sentence;

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
            }
        };

        new Thread() {
            public void run() {
                tryClovaTTS(path, sentences);
                Bundle bun = new Bundle();
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();


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
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
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
                    Log.d("비전피킹 TTS 에러 : ", response.toString());
                }
            }

            FileInputStream MyFile = new FileInputStream(myFile);
            mediaPlayer.setDataSource(MyFile.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();


        } catch (Exception e) {
            Log.d("뭔가 에러 : ", e.toString());
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
        if(mediaPlayer!=null) {
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}