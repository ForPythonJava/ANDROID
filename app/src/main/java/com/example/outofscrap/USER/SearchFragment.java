package com.example.outofscrap.USER;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.COMMON.ProductAdapter;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.R;
import com.example.outofscrap.databinding.FragmentSearchBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    List<RequestPojo> requestPojoList;
    String key;
    Button btn_cancel, btn_okay;
    String p_Id, p_type, rate, TOTAL, amt, image, WEIGHT, DESC, SID, NAME = "NULL", SPHONE,PID;
    int tot = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        viewProducts("scrap");
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                key = binding.searchView.getQuery().toString();
                binding.noResult.setVisibility(View.GONE);
                binding.searchList.setVisibility(View.GONE);

                if (key.isEmpty()) {
                    binding.noResult.setVisibility(View.GONE);
                    binding.searchList.setVisibility(View.VISIBLE);
                    viewProducts("scrap");
                } else {
//                    binding.searchList.setVisibility(View.VISIBLE);
                    binding.noResult.setVisibility(View.GONE);
                    getResult(newText);
                }
                return true;
            }
        });
        binding.noResult.setVisibility(View.GONE);
        binding.category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewProducts("scrap");
            }
        });
        binding.category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewProducts("creativeItems");
            }
        });

        binding.searchList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
//                        AdminDeleteFood();
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
                return false;
            }

        });


        binding.searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.custom_alert_dialog, null);

//                noitem = dialoglayout.findViewById(R.id.no_item);
                btn_cancel = dialoglayout.findViewById(R.id.btn_cancel);
                btn_okay = dialoglayout.findViewById(R.id.btn_okay);


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder
                        .setCancelable(false);
                AlertDialog alert = builder.create();
                alert.setView(dialoglayout);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.cancel();
                    }
                });
                btn_okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        p_type = requestPojoList.get(position).getType();

                        p_Id = requestPojoList.get(position).getPid();

                        image = requestPojoList.get(position).getImage();

                        DESC = requestPojoList.get(position).getDesc();

                        SID = requestPojoList.get(position).getSid();

                        SPHONE = requestPojoList.get(position).getPhon();

                        System.out.println("SELLERPH" + SPHONE);
                        System.out.println(requestPojoList.get(position));

                        if (p_type.equals("creative")) {
                            TOTAL = requestPojoList.get(position).getRate();
                            WEIGHT = requestPojoList.get(position).getWeight();
                        } else {
                            NAME = requestPojoList.get(position).getName();
                            WEIGHT = requestPojoList.get(position).getWeight();
                            amt = requestPojoList.get(position).getRate();
                            tot = Integer.parseInt(requestPojoList.get(position).getWeight()) * Integer.parseInt(amt);
                            TOTAL = String.valueOf(tot);
                        }
                        System.out.println(amt + "AMOUNT");
                        System.out.println(rate + "RATE");
                        System.out.println(tot + "TOTAL");

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("SharedData", MODE_PRIVATE).edit();
                        editor.putString("can_name", "" + p_type);
                        editor.putString("seller", SPHONE);
                        editor.apply();

                        addToCartProcess();
                        alert.cancel();
                    }
                });
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
            }
        });


        return binding.getRoot();
    }

    private void addToCartProcess() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {

                    Snackbar.make(binding.getRoot(), "Added to Cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Fragment fragment = new CartFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                    transaction.commit();
//                    manager.popBackStack();
                    Intent intent = new Intent(getActivity(), UserHome.class);
                    startActivity(intent);
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
                map.put("key", "addCart");
                map.put("u_id", uid);
                map.put("p_id", p_Id);
                map.put("p_type", p_type);
                map.put("rate", TOTAL);
                map.put("image", image);
                map.put("desc", DESC);
                map.put("weight", WEIGHT);
                map.put("name", NAME);
                map.put("sid", SID);

                return map;
            }
        };
        queue.add(request);
    }

    private void getResult(String key) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    binding.noResult.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));

                    ProductAdapter productAdapter = new ProductAdapter(getActivity(), requestPojoList);
                    binding.searchList.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    registerForContextMenu(binding.searchList);
                    binding.searchList.setVisibility(View.VISIBLE);
                } else {
                    binding.noResult.setVisibility(View.VISIBLE);
                    binding.searchList.setVisibility(View.GONE);
                    Snackbar.make(getView(), "No Result Found", Snackbar.LENGTH_LONG).show();
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
                map.put("key", "searchProduct");
                map.put("keyword", key);
                map.put("h_id", uid);
                return map;
            }
        };
        queue.add(request);
    }

    public void viewProducts(String myCan) {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Gson gson = new Gson();
                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));

                    ProductAdapter productAdapter = new ProductAdapter(getActivity(), requestPojoList);
                    binding.searchList.setAdapter(productAdapter);
                    registerForContextMenu(binding.searchList);
                } else {
                    binding.noResult.setVisibility(View.VISIBLE);
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

}