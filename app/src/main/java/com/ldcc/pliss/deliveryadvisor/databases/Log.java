package com.ldcc.pliss.deliveryadvisor.databases;

import io.realm.RealmObject;

/**
 * Created by pliss on 2018. 2. 23..
 */

public class Log extends RealmObject {

    private String TIME_STAMP;
    private String CONTENTS;

    public String getTIME_STAMP() {
        return TIME_STAMP;
    }

    public void setTIME_STAMP(String TIME_STAMP) {
        this.TIME_STAMP = TIME_STAMP;
    }

    public String getCONTENTS() {
        return CONTENTS;
    }

    public void setCONTENTS(String CONTENTS) {
        this.CONTENTS = CONTENTS;
    }
}
