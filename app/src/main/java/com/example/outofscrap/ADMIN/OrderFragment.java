package com.example.outofscrap.ADMIN;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.COMMON.OrderAdapter;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.USER.UserHome;
import com.example.outofscrap.databinding.FragmentOrderAdminBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderFragment extends Fragment {

    private FragmentOrderAdminBinding binding;

    List<RequestPojo> requestPojoList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOrderAdminBinding.inflate(inflater, container, false);
        viewOrders();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AdminHome) getActivity()).getSupportActionBar().show();
    }

    public void viewOrders() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Gson gson = new Gson();
                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));
                    OrderAdapter adapter = new OrderAdapter(getActivity(), requestPojoList);
                    binding.orderList.setAdapter(adapter);
                    registerForContextMenu(binding.orderList);
                } else {
                    binding.orderEmpty.setVisibility(View.VISIBLE);
                    Snackbar.make(getView(), "No Orders Yet", Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                Toast.makeText(getContext(), "my Error :" + error, Toast.LENGTH_LONG).show();
                Log.i("My Error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "viewOrderAdmin");
//                map.put("u_id", uid);

                return map;
            }
        };
        queue.add(request);
    }
}