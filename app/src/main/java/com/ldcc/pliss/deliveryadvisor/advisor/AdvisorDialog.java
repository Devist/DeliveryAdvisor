package com.ldcc.pliss.deliveryadvisor.advisor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.advisor.google.SpeechHelper;
import com.ldcc.pliss.deliveryadvisor.advisor.naver.ClovaTTS;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.page.NavigationActivity;
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

/**
 * Created by pliss on 2018. 3. 6..
 */

public class AdvisorDialog extends Activity {

    private SharedPreferences prefs;
    private LinearLayout layoutForWorkButton;
    private TextView textViewQuestion;
    private DeliveryHelper deliveryHelper;
    private ManagerHelper managerHelper;
    private SpeechHelper speechHelper;
    private ClovaTTS clovaTTS = AdvisorService.clovaTTS;

    private WorkUtil workUtil;
    AudioManager audioManager;

    private String[] currentDeliveryInfo;
    private Handler voiceHandler;

    /**
     * 블루투스 헤드셋의 버튼을 클릭할 때, 기본으로 제공되는 비프음이나 안내 음성이 있습니다.
     * Delivery Advisor 에서 제공되는 음성이 겹칠 수 있으므로,
     * 블루투스 헤드셋의 기본 음성이 재생된 후 Delivery Advisor의 음성이 재생되도록
     * 딜레이를 설정합니다.
     */
//    private static final int DELAY_MILLIS_FOR_TTS = 1000;

    /**
     * 블루투스 헤드셋의 버튼을 클릭할 때, 기본으로 제공되는 비프음이나 안내 음성이 있습니다.
     * Delivery Advisor 에서 제공되는 음성이 겹칠 수 있으므로,
     * 블루투스 헤드셋의 기본 음성이 재생된 후 Delivery Advisor의 음성이 재생되도록
     * 딜레이를 설정합니다.
     */
    private static final int DELAY_MILLIS_FOR_TTS = 1500;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("처리","onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_advisor);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("처리","onStart()");
        setLayout();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.startBluetoothSco();

        //audioManager가 블루투스 마이크 사용을 가져오는 잠깐의 시간 동안 Delay가 발생하고,
        //이에 따라 재생하는 음성이 끊길 수 있습니다. 따라서 블루투스 마이크 사용 설정하는 잠깐의 시간 후에(0.5초 정도) 음성을 재생합니다.
        voiceHandler = new Handler();
        voiceHandler.postDelayed(new Runnable(){
            @Override
            public void run(){
                String myWork = getIntent().getStringExtra("Work-keyword");

                switch (String.valueOf(myWork)){
                    case "null":
                        drawFirstQuestionButton(currentDeliveryInfo);
                        break;
                    case "initialQuestion":
                        drawFirstQuestionButton(currentDeliveryInfo);
                        break;
                    case "processDelivery":
                        drawProcessDeliveryButton(currentDeliveryInfo);
                        break;
                    case "howToProcess":
                        break;
                }
            }
        },DELAY_MILLIS_FOR_TTS);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("처리","onNewIntent()");
        setIntent(intent);
        speechHelper = new SpeechHelper(this);
        setLayout();

        voiceHandler = new Handler();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.startBluetoothSco();


        //audioManager가 블루투스 마이크 사용을 가져오는 잠깐의 시간 동안 Delay가 발생하고,
        //이에 따라 재생하는 음성이 끊길 수 있습니다. 따라서 블루투스 마이크 사용 설정하는 잠깐의 시간 후에(0.5초 정도) 음성을 재생합니다.
        voiceHandler.postDelayed(new Runnable(){
            @Override
            public void run(){
                String myWork = getIntent().getStringExtra("Work-keyword");
                Log.d("처리번호",myWork);

                switch (String.valueOf(myWork)){
                    case "null":
                        drawFirstQuestionButton(currentDeliveryInfo);
                        break;
                    case "initialQuestion":
                        drawFirstQuestionButton(currentDeliveryInfo);
                        break;
                    case "processDelivery":
                        drawProcessDeliveryButton(currentDeliveryInfo);
                        break;
                    case "howToProcess":
                        break;
                }
            }
        },DELAY_MILLIS_FOR_TTS);
    }

    private void setLayout(){

        layoutForWorkButton = (LinearLayout) findViewById(R.id.layout_for_work_button);
        textViewQuestion = (TextView) findViewById(R.id.text_advisor);
        layoutForWorkButton.removeAllViewsInLayout();

        speechHelper = new SpeechHelper(this);
        setListener();
        String myWork = getIntent().getStringExtra("Work-keyword");
        Log.d("처리번호initSetting",myWork);

        switch (String.valueOf(myWork)){
            case "null":
                break;
            case "initialQuestion":
                speechHelper.startVoiceRecognition(VoiceAnalyzer.POPUP_HELLO_MODE);
                break;
            case "processDelivery":
                speechHelper.startVoiceRecognition(VoiceAnalyzer.POPUP_PROCESS);
                break;
            case "howToProcess":
                break;
        }

        if(clovaTTS==null)
            clovaTTS = new ClovaTTS(getFilesDir());

        prefs = this.getSharedPreferences("Pref", MODE_PRIVATE);
        deliveryHelper = new DeliveryHelper(this);
        managerHelper = new ManagerHelper(this);
        workUtil = new WorkUtil();

        currentDeliveryInfo = getIntent().getStringArrayExtra("Delivery-data");
        if(currentDeliveryInfo==null)
            currentDeliveryInfo = managerHelper.getCurrentDeliveryInfoSimple();


    }

    private void setListener(){

        speechHelper.addListener(new SpeechHelper.Listener() {
            @Override
            public void onVoiceAnalyed(int analyzeResult) {
                Log.d("처리번호",analyzeResult+"");
                switch (analyzeResult){
                    case VoiceAnalyzer.CALL_THE_CURRENT_CUSTOMER:
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                workUtil.callTheCustomer(getApplicationContext(),currentDeliveryInfo[4]);
                            }
                        },2000);
                        finish();
                        break;

                    case VoiceAnalyzer.GUIDE_THE_CURRENT_CUSTOMER:
                        clovaTTS.sayThis("tts_navigation","고객의 위치를 보여드릴께요.");
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                startActivity(new Intent(AdvisorDialog.this, NavigationActivity.class));
                                finishAffinity();
                            }
                        },3000);
                        break;

                    case VoiceAnalyzer.DELIVERY_THE_CURRENT_CUSTOMER_DEFAULT:
                        workUtil.showProcessDeliveryDialog(AdvisorDialog.this,currentDeliveryInfo);
                        break;

                    case VoiceAnalyzer.DELIVERY_THE_CURRENT_CUSTOMER_SECURITY_OFFICE:
                        workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[1]+"] 상품을 경비실에 맡겨두었으니 찾아가세요.");
                        deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","O");
                        deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                        finish();
                        break;

                    case VoiceAnalyzer.EXIT_ADVISOR:
                        clovaTTS.sayGoodBye();
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                System.exit(0);
                            }
                        },3000);
                        break;

                    case VoiceAnalyzer.HOW_TO_USE:
                        clovaTTS.sayHelp();;
                        break;
                }
            }
        });

    }

    @Override
    protected void onStop() {
        Log.d("처리","onStop()");
        clovaTTS.stopClovaTTS();
        //audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.stopBluetoothSco();
        try{
            speechHelper.stopVoiceRecognition();
            speechHelper = null;
        }catch (Exception e){
            e.printStackTrace();
        }
        voiceHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }


    private void drawFirstQuestionButton(final String[] currentDeliveryInfo){
        textViewQuestion.setText(clovaTTS.sayHello());

        Button buttonHelp = setButtonLayout("예제) 사용 방법 알려줘.");
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clovaTTS.sayHelp();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
            }
        });

        Button buttonKeepSelf = setButtonLayout("예제) 고객에게 전화 연결해줘.");
        buttonKeepSelf.setOnClickListener(new View.OnClickListener() {  //S:본인  F: 지인  O: 경비실  E: 기타 U:무인택배함
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonKeepAcquaintance = setButtonLayout("예제) 배송지 정보 좀 알려줄래?");
        buttonKeepAcquaintance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","F");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                finish();
            }
        });

        Button buttonKeepDoor = setButtonLayout("예제) 배송 처리 부탁해.");
        buttonKeepDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","D");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                finish();
            }
        });

        Button buttonKeepSecurityOffice = setButtonLayout("예제) 아냐, 괜찮아.");
        buttonKeepSecurityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","O");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void drawProcessDeliveryButton(final String[] currentDeliveryInfo){
        String invoiceKeword = clovaTTS.convertNumberToClova(deliveryHelper.getSearchedInfo(currentDeliveryInfo[2]).getINV_KW());
        String sentence = "\"송장번호 "+currentDeliveryInfo[2]+", "+currentDeliveryInfo[0]
                +" 님의 상품을 배송처리할께요. 수령자는 누구입니까?"+"\"";

        String voice = "\"송장번호 "+invoiceKeword+", "+currentDeliveryInfo[0]
                +" 님의 상품을 배송처리할께요. 수령자는 누구입니까?"+"\"";

        textViewQuestion.setText(sentence);
        clovaTTS.sayThis("tts_"+currentDeliveryInfo[2],voice);


        Button buttonKeepSelf = setButtonLayout("본인이 수령");
        buttonKeepSelf.setOnClickListener(new View.OnClickListener() {  //S:본인  F: 지인  O: 경비실  E: 기타 U:무인택배함
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[1]+"] 상품을 본인이 수령하셨습니다.");
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","S");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepAcquaintance = setButtonLayout("[가족,동료,어머니]가 수령했어");
        buttonKeepAcquaintance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[1]+"] 상품을 지인이 수령하셨습니다.");
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","F");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepDoor = setButtonLayout("문 앞에 두었어");
        buttonKeepDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[1]+"] 상품을 문 앞에 두었습니다.");
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","D");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepSecurityOffice = setButtonLayout("경비실에 맡겨뒀어.");
        buttonKeepSecurityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[1]+"] 상품을 경비실에 맡겨두었으니 찾아가세요.");
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","O");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepUnmannedCourier = setButtonLayout("무인택배함");
        buttonKeepUnmannedCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[1]+"] 상품을 무인택배함에 보관했습니다.");
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","U");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonNoShipping = setButtonLayout("미배송[취소] 처리할께");
        buttonNoShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[1]+"] 상품이 미배송 처리되었습니다.");
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"N","");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[2]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.stopBluetoothSco();
    }

    private Button setButtonLayout(String btnContents){
        Button createdButton = new Button(this);
        createdButton.setText(btnContents);
        createdButton.setTextColor(Color.WHITE);
        createdButton.setTextSize(18);
        createdButton.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(createdButton);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) createdButton.getLayoutParams();
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        layoutParams.topMargin = 10;
        layoutParams.bottomMargin = 10;
        createdButton.setLayoutParams(layoutParams);
        return  createdButton;
    }

}
