package com.ldcc.pliss.deliveryadvisor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.adapter.CurrentWorkListAdapter;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;
import com.ldcc.pliss.deliveryadvisor.page.DetailInfoDialog;
import com.ldcc.pliss.deliveryadvisor.page.NavigationActivity;
import com.ldcc.pliss.deliveryadvisor.util.WorkUtil;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<String[]> mData;
    private List<Integer> mode;
    private float mBaseElevation;
    private ListView workView;
    private DeliveryHelper deliveryHelper;

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

    public ListView getWorkListView(){
        return workView;
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

    private void bind(final String[] managerInfo, View view, int mode) {

        final WorkUtil workUtil = new WorkUtil();
        //상단 현재 업무 뷰 세팅
        workView = (ListView) view.findViewById(R.id.currentWorkList);

        CurrentWorkListAdapter currentWorkListAdapter = new CurrentWorkListAdapter(view, managerInfo,mode);
        workView.setAdapter(currentWorkListAdapter);


        workView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tvView  = (TextView) view.findViewById(R.id.aNametxt);
                DetailInfoDialog detailInfoDialog = new DetailInfoDialog(view.getContext(),managerInfo[0]);
                switch (position){
                    case 0: //송장 클릭시 상세정보
                        detailInfoDialog = new DetailInfoDialog(view.getContext(),managerInfo[0]);
                        detailInfoDialog.show();
                        break;
                    case 2: //상품 클릭시 상세정보
                        detailInfoDialog = new DetailInfoDialog(view.getContext(),managerInfo[0]);
                        detailInfoDialog.show();
                        break;
                    case 3: //주소 클릭시 지도로 이동
                        view.getContext().startActivity(new Intent(view.getContext(), NavigationActivity.class));
                        break;
                    case 4: //전화번호 클릭시 전화 연결
                        workUtil.callTheCustomer(view.getContext(), String.valueOf(tvView.getText()));
                        workUtil.sendSMS(view.getContext(),String.valueOf(tvView.getText()),"배달원이 상품 배송을 진행하기 위해 전화 걸었습니다.");
                        break;
                }

                return false;
            }
        });

        Button doneButton = (Button) view.findViewById(R.id.done_current_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workUtil.showProcessDeliveryDialog(v.getContext(),managerInfo);
            }
        });
        deliveryHelper = new DeliveryHelper(view.getContext());
        String shipStat = deliveryHelper.getSearchedInfo(managerInfo[0]).getSHIP_STAT();
        String shipHow = deliveryHelper.getSearchedInfo(managerInfo[0]).getSTAT_HOW();

        if (shipStat.equals("C")){
            doneButton.setClickable(false);
            doneButton.setBackgroundResource(R.drawable.rounded_done);
            String buttonText = "완료";
            Log.d("방법",shipHow);
            switch (shipHow){
                case "S":
                    buttonText+=" -본인";
                    doneButton.setText(buttonText);
                    break;
                case "F":
                    buttonText+=" -가족";
                    break;
                case "A":
                    buttonText+=" -지인";
                    break;
                case "C":
                    buttonText+=" -회사";
                    break;
                case "O":
                    buttonText+=" -경비실";
                    break;
                case "D":
                    buttonText+=" -문 앞(옆)";
                    break;
                case "E":
                    buttonText+=" -기타";
                    break;
                case "U":
                    buttonText+=" -택배함";
                    break;
            }
            doneButton.setText(buttonText);
            doneButton.setTextColor(Color.GRAY);
        }else if (shipStat.equals("N")){
            doneButton.setClickable(false);
            doneButton.setText("배송 취소");
            doneButton.setBackgroundResource(R.drawable.rounded_done);
        }

        deliveryHelper.destroy();

    }

}
