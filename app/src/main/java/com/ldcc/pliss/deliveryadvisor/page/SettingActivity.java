package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.databases.ManagerHelper;

import java.io.File;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void initAndgoHomePage(View v){
        MainActivity.fa.finish();
        clearApplicationData(v.getContext());

        DeliveryHelper deliveryHelper = new DeliveryHelper(this);
        deliveryHelper.deleteAllList();

        ManagerHelper managerHelper = new ManagerHelper(this);
        managerHelper.deleteAllList();

        Intent newIntent = new Intent(this, HomeActivity.class);
        startActivity(newIntent);
        finish();
    }

    /**
     * 앱 캐시 지우기
     * @param context
     */
    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                //다운로드 파일은 지우지 않도록 설정
                //if(s.equals("lib") || s.equals("files")) continue;
                deleteDir(new File(appDir, s));
                Log.d("test", "File /data/data/"+context.getPackageName()+"/" + s + " DELETED");
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
