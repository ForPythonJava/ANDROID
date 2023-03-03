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
import com.example.outofscrap.COMMON.FeedbackAdapter;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.R;
import com.example.outofscrap.databinding.FragmentFeedbackBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    FragmentFeedbackBinding feedbackBinding;
    String TITLE, FEEDBACK;
    List<RequestPojo> requestPojoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        feedbackBinding = FragmentFeedbackBinding.inflate(getLayoutInflater());
        feedbackBinding.btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
        viewFeedback();
        feedbackBinding.feedbackList.setVisibility(View.GONE);
        return feedbackBinding.getRoot();
    }

    private void validate() {
        TITLE = feedbackBinding.feedbackTitle.getEditText().getText().toString().trim();
        FEEDBACK = feedbackBinding.feedback.getEditText().getText().toString().trim();

        if (TITLE.isEmpty()) {
            feedbackBinding.feedbackTitle.setError("This Field Is Required");
            feedbackBinding.feedbackTitle.requestFocus();
        } else if (FEEDBACK.isEmpty()) {
            feedbackBinding.feedbackTitle.setError(null);
            feedbackBinding.feedback.setError("This Field Is Required");
            feedbackBinding.feedback.requestFocus();
        } else {
            feedbackBinding.feedback.setError(null);
            sendFeedback();
        }
    }

    private void sendFeedback() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                    transaction.commit();
                    manager.popBackStack();
                    Snackbar snackbar = Snackbar
                            .make(feedbackBinding.getRoot(), "Feedback Sent", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar.make(feedbackBinding.getRoot(), response, Snackbar.LENGTH_LONG)
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
                map.put("key", "addFeedback");
                map.put("title", TITLE);
                map.put("feedback", FEEDBACK);
                map.put("uid", uid);
                return map;
            }
        };
        queue.add(request);
    }

    private void viewFeedback() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Gson gson = new Gson();
                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));
                    FeedbackAdapter adapter = new FeedbackAdapter(getActivity(), requestPojoList);
                    feedbackBinding.feedbackList.setAdapter(adapter);
                    registerForContextMenu(feedbackBinding.feedbackList);

                    feedbackBinding.feedbackList.setVisibility(View.VISIBLE);
                } else {
                    feedbackBinding.feedbackList.setVisibility(View.GONE);
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
                map.put("key", "viewFeedback");
                map.put("uid", uid);
                return map;
            }
        };
        queue.add(request);
    }
}