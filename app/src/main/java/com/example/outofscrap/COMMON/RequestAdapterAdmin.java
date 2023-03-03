package com.example.outofscrap.COMMON;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.outofscrap.R;
import com.example.outofscrap.databinding.AdminViewUserBinding;

import java.util.List;

public class RequestAdapterAdmin extends ArrayAdapter<RequestPojo> {
    Activity context;
    List<RequestPojo> rest_List;
    String status;
    AdminViewUserBinding binding;

    public RequestAdapterAdmin(Activity context, List<RequestPojo> rest_List) {
        super(context, R.layout.admin_view_user, rest_List);
        this.context = context;
        this.rest_List = rest_List;
        // TODO Auto-generated constructor stub
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_view_user, null, true);

        TextView UserName = view.findViewById(R.id.name);
        TextView UserEmail = view.findViewById(R.id.email);
        TextView UserPhone = view.findViewById(R.id.phone);
        TextView Address = view.findViewById(R.id.address);
        ImageView statusImg = view.findViewById(R.id.imgStatus);
        TextView typeBadge = view.findViewById(R.id.imgBadge);
        statusImg.setVisibility(View.GONE);
        status = rest_List.get(position).getU_status();
        String badge = rest_List.get(position).getType();
        int g = Integer.parseInt(badge);

        if (status.equals("approved")) {
            typeBadge.setVisibility(View.VISIBLE);
        } else {
            typeBadge.setVisibility(View.GONE);
        }
        if (g <= 10) {
            typeBadge.setText("⭐");
        } else if (10 < g && g < 40) {
            typeBadge.setText("⭐⭐");

        } else if (50 < g && g < 100) {
            typeBadge.setText("⭐⭐⭐");
        }

        if (status.equals("blocked")) {
            statusImg.setVisibility(View.VISIBLE);
            statusImg.setImageResource(R.drawable.block);
        } else if (status.equals("approved")) {
            statusImg.setVisibility(View.VISIBLE);
            statusImg.setImageResource(R.drawable.approved);
        }
        UserName.setText(rest_List.get(position).getUname());
        UserEmail.setText(rest_List.get(position).getU_email());
        UserPhone.setText(rest_List.get(position).getU_phone());
        Address.setText(rest_List.get(position).getAddress());

        return view;
    }
}
