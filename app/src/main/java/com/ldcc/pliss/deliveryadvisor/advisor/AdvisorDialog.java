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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.advisor.google.SpeechHelper;
import com.ldcc.pliss.deliveryadvisor.advisor.google.SpeechService;
import com.ldcc.pliss.deliveryadvisor.advisor.naver.ClovaTTS;
import com.ldcc.pliss.deliveryadvisor.analyzer.Analyzer;
import com.ldcc.pliss.deliveryadvisor.analyzer.FinalAction;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.page.NavigationActivity;
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

import java.util.List;

import io.realm.RealmResults;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by pliss on 2018. 3. 6..
 */

public class AdvisorDialog extends Activity {

    /**
     * P
     */
    private SharedPreferences prefs;

    private LinearLayout layoutForWorkButton;
    private TextView textViewQuestion;
    private TextView textIsListening;
    private TextView textDescription;
    private DeliveryHelper deliveryHelper;
    private ManagerHelper managerHelper;
    private SpeechHelper speechHelper;
    private ClovaTTS clovaTTS = AdvisorService.clovaTTS;

    private WorkUtil workUtil;
    AudioManager audioManager;

    private String[] currentDeliveryInfo;
    private Handler voiceHandler;
    boolean isPresentation;

    private String workKeyword;
    private Activity mActivity;
    private Handler myTimer;
    private Runnable myRunnable;
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

        initSetting();
    }

    private void initSetting(){
        workUtil        = new WorkUtil();
        deliveryHelper  = new DeliveryHelper(this);
        managerHelper   = new ManagerHelper (this);
        mActivity = (Activity)this;
        if(clovaTTS==null)
            clovaTTS = new ClovaTTS(getApplicationContext());

        if(speechHelper!= null)
            speechHelper.stopVoiceRecognition();
        speechHelper = new SpeechHelper(this);
        prefs= getSharedPreferences("Pref", MODE_PRIVATE);
        isPresentation = prefs.getBoolean("isPresentation",false);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.startBluetoothSco();

        workKeyword = getIntent().getStringExtra("Work-keyword");
        if(!isPresentation)
            startVoiceRecognition();

        currentDeliveryInfo = getIntent().getStringArrayExtra("Delivery-data");
        if(currentDeliveryInfo==null ||currentDeliveryInfo[0]==null){
            Log.d("송장","null");
            currentDeliveryInfo = managerHelper.getCurrentDeliveryInfoSimple();
        }

        myTimer = new Handler();
        myRunnable = new Runnable(){
            @Override
            public void run(){
                try{
                    speechHelper.stopVoiceRecognition();
                    clovaTTS.sayNoVoice();
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            finish();
                        }
                    },2500);
                }catch(Exception e){
                    Log.d("처리","기존 다이얼로그에서 동작함");
                    finish();
                }

            }
        };

        myTimer.postDelayed(myRunnable,30000);


        setLayout();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("처리","newIntent()");
        if(clovaTTS==null)
            clovaTTS = new ClovaTTS(getApplicationContext());

        speechHelper.stopVoiceRecognition();
        speechHelper = new SpeechHelper(this);
        audioManager.startBluetoothSco();

        workKeyword = getIntent().getStringExtra("Work-keyword");
        if(!isPresentation)
            startVoiceRecognition();

        currentDeliveryInfo = getIntent().getStringArrayExtra("Delivery-data");
        if(currentDeliveryInfo==null)
            currentDeliveryInfo = managerHelper.getCurrentDeliveryInfoSimple();

        setLayout();
    }

    private void setLayout(){

        layoutForWorkButton = (LinearLayout) findViewById(R.id.layout_for_work_button);
        textViewQuestion = (TextView) findViewById(R.id.text_advisor);
        layoutForWorkButton.removeAllViewsInLayout();
        textIsListening = (TextView) findViewById(R.id.text_is_listening);
        textDescription = (TextView) findViewById(R.id.text_advisor_description);

        switch (String.valueOf(workKeyword)){
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
                drawFirstQuestionButton(currentDeliveryInfo);
                break;
        }

        //audioManager가 블루투스 마이크 사용을 가져오는 잠깐의 시간 동안 Delay가 발생하고,
        //이에 따라 재생하는 음성이 끊길 수 있습니다. 따라서 블루투스 마이크 사용 설정하는 잠깐의 시간 후에(0.5초 정도) 음성을 재생합니다.
        voiceHandler = new Handler();
        voiceHandler.postDelayed(new Runnable(){
            @Override
            public void run(){
                switch (String.valueOf(workKeyword)){
                    case "null":
                        clovaTTS.sayHello();
                        break;
                    case "initialQuestion":
                        clovaTTS.sayHello();
                        break;
                    case "processDelivery":
                        String invoiceKeword = clovaTTS.convertNumberToClova(deliveryHelper.getSearchedInfo(currentDeliveryInfo[0]).getINV_KW());
                        String voice = "\"송장번호, "+invoiceKeword+", 배송처리 하겠습니다. 수령자는 누구입니까?"+"\"";
                        clovaTTS.sayThis("tts_"+currentDeliveryInfo[0],voice);
                        break;
                    case "howToProcess":
                        clovaTTS.sayHelp();
                        break;
                }
            }
        },DELAY_MILLIS_FOR_TTS);

        setListener();
    }

    private void setListener(){

        speechHelper.addListener(new SpeechHelper.Listener() {
            @Override
            public void onVoiceAnalyed(int analyzeResult, List<String> invoiceKeywords) {
                if(invoiceKeywords.size()>0)
                    Log.d("처리송장 : ", invoiceKeywords.get(0));
                speechHelper.stopVoiceRecognition();
                boolean hasResults= false;
                hasResults = processCurrentSituation(analyzeResult);
                hasResults = processNextSituation(analyzeResult);
                hasResults = processInvoiceSituation(analyzeResult,invoiceKeywords);
                if(!hasResults){
                    startVoiceRecognition();
                }else
                    speechHelper.stopVoiceRecognition();
            }
        });

        speechHelper.addConnection(new SpeechHelper.Connection() {
            @Override
            public void onConnected(int connectNumber) {
                if (connectNumber>=0){
                    textIsListening.setTextColor(0xFF00FA92);
                    textIsListening.setText("Listening..");
                    textDescription.setTextColor(0xFFAAAAAA);
                    //layoutForWorkButton.setBackgroundColor(0xFF00FA92);
                }


            }
        });

        final ClovaTTS.Listener mListener = new ClovaTTS.Listener() {
            @Override
            public void onSpeakingFinished(boolean isFinal) {
                if(isPresentation){
                    //speechHelper = new SpeechHelper(mActivity);
                    startVoiceRecognition();
                }

            }
        };
        clovaTTS.addListener(mListener);
    }

    private boolean processCurrentSituation(int analyzeResult){
        speechHelper.stopVoiceRecognition();
        boolean hasResult = true;
        switch (analyzeResult){
            //대상자 없이 전화연결해달라고 말한 경우, 현재 고객에게 전화연결합니다.
            case FinalAction.CALL_CURRENT_CUSTOMER:
                clovaTTS.sayThis("tts_call","전화 연결합니다.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.callTheCustomer(getApplicationContext(),currentDeliveryInfo[4]);
                        finish();
                    }
                },2000);
                break;

            //대상자 없이 배송 완료 해달라고 말한 경우, 현재 고객의 상품을 배송완료 처리합니다.
            //배송 완료 처리 방법을 알기 위해, 다시 한 번 다이얼로그를 띄웁니다.
            case FinalAction.DONE_SIMPLE_CURRENNT:
                finish();
                workUtil.showProcessDeliveryDialog(AdvisorDialog.this);
                break;

            //대상자 없이 배송 취소 해달라고 말한 경우 현재 고객의 배송을 취소 처리합니다.
            case FinalAction.CANCLE_CURRENT:
                clovaTTS.sayThis("tts_cancle_shipping","배송 취소 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                    workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품이 배송 취소되었습니다.");
                    deliveryHelper.processDelivery(currentDeliveryInfo[0],"N","");
                    deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                    finish();
                    }
                },3000);

                break;

            //대상자 없이 배송 상품에 대해 본인이 수령했다고 말한 경우, 현재 배송 상품을 본인이 수령한 것으로 배송완료 처리합니다.
            case FinalAction.DONE_SELF_CURRENT:
                clovaTTS.sayThis("tts_done_shipping","배송 완료 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                    workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 본인이 수령하셨습니다. 좋은 하루 되세요 ^^");
                    deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","S");
                    deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                    finish();
                    }
                },3000);

                break;

            //대상자 없이 배송 상품을 다른 사람이 수령했다고 말한 경우, 현재 배송 상품을 다른 사람이 수령한 것으로 배송완료 처리합니다.
            case FinalAction.DONE_OTHER_CURRENT:
                clovaTTS.sayThis("tts_done_shipping","배송 완료 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 다른 분께서 수령해 주셨습니다.");
                        deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","E");
                        deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                        finish();
                    }
                },3000);

                break;

            //대상자 없이 가족(부모님, 외삼촌 등등)이 수령했다고 말한 경우, 현재 배송 상품을 가족이 수령한 것으로 배송완료 처리합니다.
            case FinalAction.DONE_OTHER_CURRENT_FAMILY:
                clovaTTS.sayThis("tts_done_shipping","배송 완료 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 가족이 수령해 주었어요. 좋은 하루 되세요 ^^");
                        deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","F");
                        deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                        finish();
                    }
                },3000);

                break;

            //대상자 없이 회사 사람이 수령했다고 말한 경우, 현재 배송 상품을 회사 사람이 수령한 것으로 배송완료 처리합니다.
            case FinalAction.DONE_OTHER_CURRENT_COMPANY:
                clovaTTS.sayThis("tts_done_shipping","배송 완료 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 회사에서 수령하였습니다. 좋은 하루 되세요 ^^");
                        deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","C");
                        deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                        finish();
                    }
                },3000);


                break;

            //대상자 없이 지인이 수령했다고 말한 경우, 현재 배송 상품을 지인이 수령한 것으로 배송완료 처리합니다.
            case FinalAction.DONE_OTHER_CURRENT_FRIEND:
                clovaTTS.sayThis("tts_done_shipping","배송 완료 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 지인에게 맡겨두었으니 찾아가세요.");
                        deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","A");
                        deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                        finish();
                    }
                },3000);

                break;

            //대상자 없이 문 앞, 문 옆 등에 두었다고 말한 경우, 현재 상품을 문앞에 둔 것으로 배송완료 처리합니다.
            case FinalAction.DONE_KEEP_CURRENT_DOOR:
                clovaTTS.sayThis("tts_done_shipping","배송 완료 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 문 앞에 맡겨두었으니 찾아가세요.");
                        deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","D");
                        deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                        finish();
                    }
                },3000);

                break;

            //대상자 없이 경비실에 맡겼다고 말한 경우, 현재 상품을 경비실에 맡긴 것으로 배송완료 처리합니다.
            case FinalAction.DONE_KEEP_CURRENT_SECURITY:
                clovaTTS.sayThis("tts_done_shipping","배송 완료 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 경비실에 맡겨두었으니 찾아가세요.");
                        deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","O");
                        deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                        finish();
                    }
                },3000);

                break;

            //대상자 없이 저장소(무인택배함,문서수발실)에 두었다고 말한 경우, 현재 상품을 택배함에 보관한 것으로 배송완료 처리합니다.
            case FinalAction.DONE_KEEP_CURRENT_STORAGE:
                clovaTTS.sayThis("tts_done_shipping","배송 완료 처리했습니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 택배함에 보관하였으니 찾아가세요.");
                        deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","U");
                        deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                        finish();
                    }
                },3000);

                break;
            case FinalAction.NAVI_CURRENT:
                clovaTTS.sayThis("tts_navigation","고객의 위치를 보여드릴께요.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        startActivity(new Intent(AdvisorDialog.this, NavigationActivity.class));
                        finishAffinity();
                    }
                },3000);
                break;

            //현재 필요없다고 말한 경우, 작별 인사와 함께 종료합니다.
            case FinalAction.CANCLE:
                clovaTTS.sayGoodBye();
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        finish();
                    }
                },1500);
                break;

            //사용방법을 물어본 경우, 사용 방법을 음성으로 안내합니다.
            case FinalAction.HOW_TO_USE:
                finish();
                workUtil.showQuestionDialogWithHelp(this,currentDeliveryInfo);
                break;
            default:
                return false;
        }

        return hasResult;

    }

    private boolean processNextSituation(int analyzeResult){
        speechHelper.stopVoiceRecognition();
        boolean hasResult = true;

        final Delivery nextProduct = deliveryHelper.findNext(currentDeliveryInfo[0]);
        switch (analyzeResult) {
            //대상자 없이 전화연결해달라고 말한 경우, 현재 고객에게 전화연결합니다.
            case FinalAction.CALL_NEXT_CUSTOMER:
                clovaTTS.sayThis("tts_call_next","다음 고객에게 전화 연결합니다.");

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.callTheCustomer(getApplicationContext(),nextProduct.getRECV_1_TELNO());
                        finish();
                    }
                },3000);

                break;
            case FinalAction.DONE_SIMPLE_NEXT:
                finish();
                workUtil.showProcessDeliveryDialog(AdvisorDialog.this,managerHelper.getSearchedInfoSimple(nextProduct));
                break;
            case FinalAction.CANCLE_NEXT:
                Toast.makeText(getApplicationContext(),"배송 취소 처리 완료하였습니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_cancle_shipping","배송 취소 처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품이 배송 취소되었습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"N","");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);
                break;
            case FinalAction.DONE_SELF_NEXT:
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품을 본인이 수령하였습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"C","S");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);
                break;
            case FinalAction.DONE_OTHER_NEXT:
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();

                clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품을 대신 수령하였습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"C","E");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);

                break;
            case FinalAction.DONE_OTHER_NEXT_FAMILY:
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품을 가족이 수령하였습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"C","F");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);
                break;
            case FinalAction.DONE_OTHER_NEXT_FRIEND:
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품을 지인이 수령하였습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"C","A");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);
                break;
            case FinalAction.DONE_OTHER_NEXT_COMPANY:
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품을 회사 동료가 수령하였습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"C","C");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);
                break;
            case FinalAction.DONE_KEEP_NEXT_DOOR:
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품을 문 앞에 두었습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"C","D");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);
                break;
            case FinalAction.DONE_KEEP_NEXT_SECURITY:
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품을 경비실에 맡겼습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"C","E");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);
                break;
            case FinalAction.DONE_KEEP_NEXT_STORAGE:
                Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        workUtil.sendSMS(getApplicationContext(),nextProduct.getRECV_1_TELNO(),"고객님, [" + nextProduct.getITEM_NM()+"] 상품을 택배함에 보관하였습니다.");
                        deliveryHelper.processDelivery(nextProduct.getINV_NUMB(),"C","U");
                        deliveryHelper.changeManagerInfoToNext(nextProduct.getINV_NUMB());
                        finish();
                    }
                },1500);
                break;
            case FinalAction.NAVI_NEXT: // 지도에 다음 위치 보이는 거 안 해둠
                Toast.makeText(getApplicationContext(),"다음 고객의 위치를 안내합니다.",LENGTH_SHORT).show();
                clovaTTS.sayThis("tts_navigation_next","다음 고객의 위치를 보여드릴께요.");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        Intent intent = new Intent(AdvisorDialog.this, NavigationActivity.class);
                        intent.putExtra("lat",nextProduct.getRECV_ADDR_LAT());
                        intent.putExtra("lng",nextProduct.getRECV_ADDR_LNG());
                        intent.putExtra("product-name",nextProduct.getITEM_NM());
                        intent.putExtra("loc",nextProduct.getRECV_ADDR());
                        startActivity(intent);
                        finishAffinity();
                    }
                },2000);
                break;
            default:
                return false;
        }

        return hasResult;
    }

    private boolean processInvoiceSituation(int analyzeResult, List<String> invoiceKeywords){
        speechHelper.stopVoiceRecognition();
        boolean hasResult = true;
        RealmResults<Delivery> invoiceProduct = null;
        try{
            invoiceProduct = deliveryHelper.findInvoiceFromKeyword(invoiceKeywords.get(0));
        }catch(Exception e){

        }

        if(invoiceProduct==null){
            return false;
        }
        else if(invoiceProduct.size()>1){
            clovaTTS.sayThis("tts_say_a_lot_of","하나의 송장번호만 말해주세요.");
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    workUtil.showProcessDeliveryDialog(AdvisorDialog.this);
                }
            },3500);

            return false;
        } else if(invoiceProduct.size()<1){
            clovaTTS.sayThis("tts_not_searched","해당 송장번호가 존재하지 않습니다.");
            return false;
        }else{
            final Delivery searchedDelivery = invoiceProduct.first();
            switch (analyzeResult){
                case FinalAction.CALL_INVOICE_CUSTOMER:
                    clovaTTS.sayThis("tts_call_invoice","검색된 송장번호로 전화 연결합니다.");

                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.callTheCustomer(getApplicationContext(),searchedDelivery.getRECV_1_TELNO());
                            finish();
                        }
                    },4000);
                    break;
                case FinalAction.DONE_SIMPLE_INVOICE:
                    finish();
                    workUtil.showProcessDeliveryDialog(AdvisorDialog.this,managerHelper.getSearchedInfoSimple(searchedDelivery));
                    break;
                case FinalAction.CANCLE_INVOICE:
                    Toast.makeText(getApplicationContext(),"배송 취소를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_cancle_shipping","배송 취소 처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품이 배송 취소되었습니다.");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"N","");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.DONE_SELF_INVOICE:
                    Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품을 본인이 수령하였습니다.");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"C","S");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.DONE_OTHER_INVOICE:
                    Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품을 타인이 수령하였습니다.");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"C","E");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.DONE_OTHER_INVOICE_FAMILY:
                    Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품을 가족에게 전달하였습니다.");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"C","F");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.DONE_OTHER_INVOICE_FRIEND:
                    Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품을 지인에게 전달하였으니 찾아가세요.");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"C","A");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.DONE_OTHER_INVOICE_COMPANY:
                    Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품을 회사 관계자에게 전달했어요. ^^");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"C","C");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.DONE_KEEP_INVOICE_DOOR:
                    Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품을 문 앞에 두었습니다.");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"C","D");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.DONE_KEEP_INVOICE_SECURITY:
                    Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품을 경비실(또는 경비원)에게 전달헸어요.");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"C","O");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.DONE_KEEP_INVOICE_STORAGE:
                    Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_done_shipping","배송처리 완료.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            workUtil.sendSMS(getApplicationContext(),searchedDelivery.getRECV_1_TELNO(),"고객님, [" + searchedDelivery.getITEM_NM()+"] 상품을 택배함에 두었습니다.");
                            deliveryHelper.processDelivery(searchedDelivery.getINV_NUMB(),"C","U");
                            deliveryHelper.changeManagerInfoToNext(searchedDelivery.getINV_NUMB());
                            finish();
                        }
                    },1500);
                    break;
                case FinalAction.NAVI_INVOICE:
                    Toast.makeText(getApplicationContext(),"해당 송장번호의 위치로 안내합니다.",LENGTH_SHORT).show();
                    clovaTTS.sayThis("tts_navigation_invoice","해당 송장의 위치를 보여드릴께요.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            Intent intent = new Intent(AdvisorDialog.this, NavigationActivity.class);
                            intent.putExtra("lat",searchedDelivery.getRECV_ADDR_LAT());
                            intent.putExtra("lng",searchedDelivery.getRECV_ADDR_LNG());
                            intent.putExtra("product-name",searchedDelivery.getITEM_NM());
                            intent.putExtra("loc",searchedDelivery.getRECV_ADDR());
                            startActivity(intent);
                            finishAffinity();
                        }
                    },3000);
                    break;
                default:
                    return false;
            }
        }
        return  hasResult;
    }

    @Override
    protected void onStop() {
        Log.d("처리","onStop()");
        myTimer.removeCallbacks(myRunnable);
        clovaTTS.stopClovaTTS();
        //audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.stopBluetoothSco();
        try{
            speechHelper.stopVoiceRecognition();
        }catch (Exception e){
            e.printStackTrace();
        }
        voiceHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    private void drawFirstQuestionButton(final String[] currentDeliveryInfo){
        textViewQuestion.setText(clovaTTS.getHelloMsg());

        Button buttonHelp = setButtonLayout("➜ 사용 방법 알려줘☻");
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clovaTTS.sayHelp();
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
            }
        });

        Button buttonKeepSelf = setButtonLayout("➜ 전화 연결해줘☻");
        buttonKeepSelf.setOnClickListener(new View.OnClickListener() {  //S:본인  F: 지인  O: 경비실  E: 기타 U:무인택배함
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonKeepAcquaintance = setButtonLayout("➜ 위치 알려줘☻");
        buttonKeepAcquaintance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","F");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                finish();
            }
        });

        Button buttonKeepDoor = setButtonLayout("➜ 배송 처리 부탁해☻");
        buttonKeepDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","D");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                finish();
            }
        });

        Button buttonKeepSecurityOffice = setButtonLayout("➜ 아니야, 괜찮아☻");
        buttonKeepSecurityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","O");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void drawProcessDeliveryButton(final String[] currentDeliveryInfo){
        speechHelper.stopVoiceRecognition();
        String sentence = "\"송장번호 "+currentDeliveryInfo[0]+", "+currentDeliveryInfo[1]
                +" 님의 상품을 배송처리할께요. 수령자는 누구입니까?"+"\"";
        textViewQuestion.setText(sentence);

        Button buttonKeepSelf = setButtonLayout("예제) 본인이 수령");
        buttonKeepSelf.setOnClickListener(new View.OnClickListener() {  //S:본인  F: 지인  O: 경비실  E: 기타 U:무인택배함
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 본인이 수령하셨습니다.");
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","S");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepAcquaintance = setButtonLayout("예제) 지인[엄마,친구]가 수령했어");
        buttonKeepAcquaintance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 지인이 수령하셨습니다.");
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","F");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepDoor = setButtonLayout("예제) 문 앞에 두었어");
        buttonKeepDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 문 앞에 두었습니다.");
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","D");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepSecurityOffice = setButtonLayout("예제) 경비실에 맡겨뒀어.");
        buttonKeepSecurityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 경비실에 맡겨두었으니 찾아가세요.");
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","O");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonKeepUnmannedCourier = setButtonLayout("예제) 무인택배함");
        buttonKeepUnmannedCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품을 무인택배함에 보관했습니다.");
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"C","U");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
                //Toast.makeText(getApplicationContext(),"배송 처리를 완료하였습니다.",LENGTH_SHORT).show();
                finish();
            }
        });

        Button buttonNoShipping = setButtonLayout("예제) 미배송[취소] 처리할께");
        buttonNoShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.sendSMS(getApplicationContext(),currentDeliveryInfo[4],"고객님, [" + currentDeliveryInfo[2]+"] 상품이 미배송 처리되었습니다.");
                deliveryHelper.processDelivery(currentDeliveryInfo[0],"N","");
                deliveryHelper.changeManagerInfoToNext(currentDeliveryInfo[0]);
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
        createdButton.setBackgroundResource(R.drawable.rounded_button_advisor);
        createdButton.setGravity(Gravity.LEFT);
        layoutForWorkButton.addView(createdButton);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) createdButton.getLayoutParams();
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        layoutParams.topMargin = 10;
        layoutParams.bottomMargin = 10;
        createdButton.setLayoutParams(layoutParams);
        return  createdButton;
    }

    public void startVoiceRecognition(){
        Log.d("테스트",workKeyword);
        switch (String.valueOf(workKeyword)){
            case "null":
                speechHelper.startVoiceRecognition(Analyzer.POPUP_HELLO_MODE);
                break;
            case "initialQuestion":
                speechHelper.startVoiceRecognition(Analyzer.POPUP_HELLO_MODE);
                break;
            case "processDelivery":
                speechHelper.startVoiceRecognition(Analyzer.POPUP_PROCESS);
                break;
            case "howToProcess":
                speechHelper.startVoiceRecognition(Analyzer.POPUP_HELLO_MODE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.stopBluetoothSco();
        speechHelper.stopVoiceRecognition();
    }
}
