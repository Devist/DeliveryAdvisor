package com.ldcc.pliss.deliveryadvisor;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.adapter.AllWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.adapter.CurrentWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.advisor.AdvisorService;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.Manager;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.page.DetailInfoDialog;
import com.ldcc.pliss.deliveryadvisor.page.HomeActivity;
import com.ldcc.pliss.deliveryadvisor.page.LogActivity;
import com.ldcc.pliss.deliveryadvisor.page.NavigationActivity;
import com.ldcc.pliss.deliveryadvisor.page.SettingActivity;
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

import io.realm.Realm;
import io.realm.RealmChangeListener;
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
    private RealmChangeListener workDataChangeListener;
    private String[] managerInfo = new String[7];
    private String[] invoice,
            customerName,
            customerProduct,
            customerAddress,
            status;

    private RealmResults<Delivery> results;
    private int deliveryDoneCount;
    private Realm mRealm;
    private Manager ddd;

    //세팅에서 초기화시, 남아있는 메인액티비티에서 Realm 디비 변화를 감지하여 처리하려다 에러나지 않도록 처리하는 변수
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fa = this;
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        boolean isFirstRun = checkFirstRun();

        if(isFirstRun)
            finish();
        else
            init();
    }

    private void init(){
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        initSetting();
        setLayout();
        startService(new Intent(this, AdvisorService.class));
    }

    private void initSetting(){
        changeWorkData();
        workUtil = new WorkUtil();
    }

    private void changeWorkData() {
        deliveryHelper = new DeliveryHelper(this);

        managerHelper = new ManagerHelper((this));
        managerInfo = managerHelper.getCurrentDeliveryInfo(MainActivity.this);

        results = deliveryHelper.getAllDeliveryList();
        invoice = new String[results.size()];
        customerName = new String[results.size()];
        customerProduct = new String[results.size()];
        customerAddress = new String[results.size()];
        status = new String[results.size()];

        deliveryDoneCount = Integer.parseInt(managerInfo[6])-1;
        for(int i = 0 ; i<results.size() ; i++){
            invoice[i] = results.get(i).getINV_NUMB();
            customerName[i] = results.get(i).getRECV_NM();
            customerProduct[i] = results.get(i).getITEM_NM();
            customerAddress[i] = results.get(i).getRECV_ADDR();
            status[i] = results.get(i).getSHIP_STAT();
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
        status[deliveryDoneCount]="O";
        allWorkListAdapter = new AllWorkListAdapter(invoice,customerName, customerProduct,customerAddress,status,deliveryDoneCount);
        allWorkListView.setAdapter(allWorkListAdapter);
        allWorkListView.setSelection(deliveryDoneCount);
        allWorkListView.getViewTreeObserver().addOnGlobalLayoutListener(new MyGlobalListenerClass(deliveryDoneCount));

        setListener();
    }

    private void setListener(){

        //음성인식 버튼을 클릭했을 때의 처리
        buttonSpeechRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "무엇을 도와 드릴까요? ^^", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        allWorkListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView temp;

                if(position==deliveryDoneCount){
                    temp = (TextView) view.findViewById(R.id.textWorkTitle_ongoing);
                }else{
                    temp = (TextView) view.findViewById(R.id.textWorkTitle);
                }

                String selectedInvoiceNumber = temp.getText().toString().split(":")[1].substring(1);    //송장번호를 잘라서 가져옴.

                final DetailInfoDialog detailInfoDialog = new DetailInfoDialog(MainActivity.this, selectedInvoiceNumber);
                detailInfoDialog.show();

                //다이얼로그에서 "현재 목적지로 변경" 버튼을 눌렀을 때의 처리
                detailInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String addCategoryStr = detailInfoDialog.getAddCategoryStr();
                        if(addCategoryStr!=null)
                            deliveryHelper.changeManagerInfo(addCategoryStr);
                    }
                });
                return true;
            }

        });

        //현재업무 리스트 뷰에서 "상세 정보" 버튼 클릭 시 처리
        buttonShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DetailInfoDialog detailInfoDialog = new DetailInfoDialog(MainActivity.this,managerInfo[2]);
                detailInfoDialog.show();
            }
        });

        //현재업무 리스트 뷰에서 "배송 처리" 버튼 클릭 시 처리
        buttonProcDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.showProcessDeliveryDialog(v.getContext(),managerInfo);
            }
        });

        //현재업무 리스트 뷰에서 "전화 연결" 버튼 클릭 시 처리
        buttonCallCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCallandMessagePossible = checkPermission();
                if(isCallandMessagePossible) {
                    workUtil.callTheCustomer(v.getContext(), managerInfo[4]);
                    workUtil.sendSMS(MainActivity.this,managerInfo[4],"배달원이 상품 배송을 진행하기 위해 전화 걸었습니다.");
                }
            }
        });

        //현재업무 리스트 뷰에서 "길 안내" 버튼 클릭 시 처리
        buttonNaviPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "위치를 허용하지 않았을 경우, 앱 설정에서 위치 권한 허용을 클릭해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(newIntent);
            }
        });

        ddd = mRealm.where(Manager.class).equalTo("userName",managerHelper.getManagerName()).findFirstAsync();
        workDataChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {

                Log.d("Realm", "개를 찾았거나 갱신됐습니다!");
                try{
                    changeWorkData();
                    currentWorkListAdapter = new CurrentWorkListAdapter(MainActivity.this, managerInfo);
                    currentWorkListView.setAdapter(currentWorkListAdapter);

                    progressBarDelivery.setProgress(deliveryDoneCount);
                    progressTextDelivery.setText("할당된 배송 리스트 (" + deliveryDoneCount + "/" + results.size()+")");

                    status[deliveryDoneCount]="O";
                    allWorkListAdapter = new AllWorkListAdapter(invoice,customerName, customerProduct,customerAddress,status,deliveryDoneCount);
                    allWorkListView.setAdapter(allWorkListAdapter);
                    allWorkListView.setSelection(deliveryDoneCount);
                    allWorkListView.getViewTreeObserver().addOnGlobalLayoutListener(new MyGlobalListenerClass(deliveryDoneCount));
                }catch(Exception e){
                    finish();
                }
            }
        };
        ddd.addChangeListener(workDataChangeListener);

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

    //왼쪽에 숨겨져 있는 네비게이션 메뉴 중 특정 버튼을 누르면, 해당 페이지로 이동시킵니다.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            Toast.makeText(MainActivity.this, "업무 내용을 확인합니다.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_navigation) {
            Toast.makeText(MainActivity.this, "위치를 허용하지 않았을 경우, 앱 설정에서 위치 권한 허용을 클릭해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, NavigationActivity.class);
            startActivity(newIntent);
        } else if (id == R.id.nav_logs) {
            Toast.makeText(MainActivity.this, "로그 기록을 확인합니다.", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, LogActivity.class);
            startActivity(newIntent);

        } else if (id == R.id.nav_settings) {
            Intent newIntent = new Intent(this, SettingActivity.class);
            startActivity(newIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    //안드로이드 네이티브 기능 중, 사용자의 권한을 우선 획득해야 하는 경우 사용합니다.
    public boolean checkPermission(){
        boolean result = false;
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE}, 1);
        }else{
            result = true;
        }
        return result;
    }

    //앱이 최초 실행인지 검출하여, 최초 페이지로 이동시키거나 메인 페이지에 머무릅니다.
    private boolean checkFirstRun(){
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun) {
            Intent newIntent = new Intent(this, HomeActivity.class);
            startActivity(newIntent);

            prefs.edit().putBoolean("isFirstRun",false).apply();
        }
        return isFirstRun;
    }

    //앱 실행시 업무 리스트뷰에 현재 업무를 중앙에 표시하기 위해 사용됩니다.
    class MyGlobalListenerClass implements ViewTreeObserver.OnGlobalLayoutListener {

        int pos;
        boolean isAlreadyDone = false;
        public MyGlobalListenerClass(int deliveryDoneCount) {
            pos = deliveryDoneCount;
        }

        @Override
        public void onGlobalLayout() {
            if(!isAlreadyDone){
                ListView v = (ListView) findViewById(R.id.allWorkList);

                int h1 = v.getHeight();
                v.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
                int h2 = v.getMeasuredHeight();
                allWorkListView.setSelectionFromTop(pos, h1/2-h2/2);
                isAlreadyDone = true;
            }
        }
    }

}