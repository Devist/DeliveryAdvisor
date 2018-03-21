package com.ldcc.pliss.deliveryadvisor.advisor;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

/**
 * Created by pliss on 2018. 3. 6..
 */

public class AdvisorDialog extends Activity {

    private LinearLayout layoutForWorkButton;
    private TextView textViewQuestion;
    private DeliveryHelper deliveryHelper;
    private ManagerHelper managerHelper;
    private SpeechHelper speechHelper;
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
        String[] currentDeliveryInfo;

        switch (String.valueOf(myWork)){
            case "null":
                currentDeliveryInfo = getIntent().getStringArrayExtra("Delivery-data");
                drawFirstQuestionButton(currentDeliveryInfo);
                break;
            case "processDelivery":
                currentDeliveryInfo = getIntent().getStringArrayExtra("Delivery-data");
                drawProcessDeliveryButton(currentDeliveryInfo);
                break;
            case "howToProcess":
                break;
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
        super.onStop();
    }

    private void drawFirstQuestionButton(final String[] currentDeliveryInfo){
        textViewQuestion.setText("무엇을 도와드릴까요?");
        clovaTTS.sayThis("tts_welcome","무엇을 도와드릴까요?");

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
                deliveryHelper.changeManagerInfoToNext();
                finish();
            }
        });

        Button buttonKeepDoor = setButtonLayout("예제) 배송 처리 부탁해.");
        buttonKeepDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","D");
                deliveryHelper.changeManagerInfoToNext();
                finish();
            }
        });

        Button buttonKeepSecurityOffice = setButtonLayout("예제) 아냐, 괜찮아.");
        buttonKeepSecurityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","O");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepUnmannedCourier = setButtonLayout("무인택배함");
        buttonKeepUnmannedCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","U");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        VoiceAnalyzer.getAnalyzedAction(VoiceAnalyzer.POPUP_HELLO_MODE,"안녕");
    }

    private void drawProcessDeliveryButton(final String[] currentDeliveryInfo){

        String sentence = "\"송장번호 "+currentDeliveryInfo[2]+", "+currentDeliveryInfo[0]
                +" 님에게 전달하는 상품을 배송처리하겠습니다. 수령자는 누구입니까?"+"\"";

        textViewQuestion.setText(sentence);
        clovaTTS.sayThis("tts_"+currentDeliveryInfo[2],sentence);

        Button buttonKeepSelf = setButtonLayout("본인이 수령");
        buttonKeepSelf.setOnClickListener(new View.OnClickListener() {  //S:본인  F: 지인  O: 경비실  E: 기타 U:무인택배함
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","S");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepAcquaintance = setButtonLayout("[가족,동료,어머니]가 수령했어");
        buttonKeepAcquaintance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","F");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepDoor = setButtonLayout("문 앞에 두었어");
        buttonKeepDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","D");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepSecurityOffice = setButtonLayout("경비실에 맡겨뒀어.");
        buttonKeepSecurityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","O");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepUnmannedCourier = setButtonLayout("무인택배함");
        buttonKeepUnmannedCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processCurrentDelivery(currentDeliveryInfo[2],"C","U");
                deliveryHelper.changeManagerInfoToNext();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
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
