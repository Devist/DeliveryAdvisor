package com.ldcc.pliss.deliveryadvisor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.adapter.AllWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.adapter.CurrentWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.page.HomeActivity;
import com.ldcc.pliss.deliveryadvisor.page.LogActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences prefs;
    private FloatingActionButton buttonSpeechRecognition;

    private ListView currentWorkListView;
    private ListAdapter currentWorkListAdapter;

    private ListView allWorkListView;
    private ListAdapter allWorkListAdapter;

    //임시 데이터
    int[] images = {R.drawable.icon_customer_name, R.drawable.icon_product, R.drawable.icon_invoice_number, R.drawable.icon_target_location, R.drawable.icon_phone, R.drawable.icon_customer_message};
    String[] infos = {"홍길동", "보이저 5200", "B102938529298", "서울시 금천구 서부샛길 528 가산미소지움오피스텔 645호", "010-5875-3636", "배송시 꼭 문 앞에 두어주세요."};

    String[] invoice = {"329847289","329847289","329847289","329847289","329847289","329847289","329847289"};
    String[] customerName = {"홍길동","정경진","이준희","박장식","전슬기","오동환","김광훈"};
    String[] customerProduct = {"보이저 5200","2018 맥북 프로 레티나","선풀기 realm wanted","잘빠진 하루 초가을 우엉차","삼성 FAST CHARGE 충전기","모니터 50인치 좋은거","자전거 하이브리그 27단"};
    String[] customerAddress = {"서울시 금천구 서부샛길 528 가산미소지움 오피스텔 645호",
            "서울시 금천구 서부샛길 528 가산미소지움 오피스텔 645호",
            "서울시 금천구 서부샛길 528 가산미소지움 오피스텔 645호",
            "서울시 금천구 서부샛길 528 가산미소지움 오피스텔 645호",
            "서울시 금천구 서부샛길 528 가산미소지움 오피스텔 645호",
            "서울시 금천구 서부샛길 528 가산미소지움 오피스텔 645호",
            "서울시 금천구 서부샛길 528 가산미소지움 오피스텔 645호"};
    String[] status = {"onGoing","yet","done","yet","done","done","done"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        boolean isFirstRun = checkFirstRun();

        if(isFirstRun)
            finish();
        else
            initLayout();

    }

    private void initLayout(){
        buttonSpeechRecognition = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_setting);
        getSupportActionBar().setTitle(null);

        buttonSpeechRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "무엇을 도와 드릴까요? ^^", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        currentWorkListView = (ListView) findViewById(R.id.currentWorkList);
        currentWorkListAdapter = new CurrentWorkListAdapter(MainActivity.this, infos, images);
        currentWorkListView.setAdapter(currentWorkListAdapter);
        currentWorkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(MainActivity.this, infos[i], Toast.LENGTH_SHORT).show();

            }
        });

        allWorkListView = (ListView) findViewById(R.id.allWorkList);
        allWorkListAdapter = new AllWorkListAdapter(MainActivity.this, invoice, customerName,customerProduct,customerAddress,status);
        allWorkListView.setAdapter(allWorkListAdapter);
        allWorkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(MainActivity.this, infos[i], Toast.LENGTH_SHORT).show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private boolean checkFirstRun(){
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun)
        {
            Intent newIntent = new Intent(this, HomeActivity.class);
            startActivity(newIntent);

            prefs.edit().putBoolean("isFirstRun",false).apply();
        }
        return isFirstRun;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        if (id == R.id.nav_camera) {
            Toast.makeText(MainActivity.this, "good job", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, LogActivity.class);
            startActivity(newIntent);

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
