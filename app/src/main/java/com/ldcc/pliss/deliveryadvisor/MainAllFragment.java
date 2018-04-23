package com.ldcc.pliss.deliveryadvisor;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.ldcc.pliss.deliveryadvisor.databases.Manager;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;
import com.ldcc.pliss.deliveryadvisor.page.DetailInfoDialog;
import com.ldcc.pliss.deliveryadvisor.page.NavigationActivity;
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class MainAllFragment extends Fragment {

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
    private String[] invoice, customerName, customerProduct, customerAddress, status, how;

    private RealmResults<Delivery> results;
    private int deliveryDoneCount;
    private Realm mRealm;
    private Manager ddd;

    private View view;

    private ProgressBar mprogressBar;   //동작하지 않으나, 쓰레드의 빠른 반환을 위해 사용
    public static Activity fa;          //세팅에서 초기화시, 남아있는 메인액티비티에서 Realm 디비 변화를 감지하여 처리하려다 에러나지 않도록 처리하는 변수


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        container.removeAllViews();
        view = inflater.inflate(R.layout.fragment_two, null);
        init();
        return view;

    }

    private void init(){

        fa = this.getActivity();
        workUtil = new WorkUtil();

        //데이터베이스 세팅
        Realm.init(this.getContext());
        mRealm = Realm.getDefaultInstance();
        deliveryHelper = new DeliveryHelper(this.getContext());
        managerHelper = new ManagerHelper((this.getContext()));

        changeWorkData();

        setLayout();
    }

    private void setLayout(){



        //상태 진행 바 세팅
        progressBarDelivery = (ProgressBar) view.findViewById(R.id.progress_bar_delivery);
        progressTextDelivery = (TextView) view.findViewById(R.id.progress_text_delivery);
        progressBarDelivery.setMax(results.size());

        //        //하단 모든 업무 뷰 세팅
        allWorkListView = (ListView) view.findViewById(R.id.allWorkList);
        status[deliveryDoneCount]="O";
        allWorkListAdapter = new AllWorkListAdapter(invoice,customerName, customerProduct,customerAddress,status,how);
        allWorkListView.setAdapter(allWorkListAdapter);
        allWorkListView.setSelection(deliveryDoneCount);
        allWorkListView.getViewTreeObserver().addOnGlobalLayoutListener(new MyGlobalListenerClass(deliveryDoneCount));

        setListener();
    }

    private void setListener(){


        ddd = mRealm.where(Manager.class).equalTo("userName",managerHelper.getManagerName()).findFirstAsync();
        workDataChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {

                try{
                    changeWorkData();
                  //  new Handler().postDelayed(new Runnable(){
                       // @Override
                        //public void run(){

                            progressTextDelivery.setText("총 " + results.size()+"개 중 "+deliveryDoneCount + "개 완료했어요.");
                            ObjectAnimator anim = ObjectAnimator.ofInt(progressBarDelivery, "progress", deliveryDoneCount);
                            anim.start();

                            allWorkListAdapter = new AllWorkListAdapter(invoice,customerName, customerProduct,customerAddress,status,how);
                            allWorkListView.setAdapter(allWorkListAdapter);
                            allWorkListView.setSelection(deliveryDoneCount);
                            allWorkListView.getViewTreeObserver().addOnGlobalLayoutListener(new MyGlobalListenerClass(deliveryDoneCount));
                        //}
                   // },1000);


                }catch(Exception e){
                    fa.finish();
                }
            }
        };
        ddd.addChangeListener(workDataChangeListener);

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
                final DetailInfoDialog detailInfoDialog = new DetailInfoDialog(getContext(), selectedInvoiceNumber);
                detailInfoDialog.show();

                // 1.상세정보 표시 팝업에서 "현재 목적지로 변경" 버튼을 눌렀을 때의 처리
                detailInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        // 3. 현재 처리해야 할 업무 변경
                        String addCategoryStr = detailInfoDialog.getAddCategoryStr();
                        if(addCategoryStr!=null)
                            deliveryHelper.changeManagerInfo(addCategoryStr);
                    }
                });
                return true;
            }

        });
    }


    private void changeWorkData() {
        managerInfo = managerHelper.getCurrentDeliveryInfoSimple();

        results = deliveryHelper.getAllDeliveryList();
        invoice = new String[results.size()];
        customerName = new String[results.size()];
        customerProduct = new String[results.size()];
        customerAddress = new String[results.size()];
        status = new String[results.size()];
        how = new String[results.size()];
        deliveryDoneCount = Integer.parseInt(managerInfo[6])-1;
        for(int i = 0 ; i<results.size() ; i++){
            invoice[i] = results.get(i).getINV_NUMB();
            customerName[i] = results.get(i).getRECV_NM();
            customerProduct[i] = results.get(i).getITEM_NM();
            customerAddress[i] = results.get(i).getRECV_ADDR();
            status[i] = results.get(i).getSHIP_STAT();
            how[i] = results.get(i).getSTAT_HOW();
        }
        status[deliveryDoneCount]="O";
    }


    //안드로이드 네이티브 기능 중, 사용자의 권한을 우선 획득해야 하는 경우 사용합니다.
    public boolean checkPermission(){
        boolean result = false;
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE}, 1);
        }else{
            result = true;
        }
        return result;
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
                ListView v = (ListView) view.findViewById(R.id.allWorkList);
                v.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
                int h1 = v.getHeight();
                int h2 = v.getMeasuredHeight();
                allWorkListView.setSelectionFromTop(pos, h1/2-h2/2);
                isAlreadyDone = true;
            }
        }
    }

}