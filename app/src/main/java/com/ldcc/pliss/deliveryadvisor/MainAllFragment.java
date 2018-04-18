package com.ldcc.pliss.deliveryadvisor;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private String[] invoice, customerName, customerProduct, customerAddress, status;

    private RealmResults<Delivery> results;
    private int deliveryDoneCount;
    private Realm mRealm;
    private Manager ddd;


    private ProgressBar mprogressBar;   //동작하지 않으나, 쓰레드의 빠른 반환을 위해 사용
    public static Activity fa;          //세팅에서 초기화시, 남아있는 메인액티비티에서 Realm 디비 변화를 감지하여 처리하려다 에러나지 않도록 처리하는 변수


    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("모드","진입");
        container.removeAllViews();
        return inflater.inflate(R.layout.fragment_two, container, false);

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
        progressBarDelivery.setProgress(deliveryDoneCount);
        progressTextDelivery.setText("업무 리스트 (" + deliveryDoneCount + "/" + results.size()+")");

        setListener();
    }

    private void setListener(){


    }


    private void changeWorkData() {
        managerInfo = managerHelper.getCurrentDeliveryInfoSimple();

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

}