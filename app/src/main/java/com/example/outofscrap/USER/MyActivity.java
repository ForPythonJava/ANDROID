package com.example.outofscrap.USER;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.ADMIN.AdminHome;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.databinding.FragmentMyActivityBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class MyActivity extends Fragment {

    FragmentMyActivityBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyActivityBinding.inflate(getLayoutInflater());
        getAllActivity();
        return binding.getRoot();
    }


    private void getAllActivity() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                String val = response.trim();
                if (!response.trim().equals("failed")) {
                    String data = response;
                    String[] arr = data.trim().split(":");

binding.totalAmount.setText(arr[0]);
                    binding.totalOrders.setText(arr[1]);
                    binding.totalPurchases.setText(arr[2]);

                } else {
                    Snackbar.make(binding.getRoot(), "Login failed..!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "my error :" + error, Toast.LENGTH_LONG).show();
                Log.i("My error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "MyAllActivities");
                map.put("uid", uid);
                return map;
            }
        };
        queue.add(request);
    }
}