package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.util.CsvUtil;

import org.w3c.dom.Text;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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
        editTextManager     = (EditText) findViewById(R.id.edit_text_manager);
        deliveryHelper      = new DeliveryHelper(this);

        CsvUtil csvUtil = new CsvUtil();
        workData = csvUtil.readCSV(this);
        deliveryHelper.setAllDeliveryList(workData);

        successProgressText.setText("20개");
        failProgressText.setText("0개");
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
