package com.ldcc.pliss.deliveryadvisor;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ldcc.pliss.deliveryadvisor.adapter.CurrentWorkListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<String[]> mData;
    private List<Integer> mode;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mData = new ArrayList<String[]>();
        mViews = new ArrayList<>();
        mode = new ArrayList<>();
    }

    public void addCardItem(String[] managerInfo, int mode) {
        mViews.add(null);
        mData.add(managerInfo);
        this.mode.add(mode);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.card_current, container, false);
        container.addView(view);
        bind(mData.get(position), view, mode.get(position));
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(String[] managerInfo, View view, int mode) {
        //상단 현재 업무 뷰 세팅
        ListView currentWorkListView = (ListView) view.findViewById(R.id.currentWorkList);
//        Button buttonShowDetails = (Button) view.findViewById(R.id.button_show_details);
//        Button buttonProcDelivery = (Button) view.findViewById(R.id.button_proc_delivery);
//        Button buttonCallCustomer= (Button) view.findViewById(R.id.button_call_customer);
//        Button buttonNaviPath = (Button) view.findViewById(R.id.button_navi_path);

        CurrentWorkListAdapter currentWorkListAdapter = new CurrentWorkListAdapter(view, managerInfo,mode);
        currentWorkListView.setAdapter(currentWorkListAdapter);

        //상태 진행 바 세팅
//        progressBarDelivery = (ProgressBar) view.findViewById(R.id.progress_bar_delivery);
//        progressTextDelivery = (TextView) view.findViewById(R.id.progress_text_delivery);
//        progressBarDelivery.setMax(results.size());
//        progressBarDelivery.setProgress(deliveryDoneCount);
//        progressTextDelivery.setText("업무 리스트 (" + deliveryDoneCount + "/" + results.size()+")");

//        TextView titleTextView = (TextView) view.findViewById(R.id.);
//        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
//        titleTextView.setText(item.getTitle());
//        contentTextView.setText(item.getText());
    }

}
