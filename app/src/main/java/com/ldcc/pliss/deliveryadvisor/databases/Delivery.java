package com.ldcc.pliss.deliveryadvisor.databases;

import android.util.Log;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by pliss on 2018. 2. 23..
 */

@RealmClass
public class Delivery extends RealmObject {


    @PrimaryKey
    private String  INV_NUMB;       //송장번호

    private int     SHIP_ID;
    private String  INV_KW;         //송장번호 키워드
    private String  SHIP_TYPE;      //배송유형
    private int     SHIP_ORD;       //배송순서
    private String  SHIP_GRP_NM;    //배송그룹명
    private String  SEND_NM;        //송하인 성명
    private String  SEND_ADDR;      //송하인 주소
    private String  SEND_ADDR_LAT;  //송하인 주소 위도
    private String  SEND_ADDR_LNG;  //송하인 주소 경도
    private String  SEND_1_TELNO;   //송하인 전화번호 1
    private String  SEND_2_TELNO;   //송하인 전화번호 2
    private String  ITEM_NM;        //상품명
    private String  RECV_NM;        //수하인 성명
    private String  RECV_ADDR;      //수하인 주소
    private String  RECV_ADDR_LAT;  //수하인 주소 위도
    private String  RECV_ADDR_LNG;  //수하인 주소 경도
    private String  RECV_1_TELNO;   //수하인 전화번호 1
    private String  RECV_2_TELNO;   //수하인 전화번호 2
    private String  SHIP_MSG;       //배송메시지
    private String  SHIP_STAT;      //배송상태
    private String  STAT_DTIME;     //배송상태 최종 변경일시
    private String  STAT_HOW;       //인수방법
    private String  STAT_HOW_DTL;   //인수방법-상세
    private String  RMRK;           //비고
    private String  DEL_YN;         //삭제여부
    private String  REG_DTIME;      //등록일시
    private String  MOD_DTIME;      //수정일시


    public int getSHIP_ID() {
        return SHIP_ID;
    }

    public void setSHIP_ID(int SHIP_ID) {
        this.SHIP_ID = SHIP_ID;
    }

    public String getINV_NUMB() {
        return INV_NUMB;
    }

    public void setINV_NUMB(String INV_NUMB) {
        this.INV_NUMB = INV_NUMB;
    }

    public String getINV_KW() {
        return INV_KW;
    }

    public void setINV_KW(String INV_KW) {
        this.INV_KW = INV_KW;
    }

    public String getSHIP_TYPE() {
        return SHIP_TYPE;
    }

    public void setSHIP_TYPE(String SHIP_TYPE) {
        this.SHIP_TYPE = SHIP_TYPE;
    }

    public int getSHIP_ORD() {
        return SHIP_ORD;
    }

    public void setSHIP_ORD(int SHIP_ORD) {
        this.SHIP_ORD = SHIP_ORD;
    }

    public String getSHIP_GRP_NM() {
        return SHIP_GRP_NM;
    }

    public void setSHIP_GRP_NM(String SHIP_GRP_NM) {
        this.SHIP_GRP_NM = SHIP_GRP_NM;
    }

    public String getSEND_NM() {
        return SEND_NM;
    }

    public void setSEND_NM(String SEND_NM) {
        this.SEND_NM = SEND_NM;
    }

    public String getSEND_ADDR() {
        return SEND_ADDR;
    }

    public void setSEND_ADDR(String SEND_ADDR) {
        this.SEND_ADDR = SEND_ADDR;
    }

    public String getSEND_1_TELNO() {
        return SEND_1_TELNO;
    }

    public void setSEND_1_TELNO(String SEND_1_TELNO) {
        this.SEND_1_TELNO = SEND_1_TELNO;
    }

    public String getSEND_2_TELNO() {
        return SEND_2_TELNO;
    }

    public void setSEND_2_TELNO(String SEND_2_TELNO) {
        this.SEND_2_TELNO = SEND_2_TELNO;
    }

    public String getITEM_NM() {
        return ITEM_NM;
    }

    public void setITEM_NM(String ITEM_NM) {
        this.ITEM_NM = ITEM_NM;
    }

    public String getRECV_NM() {
        return RECV_NM;
    }

    public void setRECV_NM(String RECV_NM) {
        this.RECV_NM = RECV_NM;
    }

    public String getRECV_ADDR() {
        return RECV_ADDR;
    }

    public void setRECV_ADDR(String RECV_ADDR) {
        this.RECV_ADDR = RECV_ADDR;
    }

    public String getRECV_1_TELNO() {
        return RECV_1_TELNO;
    }

    public void setRECV_1_TELNO(String RECV_1_TELNO) {
        this.RECV_1_TELNO = RECV_1_TELNO;
    }

    public String getRECV_2_TELNO() {
        return RECV_2_TELNO;
    }

    public void setRECV_2_TELNO(String RECV_2_TELNO) {
        this.RECV_2_TELNO = RECV_2_TELNO;
    }

    public String getSHIP_MSG() {
        return SHIP_MSG;
    }

    public void setSHIP_MSG(String SHIP_MSG) {
        this.SHIP_MSG = SHIP_MSG;
    }

    public String getSHIP_STAT() {
        return SHIP_STAT;
    }

    public void setSHIP_STAT(String SHIP_STAT) {
        this.SHIP_STAT = SHIP_STAT;
    }

    public String getSTAT_DTIME() {
        return STAT_DTIME;
    }

    public void setSTAT_DTIME(String STAT_DTIME) {
        this.STAT_DTIME = STAT_DTIME;
    }

    public String getSTAT_HOW() {
        return STAT_HOW;
    }

    public void setSTAT_HOW(String STAT_HOW) {
        this.STAT_HOW = STAT_HOW;
    }

    public String getSTAT_HOW_DTL() {
        return STAT_HOW_DTL;
    }

    public void setSTAT_HOW_DTL(String STAT_HOW_DTL) {
        this.STAT_HOW_DTL = STAT_HOW_DTL;
    }

    public String getRMRK() {
        return RMRK;
    }

    public void setRMRK(String RMRK) {
        this.RMRK = RMRK;
    }

    public String getDEL_YN() {
        return DEL_YN;
    }

    public void setDEL_YN(String DEL_YN) {
        this.DEL_YN = DEL_YN;
    }

    public String getREG_DTIME() {
        return REG_DTIME;
    }

    public void setREG_DTIME(String REG_DTIME) {
        this.REG_DTIME = REG_DTIME;
    }

    public String getMOD_DTIME() {
        return MOD_DTIME;
    }

    public void setMOD_DTIME(String MOD_DTIME) {
        this.MOD_DTIME = MOD_DTIME;
    }

    public String getSEND_ADDR_LAT() {
        return SEND_ADDR_LAT;
    }

    public void setSEND_ADDR_LAT(String SEND_ADDR_LAT) {
        this.SEND_ADDR_LAT = SEND_ADDR_LAT;
    }

    public String getSEND_ADDR_LNG() {
        return SEND_ADDR_LNG;
    }

    public void setSEND_ADDR_LNG(String SEND_ADDR_LNG) {
        this.SEND_ADDR_LNG = SEND_ADDR_LNG;
    }

    public String getRECV_ADDR_LAT() {
        return RECV_ADDR_LAT;
    }

    public void setRECV_ADDR_LAT(String RECV_ADDR_LAT) {
        this.RECV_ADDR_LAT = RECV_ADDR_LAT;
    }

    public String getRECV_ADDR_LNG() {
        return RECV_ADDR_LNG;
    }

    public void setRECV_ADDR_LNG(String RECV_ADDR_LNG) {
        this.RECV_ADDR_LNG = RECV_ADDR_LNG;
    }
}