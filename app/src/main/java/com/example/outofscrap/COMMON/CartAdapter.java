package com.example.outofscrap.COMMON;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.outofscrap.R;

import java.io.IOException;
import java.util.List;

public class CartAdapter extends ArrayAdapter<RequestPojo> {
    Activity context;
    List<RequestPojo> rest_List;
    String type;

    public CartAdapter(Activity context, List<RequestPojo> rest_List) {
        super(context, R.layout.user_view_cart, rest_List);
        this.context = context;
        this.rest_List = rest_List;
        // TODO Auto-generated constructor stub
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.user_view_cart, null, true);
        String base = rest_List.get(position).getImage();
        TextView ProductName = view.findViewById(R.id.order_p_name);
        TextView ProductDesc = view.findViewById(R.id.order_p_desc);
        TextView ProductRate = view.findViewById(R.id.order_p_rate);
        TextView ProductQuantity = view.findViewById(R.id.order_p_qty);
        ImageView ProductImage = view.findViewById(R.id.pdtImg);
        LinearLayout qtyLayout = view.findViewById(R.id.qtyLayout);

        type = rest_List.get(position).getType();
        if (type.equals("creative")) {
            ProductName.setText(rest_List.get(position).getWeight());
            qtyLayout.setVisibility(View.GONE);
        } else {
            ProductName.setText(rest_List.get(position).getName());
            ProductQuantity.setText(rest_List.get(position).getWeight() + " Kg");
        }

        ProductDesc.setText(rest_List.get(position).getDesc());
        ProductRate.append(rest_List.get(position).getTotal());

        try {
            byte[] imageAsBytes = Base64.decode(base.getBytes());
            ProductImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }
}
