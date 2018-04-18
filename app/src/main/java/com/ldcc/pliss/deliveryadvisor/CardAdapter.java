package com.ldcc.pliss.deliveryadvisor;


import android.support.v7.widget.CardView;

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 3;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}