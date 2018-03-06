package com.ldcc.pliss.deliveryadvisor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.adapter.AllWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.adapter.CurrentWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.page.HomeActivity;
import com.ldcc.pliss.deliveryadvisor.page.LogActivity;
import com.ldcc.pliss.deliveryadvisor.page.NavigationActivity;
import com.ldcc.pliss.deliveryadvisor.page.SettingActivity;
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

import org.w3c.dom.Text;

import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences prefs;
    private FloatingActionButton buttonSpeechRecognition;

    private ListView currentWorkListView;
    private ListAdapter currentWorkListAdapter;

    private ListView allWorkListView;
    private ListAdapter allWorkListAdapter;

    private ProgressBar progressBarDelivery;
    private TextView progressTextDelivery;

    private DeliveryHelper deliveryHelper;
    private ManagerHelper managerHelper;

    private WorkUtil workUtil;

    private Button buttonShowDetails, buttonProcDelivery, buttonCallCustomer, buttonNaviPath;

    private String[] managerInfo = new String[6];
    private String[] invoice,
             customerName,
             customerProduct,
             customerAddress,
             status;

    private RealmResults<Delivery> results;
    private int deliveryDoneCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        boolean isFirstRun = checkFirstRun();

        if(isFirstRun)
            finish();
        else
            init();
    }

    private void init(){
        initSetting();
        setLayout();
    }

    private void initSetting(){
        workUtil = new WorkUtil();
        deliveryHelper = new DeliveryHelper(this);
        deliveryHelper.showLogs();

        managerHelper = new ManagerHelper((this));
        managerInfo = managerHelper.getCurrentDeliveryInfo(this);

        results = deliveryHelper.getAllDeliveryList();
        invoice = new String[results.size()];
        customerName = new String[results.size()];
        customerProduct = new String[results.size()];
        customerAddress = new String[results.size()];
        status = new String[results.size()];

        deliveryDoneCount = 0;
        for(int i = 0 ; i<results.size() ; i++){
            invoice[i] = results.get(i).getINV_NUMB();
            customerName[i] = results.get(i).getRECV_NM();
            customerProduct[i] = results.get(i).getITEM_NM();
            customerAddress[i] = results.get(i).getRECV_ADDR();
            status[i] = results.get(i).getSHIP_STAT();

            if (status[i].equals("C"))
                deliveryDoneCount++;
        }
    }

    private void setLayout(){


        //우측 하단의 음성인식 버튼
        buttonSpeechRecognition = (FloatingActionButton) findViewById(R.id.fab);

        //상단 툴바 세팅
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_setting);
        getSupportActionBar().setTitle(null);

        //상단 툴바 왼쪽의 메뉴 버튼 클릭했을 때 등장하는 뷰 세팅
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //상단 현재 업무 뷰 세팅
        currentWorkListView = (ListView) findViewById(R.id.currentWorkList);
        buttonShowDetails = (Button) findViewById(R.id.button_show_details);
        buttonProcDelivery = (Button) findViewById(R.id.button_proc_delivery);
        buttonCallCustomer= (Button) findViewById(R.id.button_call_customer);
        buttonNaviPath = (Button) findViewById(R.id.button_navi_path);

        currentWorkListAdapter = new CurrentWorkListAdapter(MainActivity.this, managerInfo);
        currentWorkListView.setAdapter(currentWorkListAdapter);

        //상태 진행 바 세팅
        progressBarDelivery = (ProgressBar) findViewById(R.id.progress_bar_delivery);
        progressTextDelivery = (TextView) findViewById(R.id.progress_text_delivery);
        progressBarDelivery.setMax(results.size());
        progressBarDelivery.setProgress(deliveryDoneCount);
        progressTextDelivery.setText("할당된 배송 리스트 (" + deliveryDoneCount + "/" + results.size()+")");

        //하단 모든 업무 뷰 세팅

        allWorkListView = (ListView) findViewById(R.id.allWorkList);
        allWorkListAdapter = new AllWorkListAdapter(invoice,customerName, customerProduct,customerAddress,status);
        allWorkListView.setAdapter(allWorkListAdapter);

        setListener();

    }

    private void setHighlight(int position, boolean on){
        int firstPosition = allWorkListView.getFirstVisiblePosition();
        int wantedPosition = position-firstPosition;
//        if(wantedPosition < 0 || wantedPosition>=allWorkListView.getChildCount() ){
//            Log.d("position값",position+"");
//            Log.d("wantedPosition값",wantedPosition+"");
//            Log.d("allWorkListView의 자식 값",allWorkListView.getChildCount()+"");
//            return;
//        }
        View childView = allWorkListView.getChildAt(wantedPosition);
        if(childView == null){
            Log.d("값","안들어옴");
            return;
        }

    }

    private void setListener(){



        buttonSpeechRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "무엇을 도와 드릴까요? ^^", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        currentWorkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, managerInfo[i], Toast.LENGTH_SHORT).show();
            }
        });

        allWorkListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItem = (String) parent.getItemAtPosition(position);
                int h1 = parent.getHeight();
                int h2 = view.getHeight();
                allWorkListView.setSelectionFromTop(position, h1/2 -h2/2);
                //allWorkListView.notifyAll();
                //tv.setText("Your selected fruit is : " + selectedItem);
                return true;
            }

        });

        buttonShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonProcDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonCallCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.callTheCustomer(v.getContext(),managerInfo[4]);
            }
        });

        buttonNaviPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

        if (id == R.id.nav_main) {
            Toast.makeText(MainActivity.this, "업무 내용을 확인합니다.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_navigation) {
            Toast.makeText(MainActivity.this, "경로를 확인합니다.", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, NavigationActivity.class);
            startActivity(newIntent);
        } else if (id == R.id.nav_logs) {
            Toast.makeText(MainActivity.this, "로그 기록을 확인합니다.", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, LogActivity.class);
            startActivity(newIntent);

        } else if (id == R.id.nav_settings) {
            Toast.makeText(MainActivity.this, "환경 설정", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, SettingActivity.class);
            startActivity(newIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
    }

    private boolean checkFirstRun(){
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun) {
            Intent newIntent = new Intent(this, HomeActivity.class);
            startActivity(newIntent);

            prefs.edit().putBoolean("isFirstRun",false).apply();
        }
        return isFirstRun;
    }
}