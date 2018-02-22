package com.ldcc.pliss.deliveryadvisor.adapter;

/**
 * Created by pliss on 2018. 2. 22..
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldcc.pliss.deliveryadvisor.R;


public class allWorkListAdapter extends BaseAdapter {

    Context context;
    private final String [] invoice;
    private final String [] customerName;
    private final String [] customerProduct;
    private final String [] customerAddress;
    private final String [] status;

    public allWorkListAdapter(Context context, String[] invoice, String[] customerName, String[] customerProduct, String[] customerAddress, String [] status){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.invoice = invoice;
        this.customerName = customerName;
        this.customerProduct = customerProduct;
        this.customerAddress = customerAddress;
        this.status = status;
    }

    @Override
    public int getCount() {
        return invoice.length;
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

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_all_work, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.textWorkTitle);
            viewHolder.txtContents = (TextView) convertView.findViewById(R.id.textWorkContents);
            viewHolder.txtAddress = (TextView) convertView.findViewById(R.id.textWorkAddress);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.imageWorkState);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtTitle.setText("배송지 "+ position + ": 송장번호[ " + invoice[position] + "]");
        viewHolder.txtContents.setText("["+ customerName[position] + "]님에게 상품 ["+ customerProduct[position] + "] 배송입니다.");
        viewHolder.txtAddress.setText(customerAddress[position]);
        if(status[position].equals("done"))
            viewHolder.icon.setImageResource(R.drawable.icon_check);
        else if(status[position].equals("yet"))
            viewHolder.icon.setImageResource(R.drawable.icon_check);
        else
            viewHolder.icon.setImageResource(R.drawable.icon_check);

        return convertView;
    }

    private static class ViewHolder {

        TextView txtTitle;
        TextView txtContents;
        TextView txtAddress;
        ImageView icon;

    }

}