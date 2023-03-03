package com.example.outofscrap.ADMIN;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.COMMON.ProductAdapter;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.R;
import com.example.outofscrap.databinding.FragmentViewProductsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewProducts extends Fragment {

    FragmentViewProductsBinding viewProductsBinding;
    List<RequestPojo> requestPojoList;
    String Fid, typ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewProductsBinding = FragmentViewProductsBinding.inflate(inflater, container, false);
        View view = viewProductsBinding.getRoot();
        viewProducts("scrap");
        viewProductsBinding.itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                String type = prefs.getString("type", "No logid");
                if (type.equals("admin")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Do you want Delete this item")
                            .setCancelable(false)
//                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Bundle bundle = new Bundle();
//                                    RequestPojo requestPojo = requestPojoList.get(position);
//                                    bundle.putParcelable("clicked_item", requestPojo);
//                                    UpdateFoodFragment fragment = new UpdateFoodFragment();
//                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                                    fragment.setArguments(bundle);
//                                    transaction.replace(R.id.nav_host_fragment, fragment);
//                                    transaction.commit();
//                                }
//                            }
//                )
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Fid = requestPojoList.get(position).getPid();
                                    typ = requestPojoList.get(position).getType();
                                    System.out.println(typ);
                                    AdminDeleteFood(typ);
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Out of Scrap");
                    alert.show();
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                    alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        viewProductsBinding.category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewProducts("scrap");
            }
        });
        viewProductsBinding.category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewProducts("creativeItems");
            }
        });

        return view;
    }

    //sample code start
    public void viewProducts(String myCan) {

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Gson gson = new Gson();
                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));

                    ProductAdapter productAdapter = new ProductAdapter(getActivity(), requestPojoList);
                    viewProductsBinding.itemList.setAdapter(productAdapter);
                    registerForContextMenu(viewProductsBinding.itemList);
                } else {
                    viewProductsBinding.noDataImg.setVisibility(View.VISIBLE);
                    Snackbar.make(getView(), "No Items Added Yet", Snackbar.LENGTH_LONG).show();
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
                if (myCan.equals("scrap")) {
                    map.put("key", "getScrap");
                } else {
                    map.put("key", "getCreativeItem");
                }
                map.put("h_id", uid);
                return map;
            }
        };
        queue.add(request);
    }
    //sample end


    public void AdminDeleteFood(String type) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {

                    System.out.println(response);
                    Snackbar.make(getView(), "Deleted Successfully", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Fragment fragment = new ViewProducts();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
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
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "deleteItem");
                map.put("fd_id", Fid);
                map.put("type", typ);

                return map;
            }
        };
        queue.add(request);
    }
}