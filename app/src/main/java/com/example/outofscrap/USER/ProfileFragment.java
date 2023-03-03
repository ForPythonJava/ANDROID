package com.example.outofscrap.USER;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.outofscrap.databinding.FragmentUserProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;

    String UNAME, UEMAIL, UPHONE, UPASS, ADDRESS;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);

        getData();

        binding.updateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.btnUpdate.setVisibility(View.VISIBLE);
                    binding.userProfName.setEnabled(true);
                    binding.userProfPhone.setEnabled(true);
                    binding.userProfEmail.setEnabled(true);
                    binding.userProfPass.setEnabled(true);
                    binding.userAddress.setEnabled(true);
                } else {
                    binding.btnUpdate.setVisibility(View.GONE);
                    binding.userProfName.setEnabled(false);
                    binding.userProfPhone.setEnabled(false);
                    binding.userProfEmail.setEnabled(false);
                    binding.userProfPass.setEnabled(false);
                    binding.userAddress.setEnabled(false);
                }
            }
        });
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        return binding.getRoot();
    }

    private void validate() {
        UNAME = binding.userProfName.getEditText().getText().toString().trim();
        UEMAIL = binding.userProfEmail.getEditText().getText().toString().trim();
        UPHONE = binding.userProfPhone.getEditText().getText().toString().trim();
        UPASS = binding.userProfPass.getEditText().getText().toString().trim();
        ADDRESS = binding.userAddress.getEditText().getText().toString().trim();
        String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";

        if (UNAME.isEmpty()) {
            binding.userProfName.setError("Enter Name");
            binding.userProfName.requestFocus();
        } else if (UEMAIL.isEmpty()) {
            binding.userProfName.setError(null);
            binding.userProfEmail.setError("Enter Email");
            binding.userProfEmail.requestFocus();
        } else if (!UEMAIL.matches(emailPattern)) {
            binding.userProfEmail.setError(null);
            binding.userProfEmail.setError("Invalid Email");
            binding.userProfEmail.requestFocus();
        } else if (UPHONE.length() != 10) {
            binding.userProfEmail.setError(null);
            binding.userProfPhone.setError("Enter Phone");
            binding.userProfPhone.requestFocus();
        } else if (ADDRESS.isEmpty()) {
            binding.userProfPhone.setError(null);
            binding.userAddress.setError("Enter Address");
            binding.userAddress.requestFocus();
        } else if (UPASS.isEmpty()) {
            binding.userAddress.setError(null);
            binding.userProfPass.setError("Enter Password");
            binding.userProfPass.requestFocus();
        } else {
            binding.userProfPass.setError(null);
            register();
        }
    }

    private void register() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Fragment fragment = new ProfileFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                    transaction.commit();
                    manager.popBackStack();
                    Snackbar.make(binding.getRoot(), "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();
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
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "updateProfile");
                map.put("uid", uid);
                map.put("uname", UNAME);
                map.put("uemail", UEMAIL);
                map.put("uphone", UPHONE);
                map.put("upass", UPASS);
                map.put("address", ADDRESS);
                return map;
            }
        };
        queue.add(request);
    }


    private void getData() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    String[] arr = response.trim().split(":");
                    binding.userProfName.getEditText().setText(arr[1]);
                    binding.userProfEmail.getEditText().setText(arr[2]);
                    binding.userProfPhone.getEditText().setText(arr[3]);
                    binding.userAddress.getEditText().setText(arr[4]);
                    binding.userProfPass.getEditText().setText(arr[5]);

                } else {
                    Snackbar.make(getView(), "Something went wrong", Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "my Error :" + error, Toast.LENGTH_LONG).show();
                Log.i("My Error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "viewProfile");
                map.put("uid", uid);
                return map;
            }
        };
        queue.add(request);
    }
}