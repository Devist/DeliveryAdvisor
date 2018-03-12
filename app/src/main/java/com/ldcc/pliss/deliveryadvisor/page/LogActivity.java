package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.logger.LogFragment;

public class LogActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private LogFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        init();
    }

    private void init(){
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
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
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
