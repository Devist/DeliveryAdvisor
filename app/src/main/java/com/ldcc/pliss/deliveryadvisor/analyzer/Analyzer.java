package com.ldcc.pliss.deliveryadvisor.analyzer;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.ldcc.pliss.deliveryadvisor.advisor.ProcessorNLP;
import com.ldcc.pliss.deliveryadvisor.databases.AppLogsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by pliss on 2018. 4. 6..
 */

public class Analyzer {

    /** 임시. 배송 처리 디테일 장소 키워드 셋. */
    private static final String[] processDetailSecurityOffice= {"경비","경비실"};
    private static final String[] processDetailSelf= {"본인","자기"};
    private static final String[] processDetailAcquaintance = {"가족","지인","어머니","아버지","친척","동생"};
    private static final String[] processDetailDoor = {"문","앞","집","문 앞","집 앞"};
    private static final String[] processDetailUnmannedCourier = {"무인택배함","무인","택배"};
    private static final String[] processDetailCancle = {"취소","다음","미배송"};


    public static final int POPUP_HELLO_MODE                    = 0;
    public static final int POPUP_INVOICE_NUMBER_MODE          = 1;
    public static final int POPUP_PROCESS                       = 2;
    private static List<String> invoiceKeywords;

    private static AppLogsHelper logsHelper;

    public Analyzer(Context context){
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
        invoiceKeywords = new ArrayList<>();
        String keywords = NLP(voice);
        List<String> tokens = getTokens(keywords);

        int result = POPUP_HELLO_MODE;

        switch(mode){
            case POPUP_HELLO_MODE:
                result = analyzeAllSituation(tokens);
                break;
            case POPUP_PROCESS:
                result = analyzeHowToSHIP(tokens);
            default:
                break;
        }

        return result;

    }

    public List<String> getInvoiceKeywords(){
        return invoiceKeywords;
    }

    private static int analyzeAllSituation(List<String> tokens){

        int finalAction;
        Params params = new Params(tokens);

        Log.d("처리","길안내 : "+ params.isNavigate);
        Log.d("처리","다음배송지 : "+ params.isNextCustomer);
        Log.d("처리","송장개수 : "+ invoiceKeywords.size());

        finalAction = classifyCancle(tokens);
        if(finalAction>0)
            return finalAction;

        finalAction = classifyHelp(tokens);
        if(finalAction>0)
            return finalAction;

        finalAction = classifyWithCall(params);
        if(finalAction>0)
            return finalAction;

        finalAction = classifyWithDelivery(params,tokens);
        if(finalAction>0)
            return finalAction;

        finalAction = classifyWithPersonDirect(params);
        if(finalAction>0)
            return finalAction;

        finalAction = classifyWithPersonIndirect(params);
        if(finalAction>0)
            return finalAction;

        finalAction = classifyWithDeliveryPlace(params);
        if(finalAction>0)
            return finalAction;

        finalAction = classifyNavigatePlace(params);
        if(finalAction>0)
            return finalAction;

        return -1;
    }

    private static int classifyCancle(List<String> tokens){
        for(String keyword : ParamSet.finishArray) {
            if(tokens.contains(keyword))
                return FinalAction.CANCLE;
        }
        return -1;
    }

    private static int classifyHelp(List<String> tokens){
        for(String keyword : ParamSet.helpArray) {
            if(tokens.contains(keyword))
                return FinalAction.HOW_TO_USE;
        }
        return -1;
    }

    private static int classifyWithCall(Params params){
        if(!params.isCall)
            return -1;
        if(params.isNextCustomer && invoiceKeywords.size()>0)
            return FinalAction.CALL_E_INVOICE_OR_NEXT;
        if(params.isNextCustomer)
            return FinalAction.CALL_NEXT_CUSTOMER;
        if(invoiceKeywords.size()==1)
            return FinalAction.CALL_INVOICE_CUSTOMER;
        if(invoiceKeywords.size()>1)
            return FinalAction.CALL_E_MANY_INVOICES;

        return FinalAction.CALL_CURRENT_CUSTOMER;
    }

    private static int classifyWithDelivery(Params params, List<String> tokens) {

        for(String keyword : ParamSet.deliveryKeywords) {
            if(tokens.contains(keyword)){
                for(String comb : ParamSet.deliveryCancleKeywords){
                    if(tokens.contains(comb)){
                        if(invoiceKeywords.size()<1 && params.isNextCustomer)
                            return FinalAction.CANCLE_NEXT;
                        if(invoiceKeywords.size()<1)
                            return FinalAction.CANCLE_CURRENT;
                        if(invoiceKeywords.size()>0){
                            if(params.isNextCustomer)
                                return FinalAction.CANCLE_E_INVOICE_OR_NEXT;
                            if(invoiceKeywords.size()==1)
                                return FinalAction.CANCLE_INVOICE;
                            if(invoiceKeywords.size()>1)
                                return FinalAction.CANCLE_E_MANY_INVOICES;
                        }
                        return FinalAction.CANCLE_CURRENT;
                    }
                }
            }
        }
        Log.d("처리","여기까지??");
        if(params.isCompleteDelivery==null)
            return -1;

        if(params.isCompleteDelivery.equals("배송완료")){
            if(invoiceKeywords.size()<1 && params.isNextCustomer)
                return FinalAction.DONE_SIMPLE_NEXT;
            if(invoiceKeywords.size()<1)
                return FinalAction.DONE_SIMPLE_CURRENNT;
            if(invoiceKeywords.size()>0){
                if(params.isNextCustomer)
                    return FinalAction.DONE_SIMPLE_E_INVOICE_OR_NEXT;
                if(invoiceKeywords.size()==1)
                    return FinalAction.DONE_SIMPLE_INVOICE;
                if(invoiceKeywords.size()>1)
                    return FinalAction.DONE_SIMPLE_E_MANY_INVOICES;
            }
            return FinalAction.DONE_SIMPLE_CURRENNT;
        }else if(params.isCompleteDelivery.equals("배송미완료")){
            Log.d("처리","취소 키워드");
            if(invoiceKeywords.size()<1 && params.isNextCustomer)
                return FinalAction.CANCLE_NEXT;
            if(invoiceKeywords.size()<1)
                return FinalAction.CANCLE_CURRENT;
            if(invoiceKeywords.size()>0){
                if(params.isNextCustomer)
                    return FinalAction.CANCLE_E_INVOICE_OR_NEXT;
                if(invoiceKeywords.size()==1)
                    return FinalAction.CANCLE_INVOICE;
                if(invoiceKeywords.size()>1)
                    return FinalAction.CANCLE_E_MANY_INVOICES;
            }
            return FinalAction.CANCLE_CURRENT;
        }

        return -1;
    }

    private static int classifyWithPersonDirect(Params params){
        if(params.isDirectReceipt){
            if(invoiceKeywords.size()>0 && params.isCurrentCustomer)
                return FinalAction.DONE_SELF_E_INVOICE_OR_CURRENT;
            if(invoiceKeywords.size()>0 && params.isNextCustomer)
                return FinalAction.DONE_SELF_E_INVOICE_OR_NEXT;
            if(invoiceKeywords.size()==1)
                return FinalAction.DONE_SELF_INVOICE;
            if(invoiceKeywords.size()>1)
                return FinalAction.DONE_SELF_E_MANY_INVOICES;
            if(params.isNextCustomer)
                return FinalAction.DONE_SELF_NEXT;
            if(params.isCurrentCustomer)
                return FinalAction.DONE_SELF_CURRENT;

            return FinalAction.DONE_SELF_CURRENT;
        }
        return -1;
    }

    private static int classifyWithPersonIndirect(Params params){
        if(params.isOtherReceipt==null)
            return -1;

        if(params.isOtherReceipt.equals("대리수령자없음")) //대리 수령인데 수령 대상이 없다는 건지 확인 필요
            return FinalAction.DONE_OTHER_CURRENT;

        //if(params.isOtherReceipt.equals("대리수령")){   // 없음. 확인 필요
        Log.d("처리", params.isOtherReceipt);
        //}
        if(params.isOtherReceipt.equals("대리수령:가족")){
            if(invoiceKeywords.size()>0 && params.isCurrentCustomer)
                return FinalAction.DONE_OTHER_E_FAMILY_INVOICE_OR_CURRENT;
            if(invoiceKeywords.size()>0 && params.isNextCustomer)
                return FinalAction.DONE_OTHER_E_FAMILY_INVOICE_OR_NEXT;
            if(invoiceKeywords.size()==1)
                return FinalAction.DONE_OTHER_INVOICE_FAMILY;
            if(invoiceKeywords.size()>1)
                return FinalAction.DONE_OTHER_E_FAMILY_MANY_INVOICES;
            if(params.isNextCustomer)
                return FinalAction.DONE_OTHER_NEXT_FAMILY;

            return FinalAction.DONE_OTHER_CURRENT_FAMILY;
        }

        if(params.isOtherReceipt.equals("대리수령:동료")){
            if(invoiceKeywords.size()>0 && params.isCurrentCustomer)
                return FinalAction.DONE_OTHER_E_COMPANY_INVOICE_OR_CURRENT;
            if(invoiceKeywords.size()>0 && params.isNextCustomer)
                return FinalAction.DONE_OTHER_E_COMPANY_INVOICE_OR_NEXT;
            if(invoiceKeywords.size()==1)
                return FinalAction.DONE_OTHER_INVOICE_COMPANY;
            if(invoiceKeywords.size()>1)
                return FinalAction.DONE_OTHER_E_COMPANY_MANY_INVOICES;
            if(params.isNextCustomer)
                return FinalAction.DONE_OTHER_NEXT_COMPANY;

            return FinalAction.DONE_OTHER_CURRENT_COMPANY;
        }

        if(params.isOtherReceipt.equals("대리수령:친구/동거인")){
            if(invoiceKeywords.size()>0 && params.isCurrentCustomer)
                return FinalAction.DONE_OTHER_E_FRIEND_INVOICE_OR_CURRENT;
            if(invoiceKeywords.size()>0 && params.isNextCustomer)
                return FinalAction.DONE_OTHER_E_FRIEND_INVOICE_OR_NEXT;
            if(invoiceKeywords.size()==1)
                return FinalAction.DONE_OTHER_INVOICE_FRIEND;
            if(invoiceKeywords.size()>1)
                return FinalAction.DONE_OTHER_E_FRIEND_MANY_INVOICES;
            if(params.isNextCustomer)
                return FinalAction.DONE_OTHER_NEXT_FRIEND;

            return FinalAction.DONE_OTHER_CURRENT_FRIEND;
        }

        return -1;
    }

    private static int classifyWithDeliveryPlace(Params params){
        if(params.isPlaceReceipt==null)
            return -1;

        if(params.isPlaceReceipt.equals("택배보관:문")){
            if(invoiceKeywords.size()>0 && params.isCurrentCustomer)
                return FinalAction.DONE_KEEP_E_DOOR_INVOICE_OR_CURRENT;
            if(invoiceKeywords.size()>0 && params.isNextCustomer)
                return FinalAction.DONE_KEEP_E_DOOR_INVOICE_OR_NEXT;
            if(invoiceKeywords.size()==1)
                return FinalAction.DONE_KEEP_INVOICE_DOOR;
            if(invoiceKeywords.size()>1)
                return FinalAction.DONE_KEEP_E_DOOR_MANY_INVOICES;
            if(params.isNextCustomer)
                return FinalAction.DONE_KEEP_NEXT_DOOR;

            return FinalAction.DONE_KEEP_CURRENT_DOOR;
        }

        if(params.isPlaceReceipt.equals("택배보관:경비실")){
            if(invoiceKeywords.size()>0 && params.isCurrentCustomer)
                return FinalAction.DONE_KEEP_E_SECURITY_INVOICE_OR_CURRENT;
            if(invoiceKeywords.size()>0 && params.isNextCustomer)
                return FinalAction.DONE_KEEP_E_SECURITY_INVOICE_OR_NEXT;
            if(invoiceKeywords.size()==1)
                return FinalAction.DONE_KEEP_INVOICE_SECURITY;
            if(invoiceKeywords.size()>1)
                return FinalAction.DONE_KEEP_E_SECURITY_MANY_INVOICES;
            if(params.isNextCustomer)
                return FinalAction.DONE_KEEP_NEXT_SECURITY;

            return FinalAction.DONE_KEEP_CURRENT_SECURITY;
        }

        if(params.isPlaceReceipt.equals("택배보관:보관실")){
            if(invoiceKeywords.size()>0 && params.isCurrentCustomer)
                return FinalAction.DONE_KEEP_E_STORAGE_INVOICE_OR_CURRENT;
            if(invoiceKeywords.size()>0 && params.isNextCustomer)
                return FinalAction.DONE_KEEP_E_STORAGE_INVOICE_OR_NEXT;
            if(invoiceKeywords.size()==1)
                return FinalAction.DONE_KEEP_INVOICE_STORAGE;
            if(invoiceKeywords.size()>1)
                return FinalAction.DONE_KEEP_E_STORAGE_MANY_INVOICES;
            if(params.isNextCustomer)
                return FinalAction.DONE_KEEP_NEXT_STORAGE;

            return FinalAction.DONE_KEEP_CURRENT_STORAGE;
        }

        return -1;
    }

    private static int classifyNavigatePlace(Params params){

        if(params.isNavigate){
            if(params.isNextCustomer && invoiceKeywords.size()>0)
                return FinalAction.NAVI_E_INVOICE_OR_NEXT;
            if(params.isNextCustomer)
                return FinalAction.NAVI_NEXT;
            if(invoiceKeywords.size()==1)
                return FinalAction.NAVI_INVOICE;
            if(invoiceKeywords.size()>1)
                return FinalAction.NAVI_E_MANY_INVOICES;

            return FinalAction.NAVI_CURRENT;
        }

        return -1;
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
        voice = joinIndividualNumbers(voice);
        try{
            result = processorNLP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,voice).get();
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String joinIndividualNumbers(String voice){
        StringBuffer buffer = new StringBuffer(voice);
        for(int i = 0; i<buffer.length()-1 ; i++){
            char compareChar = buffer.charAt(i);
            char spaceChar = buffer.charAt(i+1);
            if (compareChar==('0')||compareChar==('1')||compareChar==('2')||compareChar==('3')
                    ||compareChar==('4')||compareChar==('5')||compareChar==('6')
                    ||compareChar==('7')||compareChar==('8')||compareChar==('9')){
                if(spaceChar==' '){
                    buffer.deleteCharAt(i+1);
                }
            }
        }
        String result = buffer.toString();
        Log.d("테스트",result);
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
                    invoiceKeywords.add(numbers[i]);
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


    private static int analyzeHowToSHIP(List<String> keywordsArray){

        for (String securityKeywords : processDetailSecurityOffice){
            if(keywordsArray.contains(securityKeywords))
                return FinalAction.DONE_KEEP_CURRENT_SECURITY;
        }
        for (String acquaintanceKeywords : processDetailAcquaintance){
            if(keywordsArray.contains(acquaintanceKeywords))
                return FinalAction.DONE_OTHER_CURRENT_FRIEND;
        }
        for (String doorKeywords : processDetailDoor){
            if(keywordsArray.contains(doorKeywords))
                return FinalAction.DONE_KEEP_CURRENT_DOOR;
        }
        for (String selfKeywords : processDetailSelf){
            if(keywordsArray.contains(selfKeywords))
                return FinalAction.DONE_SELF_CURRENT;
        }
        for (String unmannedKeywords : processDetailUnmannedCourier){
            if(keywordsArray.contains(unmannedKeywords))
                return FinalAction.DONE_KEEP_CURRENT_STORAGE;
        }
        for (String cancleKeywords : processDetailCancle){
            if(keywordsArray.contains(cancleKeywords))
                return FinalAction.CANCLE_CURRENT;
        }

        return FinalAction.DONE_SIMPLE_CURRENNT;
    }
}
