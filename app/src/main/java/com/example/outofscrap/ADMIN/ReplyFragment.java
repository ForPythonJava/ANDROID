package com.example.outofscrap.ADMIN;

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
import com.example.outofscrap.USER.ProfileFragment;
import com.example.outofscrap.databinding.FragmentReplyBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class ReplyFragment extends Fragment {

    FragmentReplyBinding binding;
    String fid, REPLY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReplyBinding.inflate(getLayoutInflater());

        final Bundle b = this.getArguments();
        fid = b.getString("fid");

        binding.btnSendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        return binding.getRoot();
    }

    private void validate() {
        REPLY = binding.adminReply.getEditText().getText().toString().trim();

        if (REPLY.isEmpty()) {
            binding.adminReply.setError("This Field Is Required");
            binding.adminReply.requestFocus();
        } else {
            binding.adminReply.setError(null);
            sendReply();
        }
    }

    private void sendReply() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_admin_home, fragment);
                    transaction.commit();
                    manager.popBackStack();
                    Snackbar snackbar = Snackbar
                            .make(binding.getRoot(), "Feedback Sent", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar.make(binding.getRoot(), response, Snackbar.LENGTH_LONG)
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
                map.put("key", "replyUser");
                map.put("reply", REPLY);
                map.put("fid", fid);
                return map;
            }
        };
        queue.add(request);
    }


}