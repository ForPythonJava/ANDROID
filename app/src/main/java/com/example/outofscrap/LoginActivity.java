package com.example.outofscrap;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.outofscrap.ADMIN.AdminHome;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.USER.UserHome;
import com.example.outofscrap.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding loginBinding;
    String EMAIL, PASS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Toolbar toolbar = new Toolbar(this);
        toolbar.setVisibility(View.INVISIBLE);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = loginBinding.getRoot();
        setContentView(view);

        loginBinding.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
        loginBinding.txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
    }

    private void validate() {
        EMAIL = loginBinding.logMail.getEditText().getText().toString().trim();
        PASS = loginBinding.logPass.getEditText().getText().toString().trim();
        String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
        if (EMAIL.isEmpty()) {
            loginBinding.logMail.setError("Enter Email");
            loginBinding.logMail.requestFocus();
        } else if (!EMAIL.matches(emailPattern)) {
            loginBinding.logMail.setError(null);
            loginBinding.logMail.setError("Invalid Email");
            loginBinding.logMail.requestFocus();
        } else if (PASS.isEmpty()) {
            loginBinding.logMail.setError(null);
            loginBinding.logPass.setError("Enter Password");
            loginBinding.logPass.requestFocus();
        } else {
            login();
        }
    }

    private void login() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                String val = response.trim();
                if (!response.trim().equals("failed")) {
                    String data = response;
                    String[] arr = data.trim().split(":");

                    SharedPreferences.Editor editor = getSharedPreferences("SharedData", MODE_PRIVATE).edit();
                    editor.putString("logid", arr[0]);
                    editor.putString("type", arr[1]);
                    editor.putString("uname", arr[2]);
                    editor.putString("email", arr[3]);
                    editor.putString("phone", arr[4]);
                    editor.putString("address", arr[5]);
                    editor.apply();
                    System.out.println("##########" + arr[0]);
                    System.out.println("##########" + arr[1]);
                    System.out.println("##########" + arr[2]);
                    System.out.println("##########" + arr[3]);
                    System.out.println("##########" + arr[4]);
                    System.out.println("##########" + arr[5]);

                    if (EMAIL.equals("admin@gmail.com") && PASS.equals("admin")) {
                        Snackbar.make(loginBinding.getRoot(), "Login Successful", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        startActivity(new Intent(getApplicationContext(), AdminHome.class));
                    } else if (arr[1].equals("user")) {
                        Snackbar.make(loginBinding.getRoot(), "Login Successful", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        startActivity(new Intent(getApplicationContext(), UserHome.class));
                    }
                } else {
                    Snackbar.make(loginBinding.getRoot(), "Login failed..!", Snackbar.LENGTH_LONG)
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
                map.put("key", "login");
                map.put("uname", EMAIL);
                map.put("password", PASS);
                return map;
            }
        };
        queue.add(request);
    }
}
