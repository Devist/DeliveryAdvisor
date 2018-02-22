package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
    }

    public void goMainPage(View v){
        Intent newIntent = new Intent(this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }
}
