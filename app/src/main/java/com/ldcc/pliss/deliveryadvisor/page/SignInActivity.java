package com.ldcc.pliss.deliveryadvisor.page;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.util.CsvUtil;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class SignInActivity extends AppCompatActivity {

    private List<String[]> workData;

    //UI 위젯
//    private TextView successProgressText;
//    private TextView failProgressText;
    private EditText editTextManager;
    private DeliveryHelper deliveryHelper;

    // 앱이 최초 실행인지 확인하기 위한 변수
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign);
        checkPermission();
        init();
    }

    private void init(){
//        successProgressText = (TextView) findViewById(R.id.textSuccessProgress);
//        failProgressText    = (TextView) findViewById(R.id.textFailProgress);
        editTextManager     = (EditText) findViewById(R.id.edit_text_manager);
        deliveryHelper      = new DeliveryHelper(this);
        TextView tvView = (TextView) findViewById(R.id.sign_title);
//        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.sign_ll);
//        LinearLayout signUserLayout = (LinearLayout)findViewById(R.id.sign_user_ll);
        Animation animationLtoR = AnimationUtils.loadAnimation(this, R.anim.from_lx_to_rx);
        Animation animationRtoL = AnimationUtils.loadAnimation(this, R.anim.from_rx_to_lx);
        Animation animationTtoB = AnimationUtils.loadAnimation(this, R.anim.from_ty_to_by);
        //linearLayout.startAnimation(animationLtoR);
        tvView.startAnimation(animationTtoB);
        //signUserLayout.startAnimation(animationRtoL);


        CsvUtil csvUtil = new CsvUtil();
        workData = csvUtil.readCSV(this);

        Realm.init(this);
        Realm mRealm = Realm.getDefaultInstance();
//        RealmResults<Delivery> delivery = mRealm.where(Delivery.class).findAll();
//        RealmChangeListener callback = new RealmChangeListener() {
//            @Override
//            public void onChange(Object o) {
//                RealmResults results = (RealmResults) o;
//                successProgressText.setText(results.size()+ "개 Success");
//                failProgressText.setText(workData.size()-results.size()-1+ "개 Fail");
//            }
//        };
//        delivery.addChangeListener(callback);
        deliveryHelper.setAllDeliveryList(workData);
    }

    /**
     * 로그이 업무 시작하기 버튼을 누를 때, 초기값을 세팅한 후 메인 페이지로 이동합니다.
     * @param v
     */
    public void goMainPage(View v){
        //SharedPreferences의 초기값을 세팅합니다.
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        prefs.edit().putBoolean("isFirstRun",false).apply();        //앱 최초 실행 값을 false로 변경하여, 다음 접속시 바로 MainActivity 가 열리도록 함.
        prefs.edit().putBoolean("isSpeechAPI",true).apply();        //Google Speech API 사용 설정을 true로 함.
        prefs.edit().putBoolean("isAwarenessAPI",true).apply();     //Awareness API 를 이용한 위치 감지를 true로 함.
        prefs.edit().putBoolean("isPresentation",false).apply();    //Presentation 모드를 false로 하여, 블루투스를 이용하도록 함.
        prefs.edit().putBoolean("isSMS",true).apply();              //문자 전송 기능을 true로 함.

        ManagerHelper managerHelper = new ManagerHelper(this);
        managerHelper.setManager(editTextManager.getText().toString(),deliveryHelper.getFirstShippingInfo());
        Toast.makeText(this,editTextManager.getText().toString() + "님 환영합니다.",Toast.LENGTH_SHORT).show();
        Intent newIntent = new Intent(this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }

    //안드로이드 네이티브 기능 중, 사용자의 권한을 우선 획득해야 하는 경우 사용합니다.
    public boolean checkPermission(){
        boolean result = false;
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SignInActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE}, 1);
        }else{
            result = true;
        }
        return result;
    }
}
