package com.example.outofscrap.USER;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.ReviewAdapter;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.databinding.FragmentViewReviewBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewReviewFragment extends Fragment {

    FragmentViewReviewBinding reviewBinding;
    List<RequestPojo> requestPojoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reviewBinding = FragmentViewReviewBinding.inflate(getLayoutInflater());

        viewReview();
        return reviewBinding.getRoot();
    }

    private void viewReview() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Gson gson = new Gson();
                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));
                    ReviewAdapter adapter = new ReviewAdapter(getActivity(), requestPojoList);
                    reviewBinding.reviewList.setAdapter(adapter);
                    registerForContextMenu(reviewBinding.reviewList);
                } else {
                    reviewBinding.reviewList.setVisibility(View.VISIBLE);
                    Snackbar.make(getView(), "No Reviews Yet", Snackbar.LENGTH_LONG).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("My Error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "viewReview");
//                map.put("u_id", uid);
//                map.put("pid", uid);

                return map;
            }
        };
        queue.add(request);
    }

}