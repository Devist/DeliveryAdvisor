package com.ldcc.pliss.deliveryadvisor.adapter;

/**
 * Created by pliss on 2018. 2. 22..
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldcc.pliss.deliveryadvisor.R;

import java.util.ArrayList;


public class CurrentWorkListAdapter extends BaseAdapter {

    private Context context;
    private final String [] infos = new String[6];
    private final int[] images = {
            R.drawable.icon_customer_name,
            R.drawable.icon_product,
            R.drawable.icon_invoice_number,
            R.drawable.icon_target_location,
            R.drawable.icon_phone,
            R.drawable.icon_customer_message
    };

    public CurrentWorkListAdapter(Context context, String [] infos){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;

        for(int i = 0; i<6 ; i++){
            System.arraycopy(infos,i,this.infos,i,1);
        }
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
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_current_work, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        if(infos[position].length()>20){
//            viewHolder.txtName.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//            Log.d("값 들어옴",infos[position]);
//            int height = viewHolder.txtName.getMeasuredHeight();
//            Log.d("값 이전 높이",height+"");
//            viewHolder.txtName.setMinimumHeight(height*2);
//            Log.d("값 이후 높이",viewHolder.txtName.getMeasuredHeight()+"");
//        }
//        Log.d("값",infos[position]);
        viewHolder.txtName.setText(infos[position]);

        viewHolder.icon.setImageResource(images[position]);


        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        ImageView icon;

    }

}