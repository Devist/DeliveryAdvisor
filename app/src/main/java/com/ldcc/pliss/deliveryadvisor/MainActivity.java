package com.ldcc.pliss.deliveryadvisor;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.adapter.AllWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.adapter.CurrentWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.advisor.AdvisorService;
import com.ldcc.pliss.deliveryadvisor.advisor.ProcessorNLP;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences prefs;                        // 앱이 최초 실행인지 확인하기 위한 변수
    private FloatingActionButton buttonSpeechRecognition;   // 음성 인식 활성화 버튼

    private ListView currentWorkListView;                   // 상단 현재 업무 리스트 화면
    private ListAdapter currentWorkListAdapter;             // 상단 현재 업무 리스트 어댑터

    private ListView allWorkListView;                       // 하단 모든 업무 리스트 화면
    private ListAdapter allWorkListAdapter;                 // 하단 모든 업무 리스트 어댑터

    private ProgressBar progressBarDelivery;                // 중단 모든업무 대비 업무 완료 진행 표시바
    private TextView progressTextDelivery;                  // 중단 모든업무 대비 업무 완료 진행 텍스트

    private DeliveryHelper deliveryHelper;                  // 업무 데이터베이스 처리 객체
    private ManagerHelper managerHelper;                    // 현재 업무 데이터베이스 처리 객체
    private RealmChangeListener workDataChangeListener;     // 데이터베이스 상태변화 감지 리스너

    private WorkUtil workUtil;                              // 업무 처리(전화,문자,어드바이저활성,배송처리 등) 객체

    //상단 현재 업무 리스트에 존재하는 버튼들
    private Button buttonShowDetails, buttonProcDelivery, buttonCallCustomer, buttonNaviPath;

    //현재 처리해야 하는 업무 정보를 담고 있는 배열
    private String[] managerInfo = new String[7];

    //모든 업무 정보를 담고 있는 배열
    private String[] invoice, customerName, customerProduct, customerAddress, status;

    private RealmResults<Delivery> results;
    private int deliveryDoneCount;
    private Realm mRealm;
    private Manager ddd;


    private ProgressBar mprogressBar;   //동작하지 않으나, 쓰레드의 빠른 반환을 위해 사용
    public static Activity fa;          //세팅에서 초기화시, 남아있는 메인액티비티에서 Realm 디비 변화를 감지하여 처리하려다 에러나지 않도록 처리하는 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkFirstRun())             //앱이 최초 실행인지 확인
            goHomePage();
        else                            //최초 실행 아닐 경우 [init() 초기세팅] => [setLayout() 화면세팅] => [setListener() 이벤트처리]
            init();
    }


    private void init(){

        fa = this;
        workUtil = new WorkUtil();

        //데이터베이스 세팅
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        deliveryHelper = new DeliveryHelper(this);
        managerHelper = new ManagerHelper((this));

        changeWorkData();

        //음성인식 서비스 활성
        startService(new Intent(this, AdvisorService.class));

        setLayout();
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
                Snackbar.make(view, "무엇을 도와 드릴까요? ^^ [전화,배송 처리,길 안내]",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
//                SpeechHelper speechHelper = new SpeechHelper(MainActivity.this);
//                speechHelper.startVoiceRecognition();
            }
        });

        // A. 모든 업무 리스트 화면 중 특정 아이템을 오래 클릭할 경우,
        allWorkListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                TextView temp;
                if(position==deliveryDoneCount){
                    temp = (TextView) view.findViewById(R.id.textWorkTitle_ongoing);
                }else{
                    temp = (TextView) view.findViewById(R.id.textWorkTitle);
                }

                // B.상세정보 표시 팝업 표출
                String selectedInvoiceNumber = temp.getText().toString().split(":")[1].substring(1);    //송장번호를 잘라서 가져옴.
                final DetailInfoDialog detailInfoDialog = new DetailInfoDialog(MainActivity.this, selectedInvoiceNumber);
                detailInfoDialog.show();

                // 1.상세정보 표시 팝업에서 "현재 목적지로 변경" 버튼을 눌렀을 때의 처리
                detailInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // 2.단순 쓰레드 처리
                        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
                        ObjectAnimator anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, 100);
                        anim.start();

                        // 3. 현재 처리해야 할 업무 변경
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
                }else{
                    Toast.makeText(MainActivity.this, "앱 설정에서 전화 권한 허용을 클릭해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //현재업무 리스트 뷰에서 "길 안내" 버튼 클릭 시 처리
        buttonNaviPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isCallandMessagePossible = checkPermission();
                if(isCallandMessagePossible) {
                    Intent newIntent = new Intent(MainActivity.this, NavigationActivity.class);
                    startActivity(newIntent);
                }else{
                    Toast.makeText(MainActivity.this, "위치를 허용하지 않았을 경우, 앱 설정에서 위치 권한 허용을 클릭해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //데이터베이스에서 현재 업무가 변경되었을 때, 이를 감지하여 [현재 업무 화면, 진행 프로그레스바, 전체 업무 화면] 내용을 변경해준다.
        ddd = mRealm.where(Manager.class).equalTo("userName",managerHelper.getManagerName()).findFirstAsync();
        workDataChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {

                try{
                    changeWorkData();
                    currentWorkListAdapter = new CurrentWorkListAdapter(MainActivity.this, managerInfo);
                    currentWorkListView.setAdapter(currentWorkListAdapter);

                    progressBarDelivery.setProgress(deliveryDoneCount);
                    progressTextDelivery.setText("할당된 배송 리스트 (" + deliveryDoneCount + "/" + results.size()+")");

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

    private void changeWorkData() {
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
        status[deliveryDoneCount]="O";
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
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        return prefs.getBoolean("isFirstRun",true);
    }

    private void goHomePage(){
        Intent newIntent = new Intent(this, HomeActivity.class);
        startActivity(newIntent);
        prefs.edit().putBoolean("isFirstRun",false).apply();
        finish();
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
                v.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
                int h1 = v.getHeight();
                int h2 = v.getMeasuredHeight();
                allWorkListView.setSelectionFromTop(pos, h1/2-h2/2);
                isAlreadyDone = true;
            }
        }
    }

    private void test(){
        ProcessorNLP processorNLP = new ProcessorNLP();  //테스트용. 추후 삭제
        String result;
        try{

            result = processorNLP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"송장번호 1 3 5 7 8 배송 처리해줘").get();

            Log.d("result222",result);
        }catch (InterruptedException e) {
            e.printStackTrace();
            result = "fail";
        } catch (ExecutionException e) {
            e.printStackTrace();
            result = "fail";
        }
        try{
            JSONObject flattenJson = new JSONObject(result);
            JSONArray abc = flattenJson.getJSONArray("token_strings");
            for(int i = 0 ;i <abc.length() ; i++){
                Log.d("토큰들 : ",abc.getString(i));
            }
        }catch(Exception e){

        }
    }
}