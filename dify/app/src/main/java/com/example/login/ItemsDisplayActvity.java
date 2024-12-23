package com.example.login;

import static java.lang.System.out;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemsDisplayActvity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    LinearLayout itemList;
    Spinner spinner;
    EditText search;
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_display);

        TextView textview= (TextView) findViewById(R.id.sell);
        search=(EditText) findViewById(R.id.search);
        Button back =(Button) findViewById(R.id.backProductButton);
        Button sell =(Button) findViewById(R.id.button);
        itemList=(LinearLayout) findViewById(R.id.layout);
        spinner = findViewById(R.id.spinner);

        new ListProductTask().execute();


        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    new SearchResultTask().execute(search.getText().toString(),"empty");
                    return true; // Return true to consume the event
                }
                return false; // Return false if you want to allow the default handling as well
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(ItemsDisplayActvity.this, LoginActivity.class);
                startActivity(i);

            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j= new Intent(ItemsDisplayActvity.this, AddProductActivity.class);
                Intent k =getIntent();
                String username = k.getStringExtra("username");
                j.putExtra("username", username);
                startActivity(j);
            }
        });
    }

    public void processJSON(String json) {
        try {
            itemList.removeAllViews();
            JSONArray output = new JSONArray(json);
            for (int i = 0; i < output.length(); i++) {
                JSONObject out = output.getJSONObject(i);
                ProductListLayout pl = new ProductListLayout(this);
                pl.populate(out);
                GradientDrawable border = new GradientDrawable();
                border.setStroke(2, Color.BLACK);

                if (i%2==0){
                    pl.setBackgroundColor(Color.parseColor("#FFFFFF"));
                } else {
                    pl.setBackgroundColor(Color.parseColor("#CE93D8"));
                }
                pl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDisplay = new Intent(ItemsDisplayActvity.this, ProductDisplayActivity.class);
                        String itemID = null;
                        try {
                            itemID = out.getString("ITEM_ID");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Intent k =getIntent();
                        String username = k.getStringExtra("username");
                        productDisplay.putExtra("itemID",itemID);
                        productDisplay.putExtra("username",username);
                        startActivity(productDisplay);
                    }
                });
                //pl.setBackground(border);
                itemList.addView(pl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        new ListProductTask().execute();
    }

    private class ListProductTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String serverResponse = null;


            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2556863/php_display.php")
                    .build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();

                if (response.isSuccessful()) {
                    serverResponse = response.body().string();
                } else {
                    out.println("Server responded with error: " + response.code() + " - " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return serverResponse;
        }


        @Override
        protected void onPostExecute(String serverResponse) {
            // Update the UI with the server response
            if (serverResponse != null && !serverResponse.isEmpty()) {
                processJSON(serverResponse);
            } else {
                out.println("Server response is null or empty.");
            }
        }
    }

    private class SearchResultTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String param = params[0];
            String order = params[1];

            String serverResponse = null;

            RequestBody requestBody = new FormBody.Builder()
                    .add("param", param)
                    .add("order",order)
                    .build();

            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2556863/php_search.php")
                    .post(requestBody)
                    .build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();

                if (response.isSuccessful()) {
                    serverResponse = response.body().string();
                } else {
                    out.println("Server responded with error: " + response.code() + " - " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return serverResponse;
        }


        @Override
        protected void onPostExecute(String serverResponse) {
            // Update the UI with the server response
            if (serverResponse != null && !serverResponse.isEmpty()) {
                processJSON(serverResponse);
            } else {
                out.println("Server response is null or empty.");
            }
        }
    }

    public class ProductListLayout extends LinearLayout{
        TextView productName;
        TextView productPrice;
        public ProductListLayout(Context context) {
            super(context);
            setOrientation(LinearLayout.HORIZONTAL);

            productName = new TextView(context);
            productPrice = new TextView(context);

            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
            lp1.weight = 1;
            productName.setTextSize(30);
            productName.setPadding(0, 0, 20, 0);
            productName.setMaxLines(1);
            productName.setEllipsize(TextUtils.TruncateAt.END);
            productName.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productName.setTextColor(Color.parseColor("#000000"));
            addView(productName, lp1);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp2.weight = 0;
            productPrice.setTextSize(30);
            productPrice.setPadding(0, 0, 50, 0);
            productPrice.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productPrice.setTextColor(Color.parseColor("#000000"));
            productPrice.setGravity(Gravity.END);
            addView(productPrice, lp2);
        }

        public void populate(JSONObject out) throws JSONException {
            productName.setText(out.getString("ITEM_NAME"));
            productPrice.setText("R "+out.getString("ITEM_PRICE"));

        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selectedOrder = spinner.getSelectedItem().toString();
        String orderBy = "";

        if (selectedOrder.equals("Oldest to newest")) {
            orderBy = "ITEM_DATE_POSTED ASC";
        } else if (selectedOrder.equals("Newest to oldest")) {
            orderBy = "ITEM_DATE_POSTED DESC";
        } else if (selectedOrder.equals("Price lowest to highest")) {
            orderBy = "ITEM_PRICE ASC";
        } else if (selectedOrder.equals("Price highest to lowest")) {
            orderBy = "ITEM_PRICE DESC";
        } else {
        }

        String param;
        if (search.getText().toString().isEmpty()==true){
            param="empty";
        } else {
            param=search.getText().toString();
        }
        new SearchResultTask().execute(param, orderBy);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}