package com.ldcc.pliss.deliveryadvisor.databases;

import android.content.Context;
import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 *  모든 업무(배송) 정보 데이터베이스를 조작하는 Helper입니다.
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


    /**
     * Realm 데이터베이스의 모든 정보를 삭제하고 초기화 합니다.
     */
    public void deleteAllList(){
        mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.commitTransaction();
    }

    /**
     * 최초 로그인시, 첫 번째 배송 목록을 반환해줍니다. SignActivity에서 사용됩니다.
     * @return 배송 목록 중 첫번째 배송 목록
     */
    public String getFirstShippingInfo(){
        results = mRealm.where(Delivery.class).findAll();
        return results.get(0).getINV_NUMB();
    }

    /**
     * 매니저(택배 기사)의 현재 업무를 해당 송장번호의 배송 정보로 변경합니다.
     * @param invoice 현재 업무로 사용할 송장 번호
     */
    public void changeManagerInfo(String invoice){
        mRealm.beginTransaction();
        Delivery delivery = mRealm.where(Delivery.class).equalTo("INV_NUMB", invoice).findFirst();
        if(!delivery.getSHIP_STAT().equals("C") && !delivery.getSHIP_STAT().equals("N")){
            Manager managerINFO = mRealm.where(Manager.class).findAll().first();
            managerINFO.setInvoice(delivery.getINV_NUMB());
        }
        mRealm.commitTransaction();
    }

    /**
     * 매니저\(택배 기사)의 현재 업무를 해당 송장번호의 다음 송장번호의 배송지로 변경합니다.
     * 다음 배송지가 이미 배송 완료가 되었을 경우, 그 다음 배송지로 변경됩니다.
     * @param invoice 기준이 되는 송장 번호
     */
    public void changeManagerInfoToNext(String invoice){
        String yetInvoiceNumber = null;
        for(int i=0; i<results.size();i++){
            if(results.get(i).getINV_NUMB().equals(invoice)){
                for(int j=i+1; j<results.size();j++){
                    if(j<results.size()){
                        if(!results.get(j).getSHIP_STAT().equals("C") && !results.get(j).getSHIP_STAT().equals("N")){
                            yetInvoiceNumber = results.get(j).getINV_NUMB();
                            break;
                        }
                    }
                }
            }
        }

        if(yetInvoiceNumber!=null){
            mRealm.beginTransaction();
            Manager managerINFO = mRealm.where(Manager.class).findAll().first();
            managerINFO.setInvoice(yetInvoiceNumber);
            mRealm.commitTransaction();
        }
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
                results = realm.where(Delivery.class).findAll();
                results.deleteAllFromRealm();
                Delivery deliveryINFO;
                for(int i = 1 ; i<workData.size();i++){
                        deliveryINFO = realm.createObject(Delivery.class,workData.get(i)[1]);
                        deliveryINFO.setINV_KW(workData.get(i)[1].substring(8));
                        deliveryINFO.setSHIP_ID(i);
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
}
