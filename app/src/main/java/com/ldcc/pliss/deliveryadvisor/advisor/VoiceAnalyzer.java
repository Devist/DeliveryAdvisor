package com.ldcc.pliss.deliveryadvisor.advisor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ldcc.pliss.deliveryadvisor.databases.AppLogsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;



public class VoiceAnalyzer {

    public static final int POPUP_HELLO_MODE                    = 0;
    public static final int POPUP_INVOICE_NUMBER_MODE          = 1;
    public static final int POPUP_PROCESS                       = 2;

    public static final int CALL_THE_CURRENT_CUSTOMER          = 1000;
    public static final int GUIDE_THE_CURRENT_CUSTOMER         = 1001;

    public static final int DELIVERY_THE_CURRENT_CUSTOMER_DEFAULT              = 1002;
    public static final int DELIVERY_THE_CURRENT_CUSTOMER_SECURITY_OFFICE      = 1003;
    public static final int DELIVERY_THE_CURRENT_CUSTOMER_ACQUAINTANCE         = 1004;
    public static final int DELIVERY_THE_CURRENT_CUSTOMER_DOOR                 = 1005;
    public static final int DELIVERY_THE_CURRENT_CUSTOMER_UNMANNED             = 1006;
    public static final int DELIVERY_THE_CURRENT_CUSTOMER_CANCLE               = 1007;
    public static final int DELIVERY_THE_CURRENT_CUSTOMER_SELF                 = 1008;


    public static final int HOW_TO_USE                                         = 8888;
    public static final int EXIT_ADVISOR                                       = 9999;


    private static AppLogsHelper logsHelper;

    /** 전화 키워드 셋. */
    private static final String[] callArray= {"전화","콜","call","전화하다"};

    /** 배송 처리 키워드 셋. */
    private static final String[] processArray= {"배송","처리","배송처리","배송 처리"};

    /** 배송 처리 디테일 장소 키워드 셋. */
    private static final String[] processDetailSecurityOffice= {"경비","경비실"};
    private static final String[] processDetailSelf= {"본인","자기"};
    private static final String[] processDetailAcquaintance = {"가족","지인","어머니","아버지","친척","동생"};
    private static final String[] processDetailDoor = {"문","앞","집","문 앞","집 앞"};
    private static final String[] processDetailUnmannedCourier = {"무인택배함","무인","택배"};
    private static final String[] processDetailCancle = {"취소","다음","미배송"};

    /** 길 안내 키워드 셋 */
    private static final String[] naviArray= {"안내하다","안내","길","경로"};

    /** 어드바이저 취소 */
    private static final String[] finishArray={"굿바이","종료","괜찮아","아냐","아니야","아니다","괜찮다"};

    /** 사용 방법 질의 */
    private static final String[] helpArray={"사용방법","사용","방법","헬프"};

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
        String keywords = NLP(voice);
//        joinIndividualNumbers();
        List<String> keywordArray = getTokens(keywords);

        int result = POPUP_HELLO_MODE;

        switch(mode){
            case POPUP_HELLO_MODE:
                result = analyzeAll(keywordArray);
                break;
            case POPUP_PROCESS:
                result = analyzeHowToSHIP(keywordArray);
            default:
                break;
        }

        return result;

    }

//    private static void joinIndividualNumbers(){
//        String myTest = "송장번호 3 2 9 8 0 배송처 리 4 7 89";
//        StringBuffer buffer = new StringBuffer(myTest);
//        for(int i = 0; i<buffer.length()-1 ; i++){
//            char compareChar = buffer.charAt(i);
//            char spaceChar = buffer.charAt(i+1);
//            if (compareChar==('0')||compareChar==('1')||compareChar==('2')||compareChar==('3')
//                    ||compareChar==('4')||compareChar==('5')||compareChar==('6')
//                    ||compareChar==('7')||compareChar==('8')||compareChar==('9')){
//                if(spaceChar==' '){
//                    buffer.deleteCharAt(i+1);
//                }
//            }
//        }
//        myTest = buffer.toString();
//        Log.d("테스트",myTest);
//    }

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
        String[] verbs = null;
        String[] verbPrefixs = null;
        String[] numbers = null;
        List<String> tokens = new ArrayList<>();

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

            verbs = new String[jsonArrayTokens.length()];
            str+="\n\n Step4.텍스트 -> 키워드(Verb): \n"; //로그 기록하기 위한 Source Code
            for(int i = 0 ;i <jsonArrayTokens.length() ; i++){
                if(jsonArrayTokens.getString(i).contains("Verb")) {
                    verbs[i] = jsonArrayTokens.getString(i).split("\\)")[0];
                    verbs[i] = verbs[i].split("\\(")[2];
                    str+="["+verbs[i]+"]"; //로그 기록하기 위한 Source Code
                    tokens.add(verbs[i]);
                }
            }

            verbPrefixs = new String[jsonArrayTokens.length()];
            str+="\n\n Step5.텍스트 -> 키워드(VerbPrefix): \n"; //로그 기록하기 위한 Source Code
            for(int i = 0 ;i <jsonArrayTokens.length() ; i++){
                Log.d("어레이스트링",jsonArrayTokens.getString(i));
                if(jsonArrayTokens.getString(i).contains("VerbPrefix")) {
                    verbPrefixs[i] = jsonArrayTokens.getString(i).split("\\)")[0];
                    verbPrefixs[i] = verbPrefixs[i].split("\\(")[2];
                    str+="["+verbPrefixs[i]+"]"; //로그 기록하기 위한 Source Code
                    tokens.add(verbPrefixs[i]);
                }
            }

            numbers = new String[jsonArrayTokens.length()];
            str+="\n\n Step6.텍스트 -> 키워드(Number): \n"; //로그 기록하기 위한 Source Code
            for(int i = 0 ;i <jsonArrayTokens.length() ; i++){
                if(jsonArrayTokens.getString(i).contains("Number")) {
                    numbers[i] = jsonArrayTokens.getString(i).split("\\)")[0];
                    numbers[i] = numbers[i].split("\\(")[0];
                    str+="["+numbers[i]+"]"; //로그 기록하기 위한 Source Code
                    tokens.add(verbPrefixs[i]);
                }
            }

            logsHelper.addAppLogs(str);

        }catch(Exception e){
            e.printStackTrace();
        }
        return tokens;
    }

    private static int analyzeAll(List<String> keywordsArray){

        for (String keyword : callArray) {
            if(keywordsArray.contains(keyword))
                return CALL_THE_CURRENT_CUSTOMER;
        }

        for (String keyword : naviArray) {
            if(keywordsArray.contains(keyword))
                return GUIDE_THE_CURRENT_CUSTOMER;
        }

        for (String keyword : finishArray) {
            if(keywordsArray.contains(keyword))
                return EXIT_ADVISOR;
        }

        for (String keyword : helpArray) {
            if(keywordsArray.contains(keyword))
                return HOW_TO_USE;
        }

        for (String processing : processArray){

            if(keywordsArray.contains(processing)){
                return analyzeHowToSHIP(keywordsArray);
            }
        }


      return -1;
    }

    private static int analyzeHowToSHIP(List<String> keywordsArray){

        for (String securityKeywords : processDetailSecurityOffice){
            if(keywordsArray.contains(securityKeywords))
                return DELIVERY_THE_CURRENT_CUSTOMER_SECURITY_OFFICE;
        }
        for (String acquaintanceKeywords : processDetailAcquaintance){
            if(keywordsArray.contains(acquaintanceKeywords))
                return DELIVERY_THE_CURRENT_CUSTOMER_ACQUAINTANCE;
        }
        for (String doorKeywords : processDetailDoor){
            if(keywordsArray.contains(doorKeywords))
                return DELIVERY_THE_CURRENT_CUSTOMER_DOOR;
        }
        for (String selfKeywords : processDetailSelf){
            if(keywordsArray.contains(selfKeywords))
                return DELIVERY_THE_CURRENT_CUSTOMER_SELF;
        }
        for (String unmannedKeywords : processDetailUnmannedCourier){
            if(keywordsArray.contains(unmannedKeywords))
                return DELIVERY_THE_CURRENT_CUSTOMER_UNMANNED;
        }
        for (String cancleKeywords : processDetailCancle){
            if(keywordsArray.contains(cancleKeywords))
                return DELIVERY_THE_CURRENT_CUSTOMER_CANCLE;
        }

        return DELIVERY_THE_CURRENT_CUSTOMER_DEFAULT;
    }
}
