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
import com.example.outofscrap.USER.UpdateProduct;
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
    String Fid, typ, PID, typee;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewProductsBinding = FragmentViewProductsBinding.inflate(inflater, container, false);
        View view = viewProductsBinding.getRoot();
        SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
        final String type = prefs.getString("type", "No logid");//"No name defined" is the default value.
        if (type.equals("admin")) {
            viewProducts("scrap");
        } else {
            viewProducts("scrapUser");
        }
        viewProductsBinding.itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                String type = prefs.getString("type", "No logid");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (type.equals("admin")) {
                    builder.setMessage("Do you want Delete this item")
                            .setCancelable(false)
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
                } else {
                    builder.setMessage("Do you want to Update or Delete")
                            .setCancelable(false)
                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Bundle bundle = new Bundle();
                                    RequestPojo requestPojo = requestPojoList.get(position);
                                    bundle.putParcelable("clicked_item", requestPojo);
                                    UpdateProduct fragment = new UpdateProduct();
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    fragment.setArguments(bundle);
                                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                                    transaction.commit();
                                }
                            }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PID = requestPojoList.get(position).getPid();
                            typee = requestPojoList.get(position).getType();
                            UserDeleteProduct(PID, typee);
                        }
                    })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Out Of Scrap");
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

    private void UserDeleteProduct(String pid, String type) {
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
                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
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
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                if (typee.equals("scrap")) {
                    map.put("key", "deleteScrapUser");
                    map.put("pid", pid);
                    map.put("type", type);
                    map.put("uid", uid);
                }else{
                    map.put("key", "deleteItemUser");
                    map.put("pid", pid);
                    map.put("type", type);
                    map.put("uid", uid);
                }
                return map;
            }
        };
        queue.add(request);
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
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                final String type = prefs.getString("type", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                if (type.equals("admin")) {
                    if (myCan.equals("scrap")) {
                        map.put("key", "getScrap");
                    } else {
                        map.put("key", "getCreativeItem");
                    }
                } else {
                    if (myCan.equals("scrapUser")) {
                        map.put("uid", uid);
                        map.put("key", "getScrapUser");
                    } else {
                        map.put("uid", uid);
                        map.put("key", "getCreativeItemUser");
                    }
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