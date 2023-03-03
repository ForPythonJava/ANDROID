package com.example.outofscrap.COMMON;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.outofscrap.R;

import java.io.IOException;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<RequestPojo> {
    Activity context;
    List<RequestPojo> requestPojoList;
    String type, base, userType;

    public OrderAdapter(Activity context, List<RequestPojo> requestPojoList) {
        super(context, R.layout.view_order, requestPojoList);
        this.context = context;
        this.requestPojoList = requestPojoList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_order, null, true);

        TextView pdtName = view.findViewById(R.id.pdtName);
        TextView pdtDesc = view.findViewById(R.id.pdtDesc);
        TextView pdtTotal = view.findViewById(R.id.pdtTotal);
        TextView p_Mode = view.findViewById(R.id.p_Mode);
        TextView uname = view.findViewById(R.id.usrName);
        TextView weight = view.findViewById(R.id.weightt);
        LinearLayout layout = view.findViewById(R.id.Linear_name);
        LinearLayout layout1 = view.findViewById(R.id.abcd);

        ImageView pdtImage = view.findViewById(R.id.pdtImage);

        type = requestPojoList.get(position).getType();
        if (type.equals("scrap")) {
            layout1.setVisibility(View.VISIBLE);
            weight.setText(requestPojoList.get(position).getWeight() + "  Kg");
            pdtName.setText(requestPojoList.get(position).getName());
        } else {
            pdtName.setText(requestPojoList.get(position).getWeight());
        }
        userType = requestPojoList.get(position).getUtype();
        if (userType.equals("ADMIN")) {
            layout.setVisibility(View.VISIBLE);
            uname.setText(requestPojoList.get(position).getUname());
        } else {
            layout.setVisibility(View.GONE);
        }
        pdtDesc.setText(requestPojoList.get(position).getDesc());
        pdtTotal.setText("₹" + requestPojoList.get(position).getTotal());
        p_Mode.setText(requestPojoList.get(position).getMode());

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%" + requestPojoList.get(position).getU_name());
        base = requestPojoList.get(position).getImage();

        try {
            byte[] imageAsBytes = Base64.decode(base.getBytes());
            pdtImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }
}
