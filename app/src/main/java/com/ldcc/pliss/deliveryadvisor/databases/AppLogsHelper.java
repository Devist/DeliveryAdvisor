package com.ldcc.pliss.deliveryadvisor.databases;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by pliss on 2018. 2. 27..
 */

public class AppLogsHelper {

    private Realm mRealm;
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초");
    private RealmResults<AppLogs> results;
    public AppLogsHelper(Context context){
        Realm.init(context);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        mRealm.beginTransaction();
        results = mRealm.where(AppLogs.class).findAll();
        mRealm.commitTransaction();
    }

    /**
     * 유저 정보 데이터 리스트 반환
     */
    public RealmResults<Delivery> getAllWorkList(){
        return mRealm.where(Delivery.class).findAll();
    }

    public void addAppLogs(final String str){
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AppLogs appLogs = realm.createObject(AppLogs.class, getTime());
                appLogs.setCONTENTS(str);
            }
        });
    }

    public RealmResults<AppLogs> getLogs(){
        return results;
    }

    public void deleteAllList(){
        mRealm.beginTransaction();
        RealmResults<Delivery> results = mRealm.where(Delivery.class).findAll();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

}
