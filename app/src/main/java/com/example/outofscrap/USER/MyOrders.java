package com.example.outofscrap.USER;

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
import com.example.outofscrap.COMMON.OrderAdapter;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.R;
import com.example.outofscrap.databinding.FragmentMyOrdersBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyOrders extends Fragment {

    private FragmentMyOrdersBinding myOrderBinding;
    List<RequestPojo> requestPojoList;
    String cartid, review_status;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        myOrderBinding = FragmentMyOrdersBinding.inflate(inflater, container, false);
        viewOrders();
        myOrderBinding.orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                review_status = requestPojoList.get(i).getReview();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (review_status.equals("noReview")) {
                    builder.setMessage("Can you please share your thoughts about our product by leaving a review?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    cartid = requestPojoList.get(i).getCart_id();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("clicked_item", cartid);
                                    ReviewFragment fragment = new ReviewFragment();
                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    fragment.setArguments(bundle);
                                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                                    transaction.commit();
                                    manager.popBackStack();
                                }
                            }).setNeutralButton("Not Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Out Of Scrap");
                    alert.show();
                } else {

                    builder.setMessage("Thank you for taking the time to review the product. Your feedback is greatly appreciated!")
                            .setCancelable(false)
                            .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
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
        return myOrderBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserHome) getActivity()).getSupportActionBar().show();
    }

    public void viewOrders() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Gson gson = new Gson();
                    requestPojoList = Arrays.asList(gson.fromJson(response, RequestPojo[].class));
                    OrderAdapter adapter = new OrderAdapter(getActivity(), requestPojoList);
                    myOrderBinding.orderList.setAdapter(adapter);
                    registerForContextMenu(myOrderBinding.orderList);
                } else {
                    myOrderBinding.orderEmpty.setVisibility(View.VISIBLE);
                    Snackbar.make(getView(), "No Orders Yet", Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                Toast.makeText(getContext(), "my Error :" + error, Toast.LENGTH_LONG).show();
                Log.i("My Error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                final String uid = prefs.getString("logid", "No logid");//"No name defined" is the default value.
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "viewOrderUser");
                map.put("u_id", uid);

                return map;
            }
        };
        queue.add(request);
    }
}