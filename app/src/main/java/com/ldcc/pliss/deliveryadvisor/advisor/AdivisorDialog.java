package com.ldcc.pliss.deliveryadvisor.advisor;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by pliss on 2018. 3. 6..
 */

public class AdivisorDialog extends Activity {

    private LinearLayout layoutForWorkButton;
    private TextView textViewQuestion;
    private DeliveryHelper deliveryHelper = new DeliveryHelper(this);
    private ManagerHelper managerHelper = new ManagerHelper(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_advisor);


        layoutForWorkButton = (LinearLayout) findViewById(R.id.layout_for_work_button);
        textViewQuestion = (TextView) findViewById(R.id.text_advisor);

        String myWork = getIntent().getStringExtra("Work-keyword");

        if(myWork.equals("processDelivery")){
            String[] currentDeliveryInfo = getIntent().getStringArrayExtra("Delivery-data");
            processDelivery(currentDeliveryInfo);
        }

    }

    private void processDelivery(final String[] currentDeliveryInfo){

        textViewQuestion.setText("\"송장번호 "+currentDeliveryInfo[2]+", "+currentDeliveryInfo[0]
                +" 님에게 전달하는 상품을 배송처리하겠습니다. 수령자는 누구입니까?"+"\"");

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
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
        buttonKeepUnmannedCourier.setLayoutParams(layoutParams);
    }
}
