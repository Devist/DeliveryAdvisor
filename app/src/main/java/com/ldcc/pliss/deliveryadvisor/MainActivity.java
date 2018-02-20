package com.ldcc.pliss.deliveryadvisor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ldcc.pliss.deliveryadvisor.page.HomeActivity;

public class MainActivity extends AppCompatActivity {

    public SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
    }

    public void checkFirstRun(){
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun)
        {
            Intent newIntent = new Intent(this, HomeActivity.class);
            startActivity(newIntent);

            prefs.edit().putBoolean("isFirstRun",false).apply();
        }
    }
}
