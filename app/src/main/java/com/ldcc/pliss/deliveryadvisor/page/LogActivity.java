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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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


        for(int i = 0 ; i<results.size();i++){
            mLogFragment.getLogView().println(results.get(i).getCONTENTS(),2);
            mLogFragment.getLogView().println(results.get(i).getTIME_STAMP(),1);
        }

        setListener();
    }

    private void setListener(){
        //데이터베이스에서 현재 업무가 변경되었을 때, 이를 감지하여 [현재 업무 화면, 진행 프로그레스바, 전체 업무 화면] 내용을 변경해준다.

        logDataChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                try{
                    mLogFragment.getLogView().println(results.get(results.size()-1).getCONTENTS(),2);
                    mLogFragment.getLogView().println(results.get(results.size()-1).getTIME_STAMP(),1);
                }catch(Exception e){
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent newIntent = new Intent(this, SettingActivity.class);
            startActivity(newIntent);
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
            Intent newIntent = new Intent(this, MainActivity.class);
            startActivity(newIntent);
            finish();
        } else if (id == R.id.nav_navigation) {
            Toast.makeText(LogActivity.this, "경로를 확인합니다.", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, NavigationActivity.class);
            startActivity(newIntent);
            finish();
        } else if (id == R.id.nav_logs) {
            Toast.makeText(LogActivity.this, "로그 기록을 확인합니다.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(LogActivity.this, "환경 설정", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, SettingActivity.class);
            startActivity(newIntent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.log_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
