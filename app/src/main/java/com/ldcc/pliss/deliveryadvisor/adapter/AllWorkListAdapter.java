package com.ldcc.pliss.deliveryadvisor.adapter;

/**
 * Created by pliss on 2018. 2. 22..
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldcc.pliss.deliveryadvisor.R;

import java.util.ArrayList;


public class AllWorkListAdapter extends BaseAdapter {

    private ArrayList<AllWorkListVO> allWorkListVO = new ArrayList<AllWorkListVO>() ;
    //invoice,customerProduct,customerName,customerAddress,status
    public AllWorkListAdapter(String[] invoice, String[] customerName, String []customerProduct, String[] customerAddress, String [] status){
        super();
        for(int i =0  ; i< invoice.length ; i++){
            String title = "업무 " + (i+1) + " : " + invoice[i];
            String contents = customerName[i] + "님 : " + customerProduct[i];
            String address = customerAddress[i];
            addVO(title,contents,address);
        }
    }


    public void addVO(String title, String contents, String address){
        AllWorkListVO item = new AllWorkListVO();

        item.setTxtTitle(title);
        item.setTxtContents(contents);
        item.setTxtAddress(address);

        allWorkListVO.add(item);
    }
    @Override
    public int getCount() {
        return allWorkListVO.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_all_work, parent, false);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.textWorkTitle);
        TextView txtContents = (TextView) convertView.findViewById(R.id.textWorkContents);
        TextView txtAddress = (TextView) convertView.findViewById(R.id.textWorkAddress);
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageWorkState);

        AllWorkListVO allWorkListViewItem = allWorkListVO.get(position);

        txtTitle.setText(allWorkListViewItem.getTxtTitle());
        txtContents.setText(allWorkListViewItem.getTxtContents());
        txtAddress.setText(allWorkListViewItem.getTxtAddress());

        icon.setImageResource(R.drawable.icon_check);

//        final View finalConvertView = convertView;
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, (pos+1)+"번째 리스트가 클릭되었습니다,",Toast.LENGTH_SHORT).show();
//                //finalConvertView.setBackgroundColor(Color.RED);
//                finalConvertView.notify();
//            }
//        });

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