package com.example.outofscrap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.databinding.ActivityRegistrationBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding registrationBinding;

    String UNAME, UEMAIL, UPHONE, UPASS, ADDRESS, CONFIRM_PASS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Toolbar toolbar = new Toolbar(this);
        toolbar.setVisibility(View.INVISIBLE);
        registrationBinding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = registrationBinding.getRoot();
        setContentView(view);

        registrationBinding.txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        registrationBinding.btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    private void validate() {
        UNAME = registrationBinding.userName.getEditText().getText().toString().trim();
        UEMAIL = registrationBinding.userMail.getEditText().getText().toString().trim();
        UPHONE = registrationBinding.userPhone.getEditText().getText().toString().trim();
        ADDRESS = registrationBinding.userAddress.getEditText().getText().toString().trim();
        UPASS = registrationBinding.userPass.getEditText().getText().toString().trim();
        CONFIRM_PASS = registrationBinding.cnfUserPass.getEditText().getText().toString().trim();
        String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";

        if (UNAME.isEmpty()) {
            registrationBinding.userName.setError("Enter Name");
            registrationBinding.userName.requestFocus();
        } else if (UEMAIL.isEmpty()) {
            registrationBinding.userName.setError(null);
            registrationBinding.userMail.setError("Enter Email");
            registrationBinding.userMail.requestFocus();
        } else if (!UEMAIL.matches(emailPattern)) {
            registrationBinding.userMail.setError(null);
            registrationBinding.userMail.setError("Invalid Email");
            registrationBinding.userMail.requestFocus();
        } else if (UPHONE.length() != 10) {
            registrationBinding.userMail.setError(null);
            registrationBinding.userPhone.setError("Enter Valid Number");
            registrationBinding.userPhone.requestFocus();
        } else if (ADDRESS.isEmpty()) {
            registrationBinding.userPhone.setError(null);
            registrationBinding.userAddress.setError("Enter Address");
            registrationBinding.userAddress.requestFocus();
        } else if (UPASS.isEmpty()) {
            registrationBinding.userPhone.setError(null);
            registrationBinding.userAddress.setError("Enter Password");
            registrationBinding.userPass.requestFocus();
        } else if (CONFIRM_PASS.isEmpty()) {
            registrationBinding.cnfUserPass.setError(null);
            registrationBinding.cnfUserPass.setError("Confirm Password");
            registrationBinding.cnfUserPass.requestFocus();
        } else if (!UPASS.equals(CONFIRM_PASS)) {
            registrationBinding.cnfUserPass.setError("Password doesn't matches");
            registrationBinding.cnfUserPass.requestFocus();
        } else {
            register();
        }
    }

    private void register() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {
                    Toast.makeText(RegistrationActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
                    Snackbar.make(registrationBinding.getRoot(), response, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "my error :" + error, Toast.LENGTH_LONG).show();
                Log.i("My error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "regUser");
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

}