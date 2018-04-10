package com.ldcc.pliss.deliveryadvisor.analyzer;

/**
 * Created by pliss on 2018. 4. 5..
 */

public class FinalAction {
    /** 최종 분석 결과 - 전화 연결 */
    public static final int CALL_CURRENT_CUSTOMER           = 1000;
    public static final int CALL_NEXT_CUSTOMER              = 1010;
    public static final int CALL_INVOICE_CUSTOMER           = 1020;
    public static final int CALL_E_INVOICE_OR_NEXT                  = 1200;
    public static final int CALL_E_MANY_INVOICES                    = 1300;

    /** 최종 분석 결과 - 배송 처리 - 단순 완료 */
    public static final int DONE_SIMPLE_CURRENNT            = 2000;
    public static final int DONE_SIMPLE_NEXT                = 2010;
    public static final int DONE_SIMPLE_INVOICE             = 2020;
    public static final int DONE_SIMPLE_E_INVOICE_OR_NEXT           = 2200;
    public static final int DONE_SIMPLE_E_MANY_INVOICES             = 2300;

    /** 최종 분석 결과 - 배송 처리 - 미완료,취소 */
    public static final int CANCLE_CURRENT                  = 3000;
    public static final int CANCLE_NEXT                     = 3010;
    public static final int CANCLE_INVOICE                  = 3020;
    public static final int CANCLE_E_INVOICE_OR_NEXT                = 3200;
    public static final int CANCLE_E_MANY_INVOICES                  = 3300;

    /** 최종 분석 결과 - 배송 처리 - 직접 수령 */
    public static final int DONE_SELF_CURRENT               = 4000;
    public static final int DONE_SELF_NEXT                  = 4010;
    public static final int DONE_SELF_INVOICE               = 4020;
    public static final int DONE_SELF_E_INVOICE_OR_CURRENT          = 4100;
    public static final int DONE_SELF_E_INVOICE_OR_NEXT             = 4200;
    public static final int DONE_SELF_E_MANY_INVOICES               = 4300;

    /** 최종 분석 결과 - 배송 처리 - 대리 수령 */
    public static final int DONE_OTHER_CURRENT              = 5000;
    public static final int DONE_OTHER_CURRENT_FAMILY       = 5001;
    public static final int DONE_OTHER_CURRENT_FRIEND       = 5002;
    public static final int DONE_OTHER_CURRENT_COMPANY      = 5003;

    public static final int DONE_OTHER_NEXT                 = 5010;
    public static final int DONE_OTHER_NEXT_FAMILY          = 5011;
    public static final int DONE_OTHER_NEXT_FRIEND          = 5012;
    public static final int DONE_OTHER_NEXT_COMPANY         = 5013;

    public static final int DONE_OTHER_INVOICE              = 5020;
    public static final int DONE_OTHER_INVOICE_FAMILY       = 5021;
    public static final int DONE_OTHER_INVOICE_FRIEND       = 5022;
    public static final int DONE_OTHER_INVOICE_COMPANY      = 5023;

    public static final int DONE_OTHER_E_INVOICE_OR_CURRENT         = 5100;
    public static final int DONE_OTHER_E_FAMILY_INVOICE_OR_CURRENT  = 5101;
    public static final int DONE_OTHER_E_FRIEND_INVOICE_OR_CURRENT  = 5102;
    public static final int DONE_OTHER_E_COMPANY_INVOICE_OR_CURRENT = 5103;

    public static final int DONE_OTHER_E_INVOICE_OR_NEXT            = 5200;
    public static final int DONE_OTHER_E_FAMILY_INVOICE_OR_NEXT     = 5201;
    public static final int DONE_OTHER_E_FRIEND_INVOICE_OR_NEXT     = 5202;
    public static final int DONE_OTHER_E_COMPANY_INVOICE_OR_NEXT    = 5203;

    public static final int DONE_OTHER_E_MANY_INVOICES              = 5300;
    public static final int DONE_OTHER_E_FAMILY_MANY_INVOICES       = 5301;
    public static final int DONE_OTHER_E_FRIEND_MANY_INVOICES       = 5302;
    public static final int DONE_OTHER_E_COMPANY_MANY_INVOICES      = 5303;

    /** 최종 분석 결과 - 배송 처리 - 보관 */
    public static final int DONE_KEEP_CURRENT_DOOR          = 6000;
    public static final int DONE_KEEP_CURRENT_SECURITY      = 6001;
    public static final int DONE_KEEP_CURRENT_STORAGE       = 6002;

    public static final int DONE_KEEP_NEXT_DOOR             = 6010;
    public static final int DONE_KEEP_NEXT_SECURITY         = 6011;
    public static final int DONE_KEEP_NEXT_STORAGE          = 6012;

    public static final int DONE_KEEP_INVOICE_DOOR          = 6020;
    public static final int DONE_KEEP_INVOICE_SECURITY      = 6021;
    public static final int DONE_KEEP_INVOICE_STORAGE       = 6022;

    public static final int DONE_KEEP_E_DOOR_INVOICE_OR_CURRENT     = 6100;
    public static final int DONE_KEEP_E_SECURITY_INVOICE_OR_CURRENT = 6101;
    public static final int DONE_KEEP_E_STORAGE_INVOICE_OR_CURRENT  = 6102;

    public static final int DONE_KEEP_E_DOOR_INVOICE_OR_NEXT        = 6200;
    public static final int DONE_KEEP_E_SECURITY_INVOICE_OR_NEXT    = 6201;
    public static final int DONE_KEEP_E_STORAGE_INVOICE_OR_NEXT     = 6202;

    public static final int DONE_KEEP_E_DOOR_MANY_INVOICES          = 6300;
    public static final int DONE_KEEP_E_SECURITY_MANY_INVOICES      = 6301;
    public static final int DONE_KEEP_E_STORAGE_MANY_INVOICES       = 6302;

    /** 최종 분석 결과 - 길 안내 */
    public static final int NAVI_CURRENT                    = 7000;
    public static final int NAVI_NEXT                       = 7010;
    public static final int NAVI_INVOICE                    = 7020;
    public static final int NAVI_E_INVOICE_OR_NEXT                    = 7200;
    public static final int NAVI_E_MANY_INVOICES                      = 7300;
}
