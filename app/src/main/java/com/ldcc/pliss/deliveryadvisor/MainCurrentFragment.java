package com.ldcc.pliss.deliveryadvisor;

import android.Manifest;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.Manager;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

import io.realm.Realm;
import io.realm.RealmChangeListener;

import static android.content.Context.MODE_PRIVATE;

public class MainCurrentFragment extends Fragment {


    private ViewPager mViewPager;
    private WorkUtil workUtil;

    private Realm mRealm;
    private Manager ddd;


    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    private SharedPreferences prefs;                        // 앱이 최초 실행인지 확인하기 위한 변수

    private ManagerHelper managerHelper;
    private DeliveryHelper deliveryHelper;
    private RealmChangeListener workDataChangeListener;     // 데이터베이스 상태변화 감지 리스너

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one, null);

        Realm.init(getContext());
        mRealm = Realm.getDefaultInstance();
        prefs = view.getContext().getSharedPreferences("Pref", MODE_PRIVATE);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        deliveryHelper = new DeliveryHelper(this.getContext());
        managerHelper = new ManagerHelper((this.getContext()));

        if(checkFirstRun())
            return view;



        setListener();

        return view;

    }

    private void setListener(){
        //데이터베이스에서 현재 업무가 변경되었을 때, 이를 감지하여 [현재 업무 화면, 진행 프로그레스바, 전체 업무 화면] 내용을 변경해준다.
        ddd = mRealm.where(Manager.class).equalTo("userName",managerHelper.getManagerName()).findFirstAsync();
        workDataChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {

                try{
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            mCardAdapter = new CardPagerAdapter();
                            int order = managerHelper.getCurrentDeliveryInfoDetail().getSHIP_ID();
                            Log.d("처리",order+"");
                            Delivery prevInfo;
                            if(order>1){
                                prevInfo = deliveryHelper.getSearchedInfoFromOrder(order-1);
                                mCardAdapter.addCardItem(managerHelper.getSearchedInfoSimple(prevInfo),0);
                            }
                            Delivery nextInfo = deliveryHelper.getSearchedInfoFromOrder(order+1);

                            mCardAdapter.addCardItem(managerHelper.getCurrentDeliveryInfoSimple(),1);
                            mCardAdapter.addCardItem(managerHelper.getSearchedInfoSimple(nextInfo),2);

                            mViewPager.setAdapter(mCardAdapter);
                            mViewPager.setPageTransformer(false, mCardShadowTransformer);
                            mViewPager.setOffscreenPageLimit(3);


                            if(order<=1)
                                mViewPager.setCurrentItem(0);
                            else
                                mViewPager.setCurrentItem(1);
                        }
                    },500);

                }catch(Exception e){
                    getActivity().finish();
                }
            }
        };
        ddd.addChangeListener(workDataChangeListener);

    }

    //앱이 최초 실행인지 검출하여, 최초 페이지로 이동시키거나 메인 페이지에 머무릅니다.
    private boolean checkFirstRun(){
        return prefs.getBoolean("isFirstRun",true);
    }


}




//package com.ldcc.pliss.deliveryadvisor;
//
//import android.Manifest;
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.app.Fragment;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.NavigationView;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.ldcc.pliss.deliveryadvisor.adapter.AllWorkListAdapter;
//import com.ldcc.pliss.deliveryadvisor.adapter.CurrentWorkListAdapter;
//import com.ldcc.pliss.deliveryadvisor.advisor.AdvisorService;
//import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
//import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
//import com.ldcc.pliss.deliveryadvisor.databases.Manager;
//import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
//import com.ldcc.pliss.deliveryadvisor.page.DetailInfoDialog;
//import com.ldcc.pliss.deliveryadvisor.page.HomeActivity;
//import com.ldcc.pliss.deliveryadvisor.page.LogActivity;
//import com.ldcc.pliss.deliveryadvisor.page.NavigationActivity;
//import com.ldcc.pliss.deliveryadvisor.page.SettingActivity;
//import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;
//
//import io.realm.Realm;
//import io.realm.RealmChangeListener;
//import io.realm.RealmResults;
//
//import static android.content.Context.MODE_PRIVATE;
//
//
//public class MainCurrentFragment extends Fragment {
//
//    private SharedPreferences prefs;                        // 앱이 최초 실행인지 확인하기 위한 변수
//    private FloatingActionButton buttonSpeechRecognition;   // 음성 인식 활성화 버튼
//
//    private ListView currentWorkListView;                   // 상단 현재 업무 리스트 화면
//    private ListAdapter currentWorkListAdapter;             // 상단 현재 업무 리스트 어댑터
//
//    private ListView allWorkListView;                       // 하단 모든 업무 리스트 화면
//    private ListAdapter allWorkListAdapter;                 // 하단 모든 업무 리스트 어댑터
//
//    private ProgressBar progressBarDelivery;                // 중단 모든업무 대비 업무 완료 진행 표시바
//    private TextView progressTextDelivery;                  // 중단 모든업무 대비 업무 완료 진행 텍스트
//
//    private DeliveryHelper deliveryHelper;                  // 업무 데이터베이스 처리 객체
//    private ManagerHelper managerHelper;                    // 현재 업무 데이터베이스 처리 객체
//    private RealmChangeListener workDataChangeListener;     // 데이터베이스 상태변화 감지 리스너
//
//    private WorkUtil workUtil;                              // 업무 처리(전화,문자,어드바이저활성,배송처리 등) 객체
//
//    //상단 현재 업무 리스트에 존재하는 버튼들
//    private Button buttonShowDetails, buttonProcDelivery, buttonCallCustomer, buttonNaviPath;
//
//    //현재 처리해야 하는 업무 정보를 담고 있는 배열
//    private String[] managerInfo = new String[7];
//
//    //모든 업무 정보를 담고 있는 배열
//    private String[] invoice, customerName, customerProduct, customerAddress, status;
//
//    private RealmResults<Delivery> results;
//    private int deliveryDoneCount;
//    private Realm mRealm;
//    private Manager ddd;
//
//
//    private ProgressBar mprogressBar;   //동작하지 않으나, 쓰레드의 빠른 반환을 위해 사용
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    View view;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        view = inflater.inflate(R.layout.fragment_one,null);
//
//        if(!checkFirstRun())             //앱이 최초 실행인지 확인
//            init();
//
//        return  view;
//
//    }
//
//    //앱이 최초 실행인지 검출하여, 최초 페이지로 이동시키거나 메인 페이지에 머무릅니다.
//    private boolean checkFirstRun(){
//        prefs = view.getContext().getSharedPreferences("Pref", MODE_PRIVATE);
//        return prefs.getBoolean("isFirstRun",true);
//    }
//
//    private void init(){
//        workUtil = new WorkUtil();
//
//        //데이터베이스 세팅
//        Realm.init(this.getContext());
//        mRealm = Realm.getDefaultInstance();
//        deliveryHelper = new DeliveryHelper(this.getContext());
//        managerHelper = new ManagerHelper((this.getContext()));
//
//        changeWorkData();
//
//        setLayout();
//
////        mCardAdapter = new CardPagerAdapter();
////        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
////        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1));
////        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1));
//    }
//
//    private void setLayout(){
//
//        //상단 현재 업무 뷰 세팅
//        currentWorkListView = (ListView) view.findViewById(R.id.currentWorkList);
//        buttonShowDetails = (Button) view.findViewById(R.id.button_show_details);
//        buttonProcDelivery = (Button) view.findViewById(R.id.button_proc_delivery);
//        buttonCallCustomer= (Button) view.findViewById(R.id.button_call_customer);
//        buttonNaviPath = (Button) view.findViewById(R.id.button_navi_path);
//        currentWorkListAdapter = new CurrentWorkListAdapter(view, managerInfo);
//        currentWorkListView.setAdapter(currentWorkListAdapter);
//
//        //상태 진행 바 세팅
//        progressBarDelivery = (ProgressBar) view.findViewById(R.id.progress_bar_delivery);
//        progressTextDelivery = (TextView) view.findViewById(R.id.progress_text_delivery);
//        progressBarDelivery.setMax(results.size());
//        progressBarDelivery.setProgress(deliveryDoneCount);
//        progressTextDelivery.setText("업무 리스트 (" + deliveryDoneCount + "/" + results.size()+")");
//
//        setListener();
//    }
//
//    private void setListener(){
//
//
//        //현재업무 리스트 뷰에서 "배송 처리" 버튼 클릭 시 처리
//        buttonProcDelivery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean isCallandMessagePossible = checkPermission();
//                if(isCallandMessagePossible) {
//                    workUtil.showProcessDeliveryDialog(v.getContext(),managerInfo);
//                }else{
//                    Toast.makeText(getContext(), "권한이 부족하여 실행하지 못했습니다.", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//        //현재업무 리스트 뷰에서 "전화 연결" 버튼 클릭 시 처리
//        buttonCallCustomer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean isCallandMessagePossible = checkPermission();
//                if(isCallandMessagePossible) {
//                    workUtil.callTheCustomer(v.getContext(), managerInfo[4]);
//                    workUtil.sendSMS(getContext(),managerInfo[4],"배달원이 상품 배송을 진행하기 위해 전화 걸었습니다.");
//                }else{
//                    Toast.makeText(getContext(), "앱 설정에서 전화 권한 허용을 클릭해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        //현재업무 리스트 뷰에서 "길 안내" 버튼 클릭 시 처리
//        buttonNaviPath.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                boolean isCallandMessagePossible = checkPermission();
//
//                if(isCallandMessagePossible) {
//                    startActivity(new Intent(getContext(), NavigationActivity.class));
//                }else{
//                    Toast.makeText(getContext(), "위치를 허용하지 않았을 경우, 앱 설정에서 위치 권한 허용을 클릭해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//    }
//
//
//    private void changeWorkData() {
//        managerInfo = managerHelper.getCurrentDeliveryInfoSimple();
//
//        results = deliveryHelper.getAllDeliveryList();
//        invoice = new String[results.size()];
//        customerName = new String[results.size()];
//        customerProduct = new String[results.size()];
//        customerAddress = new String[results.size()];
//        status = new String[results.size()];
//
//        deliveryDoneCount = Integer.parseInt(managerInfo[6])-1;
//        for(int i = 0 ; i<results.size() ; i++){
//            invoice[i] = results.get(i).getINV_NUMB();
//            customerName[i] = results.get(i).getRECV_NM();
//            customerProduct[i] = results.get(i).getITEM_NM();
//            customerAddress[i] = results.get(i).getRECV_ADDR();
//            status[i] = results.get(i).getSHIP_STAT();
//        }
//        status[deliveryDoneCount]="O";
//    }
//
//
//    //안드로이드 네이티브 기능 중, 사용자의 권한을 우선 획득해야 하는 경우 사용합니다.
//    public boolean checkPermission(){
//        boolean result = false;
//        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE}, 1);
//        }else{
//            result = true;
//        }
//        return result;
//    }
//
//}