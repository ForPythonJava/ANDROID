package com.example.outofscrap.ADMIN;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.outofscrap.databinding.FragmentAdminViewFeedbackBinding;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminViewFeedback extends Fragment {


    FragmentAdminViewFeedbackBinding binding;
    List<RequestPojo> requestPojoList;
    String fid, reply;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminViewFeedbackBinding.inflate(getLayoutInflater());
        viewFeedback();

        binding.feedbackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                reply = requestPojoList.get(position).getReply();
                if (reply.equals("NoReply")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Reply for this query?")
                            .setCancelable(false)
                            .setPositiveButton("Reply", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    fid = requestPojoList.get(position).getFid();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("fid", fid);
                                    ReplyFragment fragment = new ReplyFragment();
                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    fragment.setArguments(bundle);
                                    transaction.replace(R.id.nav_host_fragment_content_admin_home, fragment);
                                    transaction.commit();
                                    manager.popBackStack();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Out Of Scrap");
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Already Replied")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Out Of Scrap");
                    alert.show();
                }
            }
        });

        return binding.getRoot();
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
                    binding.feedbackList.setAdapter(adapter);
                    registerForContextMenu(binding.feedbackList);

                    binding.feedbackList.setVisibility(View.VISIBLE);
                } else {
                    binding.feedbackList.setVisibility(View.GONE);
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
                map.put("key", "viewFeedbackAdmin");
                return map;
            }
        };
        queue.add(request);
    }
}