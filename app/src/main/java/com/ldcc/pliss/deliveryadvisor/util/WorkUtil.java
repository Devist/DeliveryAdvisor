package com.ldcc.pliss.deliveryadvisor.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;


/**
 * Created by pliss on 2018. 2. 27..
 */

public class WorkUtil {

    private Context context;

    public void callTheCustomer(Context context, String phoneNumber){
        this.context = context;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phoneNumber));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {

        }else {
            try {
                this.context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
