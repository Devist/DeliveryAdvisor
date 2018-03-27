package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private TextView successProgressText;
    private TextView failProgressText;
    private EditText editTextManager;
    private DeliveryHelper deliveryHelper;
    private SharedPreferences prefs;                        // 앱이 최초 실행인지 확인하기 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign);

        init();
    }

    private void init(){
        successProgressText = (TextView) findViewById(R.id.textSuccessProgress);
        failProgressText    = (TextView) findViewById(R.id.textFailProgress);
        editTextManager     = (EditText) findViewById(R.id.edit_text_manager);
        deliveryHelper      = new DeliveryHelper(this);
        TextView tvView = (TextView) findViewById(R.id.sign_title);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.sign_ll);
        LinearLayout signUserLayout = (LinearLayout)findViewById(R.id.sign_user_ll);
        Animation animationLtoR = AnimationUtils.loadAnimation(this, R.anim.from_lx_to_rx);
        Animation animationRtoL = AnimationUtils.loadAnimation(this, R.anim.from_rx_to_lx);
        Animation animationTtoB = AnimationUtils.loadAnimation(this, R.anim.from_ty_to_by);
        linearLayout.startAnimation(animationLtoR);
        tvView.startAnimation(animationTtoB);
        signUserLayout.startAnimation(animationRtoL);


        CsvUtil csvUtil = new CsvUtil();
        workData = csvUtil.readCSV(this);

        Realm.init(this);
        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<Delivery> delivery = mRealm.where(Delivery.class).findAll();
        RealmChangeListener callback = new RealmChangeListener() {
            @Override
            public void onChange(Object o) { // called once the query complete and on every update
                Log.d("오브젝트",o+"");
                RealmResults<Delivery> results = (RealmResults) o;
                // use the result
                successProgressText.setText(results.size()+ "개 Success");
                failProgressText.setText(workData.size()-results.size()-1+ "개 Fail");
            }
        };
        delivery.addChangeListener(callback);
        deliveryHelper.setAllDeliveryList(workData);



    }

    public void goMainPage(View v){
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        prefs.edit().putBoolean("isFirstRun",false).apply();
        prefs.edit().putBoolean("isSpeechAPI",true).apply();
        prefs.edit().putBoolean("isAwarenessAPI",true).apply();
        prefs.edit().putBoolean("isPresentation",false).apply();
        prefs.edit().putBoolean("isSMS",true).apply();

        ManagerHelper managerHelper = new ManagerHelper(this);
        managerHelper.setManager(editTextManager.getText().toString(),deliveryHelper.getLastYetDelivery());
        Toast.makeText(this,editTextManager.getText().toString() + "님 환영합니다.",Toast.LENGTH_SHORT).show();
        Intent newIntent = new Intent(this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }
}
