package com.ldcc.pliss.deliveryadvisor.analyzer;

/**
 * Created by pliss on 2018. 4. 5..
 */

public class ParamSet {

    /** 키워드 - 현재 고객 */
    public static final String[] currentCustomer = {"현재","지금","이번"};

    /** 키워드 - 다음 고객 */
    public static final String[] nextCustomer = {"다음"};

    /** 키워드 - 전화. */
    public static final String[] callKeywords = {"전화","콜","call","전화하다","연결"};

    /** 키워드 - 배송 */
    public static final String[] deliveryKeywords = {"배송","배달","전달"};

    /** 키워드 - 배송 - 완료 */
    public static final String[] deliveryDoneKeywords = {"완료","치"};
    public static final String done ="완료";

    /** 키워드 - 배송 - 미완료 */
    public static final String[] deliveryCancleKeywords = {"취소","미완료","못","밉다"};
    public static final String cancle ="미완료";

    /** 키워드 - 배송 - 완료 - 본인 수령 */
    public static final String[] directKeywords = {"본인","수하인","이번"};
    public static final String[] directKeywords2 = {"직접"};

            //조합 키워드
            public static final String[] directCombination = {"받다","수령","전달"};




    /** 키워드 - 배송 - 완료 - 타인 수령 */
    //가족
    public static final String[] otherFamilyKeywords
            = {"가족","엄마","어머니","아빠","아버지","할머니","할아버지","남동생","여동생","동생","이모","고모","숙모"
                ,"삼촌","숙부","고종사촌"};
    //동료
    public static final String[] otherCompanyKeywords = {"회사","동료","팀원","팀장","대리"};

    //지인
    public static final String[] otherFriendKeywords = {"친구","동거인","룸메","룸메이트","메이트"};

    //대신
    public static final String[] otherKeywords = {"대신","대리"};

            //조합키워드
            public static final String[] otherCombination = {"받다","수령","전달","위탁"};





    /** 키워드 - 배송 - 완료 - 보관 */
    //문
    public static final String[] doors = {"문앞","문옆","문뒤"};
    public static final String[] door = {"문"};
    public static final String[] doorCombination = {"앞","옆"};

    //경비실
    public static final String[] security = {"경비","경비실","경비원"};

    //보관함
    public static final String[] storages = {"택배","문서"};
    public static final String[] storagesCombination = {"보관","수발","위탁"};
    public static final String unmanned = "무인";




    /** 키워드 - 길 안내 */
    public static final String[] naviLocation = {"길","위치","장소","집"};
            public static final String[] naviCombination = {"알다","안내","안내하다","어디"};

    public static final String[] nextLocation = {"다음"};
            public static final String[] nextLocationCombination = {"어디"};

    public static final String[] howLocation = {"어떻다"};
            public static final String[] howLocationCombination = {"가다"};


}
