package com.ldcc.pliss.deliveryadvisor.page;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.logger.LogFragment;

public class LogActivity extends AppCompatActivity {

    private LogFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        mLogFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
        mLogFragment.getLogView()
                .println("2017-02-08:Received an unsupported action in FenceReceiver: action=");
    }
}
