package com.ldcc.pliss.deliveryadvisor.advisor;

import android.content.Context;
import android.os.AsyncTask;

import com.ldcc.pliss.deliveryadvisor.databases.AppLogsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;



public class VoiceAnalyzer {

    public static final int POPUP_HELLO_MODE                    = 0;
    public static final int POPUP_INVOICE_NUMBER_MODE          = 1;

    public static final int CALL_THE_CURRENT_CUSTOMER          = 1000;
    public static final int GUIDE_THE_CURRENT_CUSTOMER         = 1001;

    public static final int DELIVERY_THE_CURRENT_CUSTOMER_DEFAULT              = 1002;
    public static final int DELIVERY_THE_CURRENT_CUSTOMER_SECURITY_OFFICE      = 1003;


    private static AppLogsHelper logsHelper;

    /** 전화 키워드 셋. */
    private static final String[] callArray= {"전화","콜","call"};

    /** 배송 처리 키워드 셋. */
    private static final String[] processArray= {"배송","처리","배송처리","배송 처리"};

    /** 배송 처리 디테일 장소 키워드 셋. */
    private static final String[] processDetailSecurityOffice= {"경비","경비실"};


    public VoiceAnalyzer(Context context){
        logsHelper = new AppLogsHelper(context);
    }

    /**
     * String으로 변환된 음성 문장을 키워드 분리(function NLP) 한 후, 키워드를 분석하여 애플리케이션이 수행해야 할 업무를 도출해냅니다.
     *
     * @param mode     mode에 따라서, [문장 완전 분석, 송장번호 분석] 과 같은 분석을 통해 애플리케이션이 수행해야 할 업무를 추출합니다.
     * @param voice    String으로 변환된 음성 문장
     * @return
     */
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

    /**
     * 자연 언어 처리(NLP, Natural Language Processing)을 통해 문장에서 명사, 형용사, 조사 등 키워드를 json string 형태로 추출합니다.
     *
     * @param voice String으로 변환된 음성 문장
     * @return
     */
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

    /**
     * 토큰이 저장되어 있는 파라미터 json string에서, noun과 adj 를 추출해 냅니다.
     *
     * @param kewords
     * @return
     */
    private static List<String> getTokens(String kewords){
        String[] nouns = null;
        String[] adjs = null;
        List<String> tokens = new ArrayList<String>();

        try{
            JSONObject flattenJson = new JSONObject(kewords);
            JSONArray jsonArrayTokens = flattenJson.getJSONArray("tokens");
            nouns = new String[jsonArrayTokens.length()];
            String str="Step2.텍스트 -> 키워드(Noun): \n"; //로그 기록하기 위한 Source Code
            for(int i = 0 ;i <jsonArrayTokens.length() ; i++){
                if(jsonArrayTokens.getString(i).contains("Noun")) {
                    nouns[i] = jsonArrayTokens.getString(i).split("\\(")[0];      //로그 기록하기 위한 Source Code
                    str+="["+nouns[i]+"]"; //로그 기록하기 위한 Source Code
                    tokens.add(nouns[i]);
                }
            }

            adjs = new String[jsonArrayTokens.length()];
            str+="\n\n Step3.텍스트 -> 키워드(Adj): \n"; //로그 기록하기 위한 Source Code
            for(int i = 0 ;i <jsonArrayTokens.length() ; i++){
                if(jsonArrayTokens.getString(i).contains("Adjective")) {
                    adjs[i] = jsonArrayTokens.getString(i).split("\\)")[0];
                    adjs[i] = adjs[i].split("\\(")[2];
                    str+="["+adjs[i]+"]"; //로그 기록하기 위한 Source Code
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

        for (String call : callArray) {
            if(keywordsArray.contains(call))
                return CALL_THE_CURRENT_CUSTOMER;
        }

        for (String processing : processArray){

            if(keywordsArray.contains(processing)){
                for (String securityKeywords : processDetailSecurityOffice){
                    if(keywordsArray.contains(securityKeywords))
                        return DELIVERY_THE_CURRENT_CUSTOMER_SECURITY_OFFICE;
                }
                return DELIVERY_THE_CURRENT_CUSTOMER_DEFAULT;
            }

        }


      return -1;
    }
}
