package com.ldcc.pliss.deliveryadvisor.page;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ldcc.pliss.deliveryadvisor.R;

/**
 *
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        ImageView image=(ImageView) findViewById(R.id.home_image);
        Animation alphaAnim = AnimationUtils.loadAnimation(this,R.anim.alpha);
        image.startAnimation(alphaAnim);
    }

    /**
     *
     * @param v
     */
    public void goExplanationPage(View v){
        Intent newIntent = new Intent(this, ExplanationActivity.class);
        startActivity(newIntent);
        finish();
    }
}
