package com.ldcc.pliss.deliveryadvisor.util;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.advisor.AdvisorDialog;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by pliss on 2018. 2. 27..
 */

public class WorkUtil {

    private SharedPreferences prefs;
    private Context context;
    private Bundle bundle = new Bundle();

    /**
     * 음성인식 - 사용자가 음성인식을 활성화했을 때, 시작 팝업을 띄우는 기능입니다.
     * (Ex. 무엇을 도와드릴까요?)
     *
     * @param context     Activity 로부터 가져온 context (getApplicationContext())
     * @param managerInfo ManagerHelper.getCurrentDeliveryInfoSimple() 에서 가져온 값
     */
    public void showFirstQuestionDialog (Context context, String[] managerInfo){
        this.context = context;
        bundle.putString("Work-keyword","initialQuestion");
        bundle.putStringArray("Delivery-data",managerInfo);
        popupAdvisorDialog(bundle);
    }

    /**
     * 음성인식 - 사용자가 배송 처리를 할 때, 배송 처리 팝업을 띄우는 기능입니다.
     * (Ex. 송장번호 0000 를 배송 처리하겠습니까?)
     *
     * @param context     Activity 로부터 가져온 context (getApplicationContext())
     * @param managerInfo ManagerHelper.getCurrentDeliveryInfoSimple() 에서 가져온 값
     */
    public void showProcessDeliveryDialog (Context context,String[] managerInfo){
        this.context = context;
        bundle.putString("Work-keyword","processDelivery");
        bundle.putStringArray("Delivery-data",managerInfo);
        popupAdvisorDialog(bundle);
    }

    public void showProcessDeliveryDialog (Context context,String[] managerInfo, int mode){
        this.context = context;
        bundle.putString("Work-keyword","processDelivery");
        bundle.putInt("speech",-1);
        bundle.putStringArray("Delivery-data",managerInfo);
        popupAdvisorDialog(bundle);
    }

    /**
     * 전화연결 - 고객에게 전화를 걸 때 사용합니다.
     *
     * @param context       Activity 로부터 가져온 context (getApplicationContext())
     * @param phoneNumber   고객 전화 번호. ( 가능한 형식 : "01012345678", "010-1234-5678" )
     */
    public void callTheCustomer(Context context, String phoneNumber){
        this.context = context;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phoneNumber));
        if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            this.context.startActivity(intent);
        }else{
            Toast.makeText(this.context,"전화 권한을 허용한 후, 다시 버튼을 눌러주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 문자 전송 - 사용자가 고객에메 문자 메시지를 전송할 때 사용하는 기능입니다.
     *
     * @param context       Activity 로부터 가져온 context (getApplicationContext())
     * @param phoneNumber   고객 전화 번호. ( 가능한 형식 : "01012345678", "010-1234-5678" )
     * @param message       전송할 문자 내용
     */
    public void sendSMS(Context context, String phoneNumber, String message){
        prefs = context.getSharedPreferences("Pref", MODE_PRIVATE);

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(context,0,new Intent(SENT),0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context,0,new Intent(DELIVERED),0);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        // 전송 성공
                        Toast.makeText(context, "전송 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // 전송 실패
                        Toast.makeText(context, "전송 실패", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // 서비스 지역 아님
                        Toast.makeText(context, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // 무선 꺼짐
                        Toast.makeText(context, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU 실패
                        Toast.makeText(context, "PDU Null", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT_ACTION"));

        if(prefs.getBoolean("isSMS",false)){
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }
    }


    /**
     *
     *
     * @param bundle
     */
    private void popupAdvisorDialog(Bundle bundle) {
        Intent popupIntent = new Intent(context, AdvisorDialog.class);
        popupIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        popupIntent.putExtras(bundle);
        PendingIntent pie= PendingIntent.getActivity(context, 0, popupIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pie.send();
        }catch (PendingIntent.CanceledException e) {

        }
    }
}
