package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ldcc.pliss.deliveryadvisor.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void goExplanationPage(View v){
        Intent newIntent = new Intent(this, ExplanationActivity.class);
        startActivity(newIntent);
        finish();
    }
}
