package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;

import java.io.File;

public class SettingActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    // UI 위젯
    private ToggleButton toggleSpeechBtn;
    private ToggleButton toggleAwarenessBtn;
    private ToggleButton togglePresentationBtn;
    private ToggleButton toggleSMSBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initSetting();
    }

    private void initSetting(){

        prefs= getSharedPreferences("Pref", MODE_PRIVATE);
        setLayout();
    }

    private void setLayout(){

        toggleSpeechBtn         = (ToggleButton)findViewById(R.id.toggle_speech_api);
        toggleAwarenessBtn      = (ToggleButton) findViewById(R.id.toggle_awareness);
        togglePresentationBtn   = (ToggleButton) findViewById(R.id.toggle_hi_advisor);
        toggleSMSBtn            = (ToggleButton) findViewById(R.id.toggle_sms);

        toggleSpeechBtn.setChecked(prefs.getBoolean("isSpeechAPI",true));
        toggleAwarenessBtn.setChecked(prefs.getBoolean("isAwarenessAPI",true));
        togglePresentationBtn.setChecked(prefs.getBoolean("isPresentation",false));
        toggleSMSBtn.setChecked(prefs.getBoolean("isSMS",true));

        setListener();
    }

    private void setListener(){

        toggleSpeechBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefs.edit().putBoolean("isSpeechAPI",true).apply();
                }
                else{
                    prefs.edit().putBoolean("isSpeechAPI",false).apply();
                }
            }
        });

        toggleAwarenessBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefs.edit().putBoolean("isAwarenessAPI",true).apply();
                }
                else{
                    prefs.edit().putBoolean("isAwarenessAPI",false).apply();
                }
            }
        });

        togglePresentationBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefs.edit().putBoolean("isPresentation",true).apply();
                }
                else{
                    prefs.edit().putBoolean("isPresentation",false).apply();
                }
            }
        });

        toggleSMSBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefs.edit().putBoolean("isSMS",true).apply();
                }
                else{
                    prefs.edit().putBoolean("isSMS",false).apply();
                }
            }
        });
    }

    public void showDialogInit(View v){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("데이터 초기화 및 종료")
                .setMessage("데이터를 초기화하고 앱을 종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 프로그램을 종료한다
                                initData();
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();  // 다이얼로그 생성
        alertDialog.show();                                     // 다이얼로그 보여주기
    }

    /**
     * 앱 캐시 지우기
     * @param context
     */
    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                //다운로드 파일은 지우지 않도록 설정
                //if(s.equals("lib") || s.equals("files")) continue;
                deleteDir(new File(appDir, s));
                Log.d("test", "File /data/data/"+context.getPackageName()+"/" + s + " DELETED");
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void initData(){
        MainActivity.fa.finish();
        clearApplicationData(getApplicationContext());
        DeliveryHelper deliveryHelper = new DeliveryHelper(this);
        deliveryHelper.deleteAllList();
        prefs.edit().clear().apply();
        System.exit(0);
    }

    public void onClickBackButton(View v){finish();}

}
