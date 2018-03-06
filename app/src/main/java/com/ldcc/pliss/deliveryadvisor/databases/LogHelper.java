package com.ldcc.pliss.deliveryadvisor.databases;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by pliss on 2018. 2. 27..
 */

public class LogHelper {

    private Realm mRealm;

    public LogHelper(Context context){
        Realm.init(context);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
    }

    /**
     * 유저 정보 데이터 리스트 반환
     */
    public RealmResults<Delivery> getAllWorkList(){
        return mRealm.where(Delivery.class).findAll();
    }

    public void showLogs(){
        mRealm.beginTransaction();
        RealmResults<Delivery> results = mRealm.where(Delivery.class).findAll();
        Log.d("조회값","good" + results.size());
        //for (int i = 0; i<results.size(); i++) {
        Log.d("조회값",results.get(0).getINV_KW());
        Log.d("조회값",results.get(0).getINV_NUMB());
        Log.d("조회값",results.get(0).getSHIP_TYPE());
        Log.d("조회값",results.get(0).getSHIP_ORD()+"");
        //}
        mRealm.commitTransaction();
    }
}
