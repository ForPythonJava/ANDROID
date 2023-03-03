package com.example.outofscrap.USER;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.outofscrap.COMMON.CartAdapter;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.R;
import com.example.outofscrap.databinding.FragmentCartBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    RadioButton radioButton;
    int totalamount = 0;
    String cart_id, pay_mode = "", CARTID = "";
    List<RequestPojo> requestPojoList;
    LinearLayout qtylayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        viewCart();
        binding.rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                      @Override
                                                      public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                          radioButton = group.findViewById(checkedId);

                                                          pay_mode = radioButton.getText().toString();
                                                          binding.cod.setError(null);
                                                      }
                                                  }
        );

        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pay_mode.isEmpty()) {
                    binding.online.requestFocus();
                    binding.cod.setError("Hello");
                    Toast.makeText(getContext(), "Please Choose Payment Method", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("################ after else########################" + pay_mode);

                    if (pay_mode.equals("Online")) {
                        Bundle bundle = new Bundle();
                        RequestPojo requestPojo = requestPojoList.get(0);
                        bundle.putParcelable("clicked_item", requestPojo);
                        bundle.putString("totalAMT", "" + totalamount);
                        bundle.putString("payID", CARTID);
                        bundle.putString("mode", pay_mode);
                        System.out.println("##################if######################" + pay_mode);

                        PaymentFragment fragment = new PaymentFragment();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                        transaction.commit();
                        manager.popBackStack();
                    } else {
                        MakePayment();
                    }
                }
            }
        });

        return binding.getRoot();
    }

    private void MakePayment() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                String val = response.trim();
                if (!response.trim().equals("failed")) {
                    System.out.println(response);

                    Snackbar.make(getView(), "Payment Successful", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Fragment fragment = new ConfirmationFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                    transaction.commit();
                    manager.popBackStack();
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Payment Failed", Snackbar.LENGTH_LONG)
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
                map.put("key", "pay");
                map.put("cart_id", CARTID);
                map.put("mode", pay_mode);
                return map;
            }
        };
        queue.add(request);
    }


    public void viewCart() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Gson gson = new Gson();

                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));
                    CartAdapter adapter = new CartAdapter(getActivity(), requestPojoList);
                    binding.cartList.setAdapter(adapter);
                    registerForContextMenu(binding.cartList);

                    //Cart Total
                    for (RequestPojo item : requestPojoList) {
                        totalamount += Integer.parseInt(item.getTotal().trim());
                    }
                    binding.txtAmtTotal.append(String.valueOf(totalamount));
                    //Cart Total End
                    //getting multiple cart id
                    for (RequestPojo item : requestPojoList) {
                        CARTID += item.getCart_id() + "#";
                    }
                    binding.cartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Would you like to take something out of your cart?")
                                    .setCancelable(false)
                                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            cart_id = requestPojoList.get(i).getCart_id();
                                            RemoveFromCart();
//                                            Toast.makeText(getContext(), "card id"+cart_id, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Click And Eat");
                            alert.show();
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                            alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));


                        }
                    });

                    //getting multiple cart id end

                } else {
                    binding.noDataImg.setVisibility(View.VISIBLE);
                    binding.btnPay.setVisibility(View.GONE);
                    binding.cartList.setVisibility(View.GONE);
                    binding.rLayout.setVisibility(View.GONE);
                    binding.rGroup.setVisibility(View.GONE);
//                    binding.linearL.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Snackbar.make(getView(), "Cart is Empty", Snackbar.LENGTH_LONG).show();
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
                map.put("key", "viewCart");
                map.put("u_id", uid);

                return map;
            }
        };
        queue.add(request);
    }

    private void RemoveFromCart() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {

                    System.out.println(response);
                    Snackbar.make(getView(), "Item Removed From Cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Fragment fragment = new CartFragment();
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
                final String uid = prefs.getString("u_id", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "deleteProductCart");
                map.put("cart_id", cart_id);

                return map;
            }
        };
        queue.add(request);
    }
}