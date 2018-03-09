package com.ldcc.pliss.deliveryadvisor.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by pliss on 2018. 3. 5..
 */

public class AllWorkListVO {
    private String txtTitle;
    private String txtContents;
    private String txtAddress;
    private String status;

    public String getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(String txtTitle) {
        this.txtTitle = txtTitle;
    }

    public String getTxtContents() {
        return txtContents;
    }

    public void setTxtContents(String txtContents) {
        this.txtContents = txtContents;
    }

    public String getTxtAddress() {
        return txtAddress;
    }

    public void setTxtAddress(String txtAddress) {
        this.txtAddress = txtAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
