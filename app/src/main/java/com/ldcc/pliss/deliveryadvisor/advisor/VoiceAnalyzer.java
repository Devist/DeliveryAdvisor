package com.ldcc.pliss.deliveryadvisor.advisor;

import android.content.Context;
import android.os.AsyncTask;

import com.ldcc.pliss.deliveryadvisor.databases.AppLogsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by pliss on 2018. 3. 20..
 */

public class VoiceAnalyzer {

    public static final int POPUP_HELLO_MODE                   = 0;
    private static final int POPUP_INVOICE_NUMBER_MODE          = 1;

    private static final int CALL_THE_CURRENT_CUSTOMER          = 1000;
    private static final int GUIDE_THE_CURRENT_CUSTOMER         = 1001;
    private static final int DELIVERY_THE_CURRENT_CUSTOMER      = 1002;
    private static AppLogsHelper logsHelper;


    public VoiceAnalyzer(Context context){
        logsHelper = new AppLogsHelper(context);
    }

    public static int getAnalyzedAction(int mode, String voice){
        String kewords = NLP(voice);
        List<String> kewordArray = getTokens(kewords);

        int result = POPUP_HELLO_MODE;

        switch(mode){
            case POPUP_HELLO_MODE:
                result = analyzeAll(kewordArray);
                break;
            default:
                break;
        }

        return result;

    }

    private static String NLP(String voice){
        ProcessorNLP processorNLP = new ProcessorNLP();
        String result = "fail";
        try{
            result = processorNLP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,voice).get();
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<String> getTokens(String kewords){
        String[] nouns = null;
        String[] adjs = null;
        List<String> tokens = new ArrayList<String>();

        try{
            int tokenCounts=0;

            JSONObject flattenJson = new JSONObject(kewords);
            JSONArray jsonArrayTokens = flattenJson.getJSONArray("tokens");
            nouns = new String[jsonArrayTokens.length()];
            String str="Step2.텍스트 -> 키워드(Noun): \n";
            for(int i = 0 ;i <jsonArrayTokens.length() ; i++){
                if(jsonArrayTokens.getString(i).contains("Noun")) {
                    nouns[i] = jsonArrayTokens.getString(i).split("\\(")[0];
                    str+="["+nouns[i]+"]";
                    tokens.add(nouns[i]);
                }
            }

            adjs = new String[jsonArrayTokens.length()];
            str+="\n\n Step3.텍스트 -> 키워드(Adj): \n";
            for(int i = 0 ;i <jsonArrayTokens.length() ; i++){
                if(jsonArrayTokens.getString(i).contains("Adjective")) {
                    adjs[i] = jsonArrayTokens.getString(i).split("\\)")[0];
                    adjs[i] = adjs[i].split("\\(")[2];
                    str+="["+adjs[i]+"]";
                    tokens.add(adjs[i]);
                }
            }
            logsHelper.addAppLogs(str);

        }catch(Exception e){
            e.printStackTrace();
        }
        return tokens;
    }

    private static int analyzeAll(List<String> keywordsArray){

      if(keywordsArray.contains("전화"))
          return CALL_THE_CURRENT_CUSTOMER;

      return -1;
    }
}
