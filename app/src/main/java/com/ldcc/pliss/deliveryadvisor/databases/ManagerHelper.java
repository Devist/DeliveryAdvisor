package com.ldcc.pliss.deliveryadvisor.databases;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 *  Manager(택배기사)의 현재 업무 정보 데이터베이스를 조작하는 Helper입니다.
 *  Manager의 현재 업무 변경은 DeliveryHelper에서 처리하였습니다.
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
        managerINFO.setInvoice(invoice);
        mRealm.commitTransaction();
    }

    public String getManagerName(){
        return results.get(0).getUserName();
    }
    public String getInvoice(){
        return results.get(0).getInvoice();
    }

    public String[] getCurrentDeliveryInfoSimple(){
        String [] deliveryInfo = new String[7];
        mRealm.beginTransaction();
        Delivery currentDelivery = mRealm.where(Delivery.class).equalTo("INV_NUMB", results.get(0).getInvoice()).findFirst();
        deliveryInfo[0] = currentDelivery.getINV_NUMB();
        deliveryInfo[1] = currentDelivery.getRECV_NM();
        deliveryInfo[2] = currentDelivery.getITEM_NM();
        deliveryInfo[3] = currentDelivery.getRECV_ADDR();
        deliveryInfo[4] = currentDelivery.getRECV_1_TELNO();
        deliveryInfo[5] = currentDelivery.getSHIP_MSG();
        deliveryInfo[6] = String.valueOf(currentDelivery.getSHIP_ID());
        mRealm.commitTransaction();
        return deliveryInfo;
    }

    public Delivery getCurrentDeliveryInfoDetail(){
        mRealm.beginTransaction();
        Delivery currentDelivery = mRealm.where(Delivery.class).equalTo("INV_NUMB", results.get(0).getInvoice()).findFirst();
        mRealm.commitTransaction();
        return currentDelivery;
    }

    public String[] getSearchedInfoSimple(Delivery nextProduct){
        String [] deliveryInfo = new String[7];
        mRealm.beginTransaction();
        deliveryInfo[0] = nextProduct.getRECV_NM();
        deliveryInfo[1] = nextProduct.getITEM_NM();
        deliveryInfo[2] = nextProduct.getINV_NUMB();
        deliveryInfo[3] = nextProduct.getRECV_ADDR();
        deliveryInfo[4] = nextProduct.getRECV_1_TELNO();
        deliveryInfo[5] = nextProduct.getSHIP_MSG();
        deliveryInfo[6] = String.valueOf(nextProduct.getSHIP_ID());
        mRealm.commitTransaction();
        return deliveryInfo;
    }
}
