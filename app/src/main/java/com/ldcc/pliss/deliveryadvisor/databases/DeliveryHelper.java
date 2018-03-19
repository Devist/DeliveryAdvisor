package com.ldcc.pliss.deliveryadvisor.databases;

import android.content.Context;
import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by pliss on 2018. 2. 27..
 */

public class DeliveryHelper {

    private Realm mRealm;
    private RealmResults<Delivery> results;

    public DeliveryHelper(Context context){
        Realm.init(context);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        if (mRealm.isInTransaction())
            mRealm.commitTransaction();

        mRealm.beginTransaction();
        results = mRealm.where(Delivery.class).findAll();
        mRealm.commitTransaction();
    }

    /**
     * 모든 작업 목록 반환
     */
    public RealmResults<Delivery> getAllDeliveryList(){
        return results;
    }

    public void deleteAllList(){
        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.deleteAll();
        mRealm.commitTransaction();
    }

    public String getLastYetDelivery(){
        String yetInvoiceNumber = null;

        for(int i=0; i<results.size();i++){
            if(results.get(i).getSHIP_STAT().equals("B")){
                yetInvoiceNumber = results.get(i).getINV_NUMB();
                break;
            }
        }
        return yetInvoiceNumber;
    }

    public void changeManagerInfo(String invoice){
        mRealm.beginTransaction();
        Delivery delivery = mRealm.where(Delivery.class).equalTo("INV_NUMB", invoice).findFirst();
        Manager managerINFO = mRealm.where(Manager.class).findAll().first();
        managerINFO.setCurrentInvoice(delivery.getINV_NUMB());
        mRealm.commitTransaction();
    }

    public void changeManagerInfoToNext(){
        String yetInvoiceNumber = null;
        for(int i=0; i<results.size();i++){
            if(results.get(i).getSHIP_STAT().equals("B")){
                yetInvoiceNumber = results.get(i).getINV_NUMB();
                break;
            }
        }

        mRealm.beginTransaction();
        Manager managerINFO = mRealm.where(Manager.class).findAll().first();
        managerINFO.setCurrentInvoice(yetInvoiceNumber);
        mRealm.commitTransaction();
    }

    public Delivery getSearchedInfo(String invoiceNumber){
        mRealm.beginTransaction();
        Delivery deliveryProcessed = mRealm.where(Delivery.class).equalTo("INV_NUMB", invoiceNumber).findFirst();
        mRealm.commitTransaction();

        return deliveryProcessed;

    }

    public void setAllDeliveryList(final List<String[]> workData){
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for(int i = 1 ; i<workData.size();i++){

                    Delivery deliveryINFO = realm.createObject(Delivery.class,workData.get(i)[1]);
                    deliveryINFO.setSHIP_ID(Integer.parseInt(workData.get(i)[0]));
                    deliveryINFO.setSHIP_TYPE(workData.get(i)[2]);
                    deliveryINFO.setSHIP_ORD(Integer.parseInt(workData.get(i)[3]));
                    deliveryINFO.setSHIP_GRP_NM(workData.get(i)[4]);
                    deliveryINFO.setSEND_NM(workData.get(i)[5]);
                    deliveryINFO.setSEND_ADDR(workData.get(i)[6]);
                    deliveryINFO.setSEND_ADDR_LAT(workData.get(i)[7]);
                    deliveryINFO.setSEND_ADDR_LNG(workData.get(i)[8]);
                    deliveryINFO.setSEND_1_TELNO(workData.get(i)[9]);
                    deliveryINFO.setSEND_2_TELNO(workData.get(i)[10]);

                    deliveryINFO.setITEM_NM(workData.get(i)[11]);
                    deliveryINFO.setRECV_NM(workData.get(i)[12]);
                    deliveryINFO.setRECV_ADDR(workData.get(i)[13]);
                    deliveryINFO.setRECV_ADDR_LAT(workData.get(i)[14]);
                    deliveryINFO.setRECV_ADDR_LNG(workData.get(i)[15]);
                    deliveryINFO.setRECV_1_TELNO(workData.get(i)[16]);
                    deliveryINFO.setRECV_2_TELNO(workData.get(i)[17]);
                    deliveryINFO.setSHIP_MSG(workData.get(i)[18]);

                    deliveryINFO.setINV_KW(workData.get(i)[1].substring(7,10));
                    deliveryINFO.setSHIP_STAT("B");
                }
            }
        });
    }

    public void processCurrentDelivery(String invoice, String state, String how){
        mRealm.beginTransaction();

        Delivery deliveryProcessed = mRealm.where(Delivery.class).equalTo("INV_NUMB", invoice).findFirst();
        if(state.equals("C")){
            deliveryProcessed.setSHIP_STAT("C");
            deliveryProcessed.setSTAT_HOW(how);
        } else if(state.equals("N")) {
            deliveryProcessed.setSHIP_STAT("N");
        }
        mRealm.commitTransaction();
    }

    public void showLogs(){
        mRealm.beginTransaction();
        RealmResults<Delivery> results = mRealm.where(Delivery.class).findAll();
        Log.d("조회값","good" + results.size());
        mRealm.commitTransaction();
    }
}
