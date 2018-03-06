package com.ldcc.pliss.deliveryadvisor.databases;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by pliss on 2018. 2. 27..
 */

public class ManagerHelper {

    private Realm mRealm;
    private String[] managerInfo = new String[6];
    private RealmResults<Manager> results;

    public ManagerHelper(Context context){
        Realm.init(context);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        results = mRealm.where(Manager.class).findAll();
    }

    /**
     * 유저 정보 데이터 리스트 반환
     */
    public RealmResults<Manager> getAllWorkList(){
        return mRealm.where(Manager.class).findAll();
    }

    public void setManagerName(String name){
        mRealm.beginTransaction();
        Manager managerINFO = mRealm.createObject(Manager.class);
        managerINFO.setUserName(name);
        mRealm.commitTransaction();
    }

    public void setManager(String name, String invoice){
        mRealm.beginTransaction();
        Manager managerINFO = mRealm.createObject(Manager.class);
        managerINFO.setUserName(name);
        managerINFO.setCurrentInvoice(invoice);
        mRealm.commitTransaction();
    }

    public String[] getCurrentDeliveryInfo(Context context){
        String [] deliveryInfo = new String[6];
        DeliveryHelper deliveryHelper = new DeliveryHelper(context);
        Delivery currentDelivery = mRealm.where(Delivery.class).equalTo("INV_NUMB", results.get(0).getCurrentInvoice()).findFirst();
        //currentDelivery.setSHIP_STAT();
        deliveryInfo[0] = currentDelivery.getRECV_NM();
        deliveryInfo[1] = currentDelivery.getITEM_NM();
        deliveryInfo[2] = currentDelivery.getINV_NUMB();
        deliveryInfo[3] = currentDelivery.getRECV_ADDR();
        deliveryInfo[4] = currentDelivery.getRECV_1_TELNO();
        deliveryInfo[5] = currentDelivery.getSHIP_MSG();

        return deliveryInfo;
    }

    public int getCurrentDeliveryOrder(Context context){

        DeliveryHelper deliveryHelper = new DeliveryHelper(context);
        Delivery currentDelivery = mRealm.where(Delivery.class).equalTo("INV_NUMB", results.get(0).getCurrentInvoice()).findFirst();
        //currentDelivery.setSHIP_STAT();
        int result = currentDelivery.getSHIP_ORD();

        return result;
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
