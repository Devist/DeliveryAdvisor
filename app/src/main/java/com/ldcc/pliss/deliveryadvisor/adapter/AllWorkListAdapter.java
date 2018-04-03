package com.ldcc.pliss.deliveryadvisor.adapter;

/**
 * Created by pliss on 2018. 2. 22..
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.R;

import java.util.ArrayList;


public class AllWorkListAdapter extends BaseAdapter {

    private ArrayList<AllWorkListVO> allWorkListVO = new ArrayList<AllWorkListVO>() ;

    //invoice,customerProduct,customerName,customerAddress,status
    public AllWorkListAdapter(String[] invoice, String[] customerName, String []customerProduct, String[] customerAddress, String [] deliveryStatus, int currentPosition){
        super();
        for(int i =0  ; i< invoice.length ; i++){
            String title = "업무 " + (i+1) + " : " + invoice[i];
            String contents = customerName[i] + "님 : " + customerProduct[i];
            String address = customerAddress[i];
            String status = deliveryStatus[i];
            addVO(title,contents,address,status);
        }
    }

    public void addVO(String title, String contents, String address, String status){
        AllWorkListVO item = new AllWorkListVO();

        item.setTxtTitle(title);
        item.setTxtContents(contents);
        item.setTxtAddress(address);
        item.setStatus(status);
        allWorkListVO.add(item);
    }
    @Override
    public int getCount() {
        return allWorkListVO.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        AllWorkListVO allWorkListViewItem = allWorkListVO.get(position);

        TextView txtTitle;
        TextView txtContents;
        TextView txtAddress;
        ImageView icon;

        if(allWorkListViewItem.getStatus().equals("O")){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_all_work_ongoing,parent, false);
            txtTitle = (TextView) convertView.findViewById(R.id.textWorkTitle_ongoing);
            txtContents = (TextView) convertView.findViewById(R.id.textWorkContents_ongoing);
            txtAddress = (TextView) convertView.findViewById(R.id.textWorkAddress_ongoing);
            icon = (ImageView) convertView.findViewById(R.id.imageWorkState_ongoing);
        }else{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_all_work,parent, false);
            txtTitle = (TextView) convertView.findViewById(R.id.textWorkTitle);
            txtContents = (TextView) convertView.findViewById(R.id.textWorkContents);
            txtAddress = (TextView) convertView.findViewById(R.id.textWorkAddress);
            icon = (ImageView) convertView.findViewById(R.id.imageWorkState);
        }

        txtTitle.setText(allWorkListViewItem.getTxtTitle());
        txtContents.setText(allWorkListViewItem.getTxtContents());
        txtAddress.setText(allWorkListViewItem.getTxtAddress());

        switch (allWorkListViewItem.getStatus()){
            case "C":
                icon.setImageResource(R.drawable.icon_check);
                break;
            case "B":
                icon.setImageResource(R.drawable.icon_delivery_yet);
                break;
            case "N":
                icon.setImageResource(R.drawable.icon_delevery_cancel);
                break;
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return allWorkListVO.get(position);
    }

}