package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;


import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.adapter.AllWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.adapter.CurrentWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.databases.AppLogs;
import com.ldcc.pliss.deliveryadvisor.databases.AppLogsHelper;
import com.ldcc.pliss.deliveryadvisor.databases.Manager;
import com.ldcc.pliss.deliveryadvisor.logger.LogFragment;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class LogActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private LogFragment mLogFragment;
    private Realm mRealm;
    private AppLogsHelper logsHelper= new AppLogsHelper(this);
    private RealmResults<AppLogs> results = logsHelper.getLogs();
    private RealmChangeListener logDataChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        init();
    }

    private void init(){
        //데이터베이스 세팅
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

        setLayout();
    }

    private void setLayout(){
        //상단 툴바 세팅
        Toolbar toolbar = (Toolbar) findViewById(R.id.log_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_setting);
        getSupportActionBar().setTitle(null);

        //상단 툴바 왼쪽의 메뉴 버튼 클릭했을 때 등장하는 뷰 세팅
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.log_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.log_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLogFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);

        //기록된 모든 Log 를 화면에 표시합니다.
        for(int i = 0 ; i<results.size();i++){
            //첫번째 줄은 시간을 표시하도록, 두번째 줄은 로그 내용을 표시하도록 합니다.
            mLogFragment.getLogView().println(results.get(i).getCONTENTS(),2);
            mLogFragment.getLogView().println(results.get(i).getTIME_STAMP(),1);
        }

        setListener();
    }

    private void setListener(){
        // 쌓이는 로그들을 실시간으로 표시할 수 있도록, 즉 로그 페이지에서 바로 반영하여 확인할 수 있도록,
        // 데이터베이스 로그 테이블에 데이터가 추가되었을 때 이를 감지하여 로그 페이지 표시해 줍니다.
        logDataChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                try{
                    mLogFragment.getLogView().println(results.get(results.size()-1).getCONTENTS(),2);
                    mLogFragment.getLogView().println(results.get(results.size()-1).getTIME_STAMP(),1);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
        results.addChangeListener(logDataChangeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.log_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            Toast.makeText(LogActivity.this, "업무 내용을 확인합니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_navigation) {
            Toast.makeText(LogActivity.this, "경로를 확인합니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(LogActivity.this, "환경 설정", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.log_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
