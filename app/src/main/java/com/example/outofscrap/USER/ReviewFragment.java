package com.example.outofscrap.USER;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.R;
import com.example.outofscrap.databinding.FragmentReviewBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;


public class ReviewFragment extends Fragment {

    FragmentReviewBinding reviewBinding;
    String RATING, REVIEW, CARTID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Inflate the layout for this fragment
        reviewBinding = FragmentReviewBinding.inflate(getLayoutInflater());


        final Bundle b = this.getArguments();
        CARTID = b.getString("clicked_item");
        System.out.println("####################" + CARTID);
        reviewBinding.btnSubReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        return reviewBinding.getRoot();
    }

    private void validate() {
        RATING = String.valueOf(reviewBinding.rBar.getRating());
        REVIEW = reviewBinding.reviewProduct.getEditText().getText().toString().trim();

        if (RATING.isEmpty()) {
            Snackbar.make(getView(), "Please Provide Rating", Snackbar.LENGTH_LONG).show();
        } else if (REVIEW.isEmpty()) {
            reviewBinding.reviewProduct.setError("Provide Review");
        } else {
            submitReview();
        }
    }

    private void submitReview() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    MyOrders fragment = new MyOrders();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                    transaction.commit();
                    manager.popBackStack();
                    Snackbar snackbar = Snackbar
                            .make(reviewBinding.getRoot(), "Review Updated", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar.make(reviewBinding.getRoot(), response, Snackbar.LENGTH_LONG)
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
                Map<String, String> map = new HashMap<String, String>();
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                map.put("key", "addReview");
                map.put("rating", RATING);
                map.put("review", REVIEW);
                map.put("cart_id", CARTID);
                return map;
            }
        };
        queue.add(request);
    }
}
