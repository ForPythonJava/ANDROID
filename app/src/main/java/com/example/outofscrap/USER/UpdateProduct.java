package com.example.outofscrap.USER;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.outofscrap.COMMON.Base64;
import com.example.outofscrap.COMMON.RequestPojo;
import com.example.outofscrap.COMMON.Utility;
import com.example.outofscrap.databinding.FragmentUpdateProductBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateProduct extends Fragment {
    FragmentUpdateProductBinding binding;
    RequestPojo requestPojo;
    String bal = null, PID;

    String ITEMNAME, ITEMDESC, ITEMRATE, WEIGHT;
    Uri imageUri;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_Camera_IMAGE = 2;
    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateProductBinding.inflate(getLayoutInflater());

        final Bundle b = this.getArguments();
        requestPojo = b.getParcelable("clicked_item");

        binding.itemName.getEditText().setText(requestPojo.getName());
        binding.itemDesc.getEditText().setText(requestPojo.getDesc());
        binding.itemRate.getEditText().setText(requestPojo.getRate());
        PID = requestPojo.getPid();
        bal = requestPojo.getImage();
        System.out.println(requestPojo.getType() + "@@@@@@@@@@@@@@@@@@@@");
        if (requestPojo.getType().equals("scrap")) {
            binding.approxWeight.setVisibility(View.VISIBLE);
            binding.approxWeight.getEditText().setText(requestPojo.getWeight());
        } else {
            binding.approxWeight.setVisibility(View.GONE);
        }

        if (requestPojo.getType().equals("creative")) {
            binding.itemName.getEditText().setText(requestPojo.getWeight());
        }

        binding.foodCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "new-photo-name.jpg";
                //create parameters for Intent with filename
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured by camera");
                //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
                imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                //create new Intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, PICK_Camera_IMAGE);
            }
        });


        binding.foodGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                try {
                    Intent gintent = new Intent();
                    gintent.setType("image/*");
                    gintent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gintent, "Select Picture"), PICK_IMAGE);

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }
            }
        });

        binding.addPrdctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ITEMNAME = binding.itemName.getEditText().getText().toString().trim();
                ITEMDESC = binding.itemDesc.getEditText().getText().toString().trim();
                ITEMRATE = binding.itemRate.getEditText().getText().toString().trim();
                WEIGHT = binding.approxWeight.getEditText().getText().toString().trim();

                if (ITEMNAME.isEmpty()) {
                    binding.itemName.requestFocus();
                    binding.itemName.setError("Enter Item Name");
                }  else if (ITEMDESC.isEmpty()) {
                    binding.itemName.setError(null);
                    binding.itemDesc.requestFocus();
                    binding.itemDesc.setError("Enter Description");
                } else if (ITEMRATE.isEmpty()) {
                    binding.itemDesc.setError(null);
                    binding.itemRate.requestFocus();
                    binding.itemRate.setError("Enter the Rate");
                } else if (bal == null) {
                    binding.itemRate.setError(null);
                    binding.imageView3.requestFocus();
                    Snackbar.make(getView(), "Please Select Item image", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    updateItem();
                }
            }
        });

        return binding.getRoot();
    }

    private void updateItem() {

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utility.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", response);
                if (!response.trim().equals("failed")) {

//                    SearchFragment fragment = new SearchFragment();
//                    FragmentManager manager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.nav_host_fragment_content_user_home, fragment);
//                    transaction.commit();
//                    manager.popBackStack();
                    Intent intent = new Intent(getActivity(), UserHome.class);
                    startActivity(intent);
                    Snackbar.make(getView(), "Item Added Successfully", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Toast.makeText(getContext(), "Item Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error in updating Food", Toast.LENGTH_LONG).show();
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
                if (requestPojo.getType().equals("creative")) {
                    map.put("key", "UpdateItems");
                    map.put("i_name", ITEMNAME);
                    map.put("i_desc", ITEMDESC);
                    map.put("i_price", ITEMRATE);
                    map.put("pid", requestPojo.getPid());
                    map.put("uid", uid);
                    map.put("base_string", bal);
                } else {
                    map.put("key", "UpdateScrap");
                    map.put("i_name", ITEMNAME);
                    map.put("i_desc", ITEMDESC);
                    map.put("i_price", ITEMRATE);
                    map.put("i_weight", WEIGHT);
                    map.put("pid", requestPojo.getPid());
                    map.put("uid", uid);
                    map.put("base_string", bal);
                }
                return map;
            }
        };
        queue.add(request);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Uri selectedImageUri = null;
        String filePath = null;
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    selectedImageUri = data.getData();
                }
                break;
            case PICK_Camera_IMAGE:
                if (resultCode == RESULT_OK) {
                    //use imageUri here to access the image
                    selectedImageUri = imageUri;
		 		    	/*Bitmap mPic = (Bitmap) data.getExtras().get("data");
						selectedImageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), mPic, getResources().getString(R.string.app_name), Long.toString(System.currentTimeMillis())));*/
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getContext(), "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (selectedImageUri != null) {
            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    Toast.makeText(getContext(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                if (filePath != null) {
                    decodeFile(filePath);
                } else {
                    bitmap = null;
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Internal error",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }


    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        binding.imageView3.setImageBitmap(bitmap);

        //...
        Base64.InputStream is;
        BitmapFactory.Options bfo;
        Bitmap bitmapOrg;
        ByteArrayOutputStream bao;

        bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        //bitmapOrg = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + customImage, bfo);

        bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);
        byte[] ba = bao.toByteArray();
        bal = Base64.encodeBytes(ba);
//        Toast.makeText(getContext(), bal, Toast.LENGTH_SHORT).show();


        //..

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

}