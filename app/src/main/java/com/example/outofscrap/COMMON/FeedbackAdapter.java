package com.example.outofscrap.COMMON;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.outofscrap.R;

import java.util.List;

public class FeedbackAdapter extends ArrayAdapter<RequestPojo> {
    Activity context;
    List<RequestPojo> requestPojoList;
    String Reply;

    public FeedbackAdapter(Activity context, List<RequestPojo> requestPojoList) {
        super(context, R.layout.view_feedbacks, requestPojoList);
        this.context = context;
        this.requestPojoList = requestPojoList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_feedbacks, null, true);

        TextView title = view.findViewById(R.id.txt_title);
        TextView feedback = view.findViewById(R.id.txt_feedback);
        TextView username = view.findViewById(R.id.txt_Username);
        TextView reply = view.findViewById(R.id.txt_reply);
        LinearLayout layout = view.findViewById(R.id.linear_username);
        LinearLayout replyLayout = view.findViewById(R.id.linear_reply);

        replyLayout.setVisibility(View.GONE);

        SharedPreferences prefs = getContext().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        final String type = prefs.getString("type", "No logid");//"No name defined" is the default value.
        if (type.equals("user")) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }
        Reply = requestPojoList.get(position).getReply();
        if (Reply.equals("NoReply")) {
            replyLayout.setVisibility(View.GONE);
        } else {
            replyLayout.setVisibility(View.VISIBLE);
            reply.setText(Reply);
        }

        title.setText(requestPojoList.get(position).getTitle());
        feedback.setText(requestPojoList.get(position).getFeedback());
        username.setText(requestPojoList.get(position).getUname());

        return view;
    }
}