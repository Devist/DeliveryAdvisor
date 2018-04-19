package com.ldcc.pliss.deliveryadvisor.adapter;

/**
 * Created by pliss on 2018. 2. 22..
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldcc.pliss.deliveryadvisor.R;


public class CurrentWorkListAdapter extends BaseAdapter {

    private View view;
    private final String [] infos = new String[6];
    private final int[] images = {
            R.drawable.icon_invoice,
            R.drawable.icon_customer_name,
            R.drawable.icon_product,
            R.drawable.icon_address,
            R.drawable.icon_phone,
            R.drawable.icon_message
    };

    public final int mode;

    public CurrentWorkListAdapter(View view, String[] infos, int mode){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.view = view;

        for(int i = 0; i<6 ; i++){
            System.arraycopy(infos,i,this.infos,i,1);
        }
        this.mode= mode;
        Log.d("모드",this.mode+"");
    }

    @Override
    public int getCount() {
        return infos.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            convertView = inflater.inflate(R.layout.item_current_work, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);
            viewHolder.txtName.setText(infos[position]);
            viewHolder.icon.setImageResource(images[position]);

            if(position==0){
                viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.item_current);
                TextView textView = new TextView(convertView.getContext());
                textView.setBackgroundColor(Color.LTGRAY);
                textView.setHeight(2);

                viewHolder.layout.addView(textView);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                layoutParams.leftMargin = 15;
                layoutParams.rightMargin = 15;
                layoutParams.topMargin = 5;
                layoutParams.bottomMargin = 5;
                textView.setLayoutParams(layoutParams);
                if(mode==0){
                    viewHolder.txtName.setText("Prev : " + infos[position]);
                }

                if(mode==1){
                    viewHolder.txtName.setText("Now : " + infos[position]);
                    viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                }

                if(mode==2){
                    viewHolder.txtName.setText("Next : " + infos[position]);
                }

                viewHolder.txtName.setTextSize(17);
            }

            if(position==5){
                if(infos[position].equals("") || infos[position]==null){
                    viewHolder.txtName.setText("배송 메시지 없음");
                }
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }




        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        ImageView icon;
        LinearLayout layout;
    }

}