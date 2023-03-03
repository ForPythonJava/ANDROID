package com.example.outofscrap.USER;

import static android.content.Context.MODE_PRIVATE;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.outofscrap.databinding.FragmentConfirmationBinding;

import java.util.ArrayList;

public class ConfirmationFragment extends Fragment {

    FragmentConfirmationBinding confirmationBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        confirmationBinding = FragmentConfirmationBinding.inflate(getLayoutInflater());
        View view = confirmationBinding.getRoot();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                MyOrders fragment = new MyOrders();
//                FragmentManager manager = getActivity().getSupportFragmentManager();
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
//                transaction.commit();
//                manager.popBackStack();
                SendSMS();
                Intent intent = new Intent(getActivity(), UserHome.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Order Placed Successfully", Toast.LENGTH_SHORT).show();
            }
        }, 3000);

        return view;
    }

    private void SendSMS() {
        SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
        final String no = prefs.getString("phone", "No logid");
        final String email = prefs.getString("email", "No logid");
        final String uname = prefs.getString("uname", "No logid");
        final String address = prefs.getString("address", "No logid");
        final String seller = prefs.getString("seller", "No logid");

        System.out.println(seller + "Meeeee");
        String msg = "Hey You have a New Order....!!!😊\nName:" + uname + "\nEmail:" + email + "\nPhone:" + no + "\nAddress:\n" + address;
        System.out.println(msg);

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(msg);
        int numParts = parts.size();

        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(numParts);
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>(numParts);

        for (int i = 0; i < numParts; i++) {
            Intent sentIntent = new Intent("SMS_SENT");
            Intent deliveryIntent = new Intent("SMS_DELIVERED");
            sentIntents.add(PendingIntent.getBroadcast(getContext(), 0, sentIntent, 0));
            deliveryIntents.add(PendingIntent.getBroadcast(getContext(), 0, deliveryIntent, 0));
        }
        smsManager.sendMultipartTextMessage(seller, null, parts, sentIntents, deliveryIntents);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserHome) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((UserHome) getActivity()).getSupportActionBar().show();
    }


}