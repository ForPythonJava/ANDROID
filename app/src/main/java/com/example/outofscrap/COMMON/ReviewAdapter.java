package com.example.outofscrap.COMMON;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.outofscrap.R;

import java.io.IOException;
import java.util.List;

public class ReviewAdapter extends ArrayAdapter<RequestPojo> {
    Activity context;
    List<RequestPojo> requestPojoList;
    String type, base, userType;

    public ReviewAdapter(Activity context, List<RequestPojo> requestPojoList) {
        super(context, R.layout.view_review_layout, requestPojoList);
        this.context = context;
        this.requestPojoList = requestPojoList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_review_layout, null, true);

        TextView pdtName = view.findViewById(R.id.pdtName);
        TextView USERNAME = view.findViewById(R.id.username);
        TextView REVIEW = view.findViewById(R.id.review);
        TextView RATING = view.findViewById(R.id.rating);

        ImageView pdtImage = view.findViewById(R.id.pdtImage);

        type = requestPojoList.get(position).getType();
        if (type.equals("scrap")) {
            pdtName.setText(requestPojoList.get(position).getName());
        } else {
            pdtName.setText(requestPojoList.get(position).getWeight());
        }
//        userType = requestPojoList.get(position).getUtype();
//        if (userType.equals("ADMIN")) {
//            layout.setVisibility(View.VISIBLE);
//            uname.setText(requestPojoList.get(position).getUname());
//        } else {
//            layout.setVisibility(View.GONE);
//        }
        USERNAME.setText(requestPojoList.get(position).getUname());
        REVIEW.setText(requestPojoList.get(position).getReview());
        RATING.setText(requestPojoList.get(position).getRating()+"⭐");

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
