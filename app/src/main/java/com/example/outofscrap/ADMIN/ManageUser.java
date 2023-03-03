package com.example.outofscrap.ADMIN;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.COMMON.RequestAdapterAdmin;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.R;
import com.example.outofscrap.databinding.FragmentManageUserBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageUser extends Fragment {

    private FragmentManageUserBinding binding;
    List<RequestPojo> requestPojoList;
    String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentManageUserBinding.inflate(inflater, container, false);

        viewUsers();

        binding.userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                String log_type = requestPojoList.get(position).getU_status();
                System.out.println("type"+log_type);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (log_type.equals("pending")) {
                    builder.setMessage("Select an Option?")
                            .setCancelable(false)
                            .setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    userId = requestPojoList.get(position).getU_id();
                                    manageUser("approve");
                                }
                            }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            userId = requestPojoList.get(position).getU_id();
                            manageUser("reject");
                        }
                    }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Out Of Scrap");
                    alert.show();
                } else if (log_type.equals("blocked")) {
                    builder.setMessage("Are you sure you want to Unblock this user..?")
                            .setCancelable(false)
                            .setPositiveButton("Unblock", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    userId = requestPojoList.get(position).getU_id();
                                    manageUser("unblock");
                                }
                            }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Out Of Scrap");
                    alert.show();
                } else {
                    builder.setMessage("Are you sure you want to block this user..?")
                            .setCancelable(false)
                            .setPositiveButton("Block", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    userId = requestPojoList.get(position).getU_id();
                                    manageUser("block");
                                }
                            }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
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

    private void manageUser(String type) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    String res = response.trim();
                    Snackbar.make(getView(), res, Snackbar.LENGTH_LONG).show();
                    ManageUser fragment = new ManageUser();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_admin_home, fragment);
                    transaction.commit();
                    manager.popBackStack();
                } else {
                    Toast.makeText(getContext(), "failed", Toast.LENGTH_LONG).show();
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
                final String uid = prefs.getString("u_id", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "manageUser");
                map.put("uid", userId);
                map.put("type", type);
                return map;
            }
        };
        queue.add(request);

    }


    public void viewUsers() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Gson gson = new Gson();
                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));

                    RequestAdapterAdmin adapter = new RequestAdapterAdmin(getActivity(), requestPojoList);
                    binding.userList.setAdapter(adapter);
                    registerForContextMenu(binding.userList);
                } else {
                    binding.imgTop.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No Data", Toast.LENGTH_LONG).show();
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
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "pendingUser");
                return map;
            }
        };
        queue.add(request);
    }
}