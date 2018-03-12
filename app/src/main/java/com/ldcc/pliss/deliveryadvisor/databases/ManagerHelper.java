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
    private RealmResults<Manager> results;

    public ManagerHelper(Context context){
        Realm.init(context);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        results = mRealm.where(Manager.class).findAll();
    }

    public void setManager(String name, String invoice){
        mRealm.beginTransaction();
        Manager managerINFO = mRealm.createObject(Manager.class);
        managerINFO.setUserName(name);
        managerINFO.setCurrentInvoice(invoice);
        mRealm.commitTransaction();
    }

    public String[] getCurrentDeliveryInfo(Context context){
        String [] deliveryInfo = new String[7];
        Delivery currentDelivery = mRealm.where(Delivery.class).equalTo("INV_NUMB", results.get(0).getCurrentInvoice()).findFirst();
        deliveryInfo[0] = currentDelivery.getRECV_NM();
        deliveryInfo[1] = currentDelivery.getITEM_NM();
        deliveryInfo[2] = currentDelivery.getINV_NUMB();
        deliveryInfo[3] = currentDelivery.getRECV_ADDR();
        deliveryInfo[4] = currentDelivery.getRECV_1_TELNO();
        deliveryInfo[5] = currentDelivery.getSHIP_MSG();
        deliveryInfo[6] = String.valueOf(currentDelivery.getSHIP_ORD());

        return deliveryInfo;
    }

    public String getManagerName(){
        return results.get(0).getUserName();
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

    public void deleteAllList(){
        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }
}
