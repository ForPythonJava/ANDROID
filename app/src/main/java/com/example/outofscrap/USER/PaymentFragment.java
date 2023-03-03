package com.example.outofscrap.USER;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.R;
import com.example.outofscrap.databinding.FragmentPaymentBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentFragment extends Fragment {

    FragmentPaymentBinding paymentBinding;
    RequestPojo requestPojo;
    String TOT, CART_ID;
    String CARD_NUMBER, UNAME, EXPIRY_DATE, CVV, PAY_MODE;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        paymentBinding = FragmentPaymentBinding.inflate(getLayoutInflater());
        View view = paymentBinding.getRoot();

        paymentBinding.cardExpiry.getEditText().addTextChangedListener(tw);

        final Bundle b = this.getArguments();
        requestPojo = b.getParcelable("clicked_item");
        TOT = b.getString("totalAMT");
        CART_ID = b.getString("payID");
        PAY_MODE = b.getString("mode");
        System.out.println(PAY_MODE + "**************************");

        paymentBinding.cartAmount.getEditText().setText(TOT);
        paymentBinding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();

            }
        });
        return view;
    }

    TextWatcher tw = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int x = 2; x <= cl && x < 6; x += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day = Integer.parseInt(clean.substring(0, 2));
                    int mon = Integer.parseInt(clean.substring(2, 4));
                    int year = Integer.parseInt(clean.substring(4, 8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon - 1);
                    year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                    clean = String.format("%02d%02d%02d", day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                paymentBinding.cardExpiry.getEditText().setText(current);
                paymentBinding.cardExpiry.getEditText().setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }


    };

    public void validate() {
        CARD_NUMBER = paymentBinding.cardNumber.getEditText().getText().toString().trim();
        UNAME = paymentBinding.cardName.getEditText().getText().toString().trim();
        EXPIRY_DATE = paymentBinding.cardExpiry.getEditText().getText().toString().trim();
        CVV = paymentBinding.cardCvv.getEditText().getText().toString().trim();
        if (CARD_NUMBER.isEmpty()) {
            paymentBinding.cardNumber.requestFocus();
            paymentBinding.cardNumber.setError("Enter Card Number");
        } else if (CARD_NUMBER.length() < 16) {
            paymentBinding.cardNumber.setError(null);
            paymentBinding.cardNumber.requestFocus();
            paymentBinding.cardNumber.setError("Invalid Card Number");
        } else if (UNAME.isEmpty()) {
            paymentBinding.cardNumber.setError(null);
            paymentBinding.cardName.requestFocus();
            paymentBinding.cardName.setError("Enter Name");
        } else if (EXPIRY_DATE.isEmpty()) {
            paymentBinding.cardName.setError(null);
            paymentBinding.cardExpiry.requestFocus();
            paymentBinding.cardExpiry.setError("Enter Expiry Date");
        } else if (CVV.isEmpty()) {
            paymentBinding.cardExpiry.setError(null);
            paymentBinding.cardCvv.requestFocus();
            paymentBinding.cardCvv.setError("Enter CVV");
        } else {
            paymentBinding.cardCvv.setError(null);
            MakePayment();
        }
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
                    Snackbar.make(getView(), "Payment Successful", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Fragment fragment = new ConfirmationFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
                    transaction.commit();
                    manager.popBackStack();
                } else {
                    Snackbar.make(getView(), "Payment Failed", Snackbar.LENGTH_LONG)
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
                SharedPreferences prefs = getContext().getSharedPreferences("SharedData", MODE_PRIVATE);
                final String uid = prefs.getString("logid", "No logid");
                final String type = prefs.getString("type", "No logid");
                map.put("key", "pay");
                map.put("cart_id", CART_ID);
                map.put("total", TOT);
                map.put("mode", PAY_MODE);
                return map;
            }
        };
        queue.add(request);
    }

}