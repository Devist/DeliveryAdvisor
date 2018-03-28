package com.ldcc.pliss.deliveryadvisor.databases;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;
import io.realm.Realm;
import io.realm.RealmResults;

public class AppLogsHelper {

    private Realm mRealm;
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

    private String getTime(){
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);

        return mFormat.format(mDate);
    }

}
