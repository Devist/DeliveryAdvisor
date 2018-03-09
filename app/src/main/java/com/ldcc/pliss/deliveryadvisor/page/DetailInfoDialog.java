package com.ldcc.pliss.deliveryadvisor.page;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;

import org.w3c.dom.Text;

public class DetailInfoDialog extends Dialog {

    private Delivery deliveryInfo;
    private String selectedInvoiceNumber;
    private DeliveryHelper deliveryHelper;
    private TextView tvInvoice;
    private TextView tvDeliveryType;
    private TextView tvDeliveryOrder;
    private TextView tvDeliveryGroup;
    private TextView tvID;
    private TextView tvSenderName;
    private TextView tvSenderAddr;
    private TextView tvSenderTel1;
    private TextView tvSenderTel2;
    private TextView tvItemName;
    private TextView tvRecipientName;
    private TextView tvRecipientAddr;
    private TextView tvRecipientTel1;
    private TextView tvRecipientTel2;
    private TextView tvMessage;

    public DetailInfoDialog(Context context,String selectedInvoiceNumber){
        super(context);
        deliveryHelper = new DeliveryHelper(context);
        this.selectedInvoiceNumber = selectedInvoiceNumber;

    }
    private String addCategoryStr;

    private Button buttonChange;
    private Button buttonDone;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable((android.graphics.Color.TRANSPARENT)));
        setContentView(R.layout.dialog_detail_info);

        setLayout();
    }

    private void setLayout(){
        buttonChange = (Button)findViewById(R.id.button_change_destination);
        buttonDone = (Button)findViewById(R.id.button_confirm);

        deliveryInfo = deliveryHelper.getSearchedInfo(this.selectedInvoiceNumber);

        tvInvoice       = (TextView)findViewById(R.id.textview_dialog_invoice);
        tvDeliveryType  = (TextView)findViewById(R.id.textview_dialog_delivery_type);
        tvDeliveryOrder = (TextView)findViewById(R.id.textview_dialog_delivery_order);
        tvDeliveryGroup = (TextView)findViewById(R.id.textview_dialog_delivery_group);
        tvID            = (TextView)findViewById(R.id.textview_dialog_id);
        tvSenderName    = (TextView)findViewById(R.id.textview_dialog_sender_name);
        tvSenderAddr    = (TextView)findViewById(R.id.textview_dialog_sender_addr);
        tvSenderTel1    = (TextView)findViewById(R.id.textview_dialog_sender_tel1);
        tvSenderTel2    = (TextView)findViewById(R.id.textview_dialog_sender_tel2);
        tvItemName      = (TextView)findViewById(R.id.textview_dialog_item_name);
        tvRecipientName = (TextView)findViewById(R.id.textview_dialog_recipient_name);
        tvRecipientAddr = (TextView)findViewById(R.id.textview_dialog_recipient_addr);
        tvRecipientTel1 = (TextView)findViewById(R.id.textview_dialog_recipient_tel1);
        tvRecipientTel2 = (TextView)findViewById(R.id.textview_dialog_recipient_tel2);
        tvMessage       = (TextView)findViewById(R.id.textview_dialog_message);

        tvInvoice.setText(deliveryInfo.getINV_NUMB());
        tvDeliveryType.setText(deliveryInfo.getSHIP_TYPE());
        tvDeliveryOrder.setText(String.valueOf(deliveryInfo.getSHIP_ORD()));
        tvDeliveryGroup.setText(deliveryInfo.getSHIP_GRP_NM());
        tvID.setText(deliveryInfo.getSHIP_ID()+"");
        tvSenderName.setText(deliveryInfo.getSEND_NM());
        tvSenderAddr.setText(deliveryInfo.getSEND_ADDR());
        tvSenderTel1.setText(deliveryInfo.getSEND_1_TELNO());
        tvSenderTel2.setText(deliveryInfo.getSEND_2_TELNO());
        tvItemName.setText(deliveryInfo.getITEM_NM());
        tvRecipientName.setText(deliveryInfo.getRECV_NM());
        tvRecipientAddr.setText(deliveryInfo.getRECV_ADDR());
        tvRecipientTel1.setText(deliveryInfo.getRECV_1_TELNO());
        tvRecipientTel2.setText(deliveryInfo.getRECV_2_TELNO());
        tvMessage.setText(deliveryInfo.getSHIP_MSG());

        setListener();
    }

    private void setListener(){
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddCategoryStr(tvInvoice.getText().toString());
                DetailInfoDialog.this.dismiss();
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailInfoDialog.this.dismiss();
            }
        });
    }

    public String getAddCategoryStr() {return addCategoryStr;}
    public void setAddCategoryStr(String addCategoryStr) { this.addCategoryStr = addCategoryStr; }


}