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

public class ProductAdapter extends ArrayAdapter<RequestPojo> {
    Activity context;
    List<RequestPojo> rest_List;
    String type;

    public ProductAdapter(Activity context, List<RequestPojo> rest_List) {
        super(context, R.layout.admin_view_products, rest_List);
        this.context = context;
        this.rest_List = rest_List;
        // TODO Auto-generated constructor stub
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_view_products, null, true);

        String base = rest_List.get(position).getImage();
        TextView weight = view.findViewById(R.id.pweight);
        TextView temp = view.findViewById(R.id.textFor);
        TextView temp1 = view.findViewById(R.id.textTo);
        TextView price = view.findViewById(R.id.p_price);
        LinearLayout layout = view.findViewById(R.id.linearPrice);

        TextView name = view.findViewById(R.id.pName);
        TextView description = view.findViewById(R.id.pdescription);
        ImageView imageView = view.findViewById(R.id.pdtImg);
        type = rest_List.get(position).getType();

        if (type.equals("scrap")) {
            layout.setVisibility(View.VISIBLE);
            name.setText(rest_List.get(position).getName());

            price.append(rest_List.get(position).getRate() + " / Kg");
            weight.setText(rest_List.get(position).getWeight() + " Kg");
        } else {
            temp.setVisibility(View.VISIBLE);
            temp1.setVisibility(View.GONE);
            weight.setText(rest_List.get(position).getRate());
            name.setText(rest_List.get(position).getWeight());
        }
        description.setText(rest_List.get(position).getDesc());
        try {
            byte[] imageAsBytes = Base64.decode(base.getBytes());
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }
}
