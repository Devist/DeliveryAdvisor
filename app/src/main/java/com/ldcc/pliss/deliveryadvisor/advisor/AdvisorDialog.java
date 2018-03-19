package com.ldcc.pliss.deliveryadvisor.advisor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.advisor.daum.NewtoneTalk;
import com.ldcc.pliss.deliveryadvisor.advisor.google.SpeechHelper;
import com.ldcc.pliss.deliveryadvisor.advisor.naver.ClovaTTS;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.page.NavigationActivity;
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechListener;

import static android.widget.Toast.LENGTH_SHORT;
import static com.ldcc.pliss.deliveryadvisor.advisor.AdvisorService.TAG;

/**
 * Created by pliss on 2018. 3. 6..
 */

public class AdvisorDialog extends Activity implements TextToSpeechListener {

    private LinearLayout layoutForWorkButton;
    private TextView textViewQuestion;
    private DeliveryHelper deliveryHelper;
    private ManagerHelper managerHelper;

    private SpeechHelper speechHelper;

    private NewtoneTalk newtoneTalk;
    private ClovaTTS clovaTTS = AdvisorService.clovaTTS;

    private WorkUtil workUtil;

//    Intent intent = new Intent(this, MainActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_advisor);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        deliveryHelper = new DeliveryHelper(this);
        managerHelper = new ManagerHelper(this);
        workUtil = new WorkUtil();

        layoutForWorkButton = (LinearLayout) findViewById(R.id.layout_for_work_button);
        textViewQuestion = (TextView) findViewById(R.id.text_advisor);

        if(clovaTTS==null)
            clovaTTS = new ClovaTTS(getFilesDir());


        String myWork = getIntent().getStringExtra("Work-keyword");
        if(myWork==null){
            textViewQuestion.setText("무엇을 도와드릴까요?");
            clovaTTS.sayThis("tts_welcome","무엇을 도와드릴까요?");
            String[] currentDeliveryInfo = getIntent().getStringArrayExtra("Delivery-data");
            drawFirstQuestionButton(currentDeliveryInfo);
        }
        else if(myWork.equals("processDelivery")){
            String[] currentDeliveryInfo = getIntent().getStringArrayExtra("Delivery-data");
            drawProcessDeliveryButton(currentDeliveryInfo);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        speechHelper = new SpeechHelper(this);
        speechHelper.startVoiceRecognition();

    }
    @Override
    protected void onStop() {


        clovaTTS.stopClovaTTS();
        try{
            Thread a = speechHelper.stopVoiceRecognition();
        }catch (Exception e){

        }

//        try{
//            a.join();
//        }catch (InterruptedException e) {
//
//        }


        //startActivity(intent);
        //finish();
        // Stop listening to voice

        super.onStop();


    }

    private void drawFirstQuestionButton(final String[] currentDeliveryInfo){


        Button buttonKeepSelf = new Button(this);
        buttonKeepSelf.setText("예제) 고객에게 전화 연결해줘.");
        buttonKeepSelf.setTextColor(Color.WHITE);
        buttonKeepSelf.setTextSize(18);
        buttonKeepSelf.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepSelf);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) buttonKeepSelf.getLayoutParams();
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        layoutParams.topMargin = 10;
        layoutParams.bottomMargin = 10;
        buttonKeepSelf.setOnClickListener(new View.OnClickListener() {  //S:본인  F: 지인  O: 경비실  E: 기타 U:무인택배함
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        buttonKeepSelf.setLayoutParams(layoutParams);


        Button buttonKeepAcquaintance = new Button(this);
        buttonKeepAcquaintance.setText("예제) 배송지 정보 좀 알려줄래?");
        buttonKeepAcquaintance.setTextColor(Color.WHITE);
        buttonKeepAcquaintance.setTextSize(18);
        buttonKeepAcquaintance.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepAcquaintance);
        buttonKeepAcquaintance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","F");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepAcquaintance.setLayoutParams(layoutParams);


        Button buttonKeepDoor = new Button(this);
        buttonKeepDoor.setText("예제) 배송 처리 부탁해.");
        buttonKeepDoor.setTextColor(Color.WHITE);
        buttonKeepDoor.setTextSize(18);
        buttonKeepDoor.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepDoor);
        buttonKeepDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","D");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepDoor.setLayoutParams(layoutParams);

        Button buttonKeepSecurityOffice = new Button(this);
        buttonKeepSecurityOffice.setText("예제) 아냐, 괜찮아.");
        buttonKeepSecurityOffice.setTextColor(Color.WHITE);
        buttonKeepSecurityOffice.setTextSize(18);
        buttonKeepSecurityOffice.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepSecurityOffice);
        buttonKeepSecurityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","O");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepSecurityOffice.setLayoutParams(layoutParams);

        Button buttonKeepUnmannedCourier = new Button(this);
        buttonKeepUnmannedCourier.setText("무인택배함");
        buttonKeepUnmannedCourier.setTextColor(Color.WHITE);
        buttonKeepUnmannedCourier.setTextSize(18);
        buttonKeepUnmannedCourier.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepUnmannedCourier);
        buttonKeepUnmannedCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","U");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepUnmannedCourier.setLayoutParams(layoutParams);
    }

    private void drawProcessDeliveryButton(final String[] currentDeliveryInfo){

        String sentence = "\"송장번호 "+currentDeliveryInfo[2]+", "+currentDeliveryInfo[0]
                +" 님에게 전달하는 상품을 배송처리하겠습니다. 수령자는 누구입니까?"+"\"";

        textViewQuestion.setText(sentence);
        clovaTTS.sayThis("tts_"+currentDeliveryInfo[2],sentence);

        Button buttonKeepSelf = new Button(this);
        buttonKeepSelf.setText("본인이 수령");
        buttonKeepSelf.setTextColor(Color.WHITE);
        buttonKeepSelf.setTextSize(18);
        buttonKeepSelf.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepSelf);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) buttonKeepSelf.getLayoutParams();
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        layoutParams.topMargin = 10;
        layoutParams.bottomMargin = 10;
        buttonKeepSelf.setOnClickListener(new View.OnClickListener() {  //S:본인  F: 지인  O: 경비실  E: 기타 U:무인택배함
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","S");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepSelf.setLayoutParams(layoutParams);


        Button buttonKeepAcquaintance = new Button(this);
        buttonKeepAcquaintance.setText("[가족,동료,어머니]가 수령했어");
        buttonKeepAcquaintance.setTextColor(Color.WHITE);
        buttonKeepAcquaintance.setTextSize(18);
        buttonKeepAcquaintance.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepAcquaintance);
        buttonKeepAcquaintance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","F");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepAcquaintance.setLayoutParams(layoutParams);


        Button buttonKeepDoor = new Button(this);
        buttonKeepDoor.setText("문 앞에 두었어");
        buttonKeepDoor.setTextColor(Color.WHITE);
        buttonKeepDoor.setTextSize(18);
        buttonKeepDoor.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepDoor);
        buttonKeepDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","D");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepDoor.setLayoutParams(layoutParams);

        Button buttonKeepSecurityOffice = new Button(this);
        buttonKeepSecurityOffice.setText("경비실에 맡겨뒀어.");
        buttonKeepSecurityOffice.setTextColor(Color.WHITE);
        buttonKeepSecurityOffice.setTextSize(18);
        buttonKeepSecurityOffice.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepSecurityOffice);
        buttonKeepSecurityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","O");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepSecurityOffice.setLayoutParams(layoutParams);

        Button buttonKeepUnmannedCourier = new Button(this);
        buttonKeepUnmannedCourier.setText("무인택배함");
        buttonKeepUnmannedCourier.setTextColor(Color.WHITE);
        buttonKeepUnmannedCourier.setTextSize(18);
        buttonKeepUnmannedCourier.setBackgroundResource(R.drawable.rounded);
        layoutForWorkButton.addView(buttonKeepUnmannedCourier);
        buttonKeepUnmannedCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","U");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepUnmannedCourier.setLayoutParams(layoutParams);
    }

    @Override
    public void onError(int code, String message) {
        handleError(code);
//
//        ttsClient = null;
    }

    @Override
    public void onFinished() {
//        int intSentSize = ttsClient.getSentDataSize();
//        int intRecvSize = ttsClient.getReceivedDataSize();
//
//        final String strInacctiveText = "onFinished() SentSize : " + intSentSize + " RecvSize : " + intRecvSize;
//
//        Log.i(TAG, strInacctiveText);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mStatus.setText(strInacctiveText);
//            }
//        });
//
//        ttsClient = null;
    }

    private void handleError(int errorCode) {
        String errorText;
        switch (errorCode) {
            case TextToSpeechClient.ERROR_NETWORK:
                errorText = "네트워크 오류";
                break;
            case TextToSpeechClient.ERROR_NETWORK_TIMEOUT:
                errorText = "네트워크 지연";
                break;
            case TextToSpeechClient.ERROR_CLIENT_INETRNAL:
                errorText = "음성합성 클라이언트 내부 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_INTERNAL:
                errorText = "음성합성 서버 내부 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_TIMEOUT:
                errorText = "음성합성 서버 최대 접속시간 초과";
                break;
            case TextToSpeechClient.ERROR_SERVER_AUTHENTICATION:
                errorText = "음성합성 인증 실패";
                break;
            case TextToSpeechClient.ERROR_SERVER_SPEECH_TEXT_BAD:
                errorText = "음성합성 텍스트 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_SPEECH_TEXT_EXCESS:
                errorText = "음성합성 텍스트 허용 길이 초과";
                break;
            case TextToSpeechClient.ERROR_SERVER_UNSUPPORTED_SERVICE:
                errorText = "음성합성 서비스 모드 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_ALLOWED_REQUESTS_EXCESS:
                errorText = "허용 횟수 초과";
                break;
            default:
                errorText = "정의하지 않은 오류";
                break;
        }

        final String statusMessage = errorText + " (" + errorCode + ")";

        Log.i(TAG, statusMessage);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("error",statusMessage);
            }
        });
    }
}
