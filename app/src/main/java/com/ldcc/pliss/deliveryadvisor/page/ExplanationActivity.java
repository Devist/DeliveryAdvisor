package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ldcc.pliss.deliveryadvisor.R;

public class ExplanationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation);
    }

    public void goSignInPage(View v){
        Intent newIntent = new Intent(this, SignInActivity.class);
        startActivity(newIntent);
        finish();
    }
}
