
package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddProductActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    Button addProduct;
    EditText description;
    EditText price;
    EditText quantity;
    EditText product;
    Button back;
    Intent j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        product = findViewById(R.id.productLayout);
        quantity = findViewById(R.id.quantity);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        addProduct = findViewById(R.id.add_button);
        back= findViewById(R.id.Back_Itemlist);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(AddProductActivity.this, ItemsDisplayActvity.class);
                j = getIntent();
                String send = j.getStringExtra("username");
                i.putExtra("username",send);
                startActivity(i);

            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String productName = product.getText().toString();
                String productQuantity = quantity.getText().toString();
                String productPrice = price.getText().toString();
                String productDescription = description.getText().toString();

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String currentDate = year + "-" + month + "-" + day;
                j  =getIntent();
                String username = j.getStringExtra("username");

                if (checkInputs(productName, productQuantity, productPrice, productDescription)) {
                    int parsedQuantity = Integer.parseInt(productQuantity); // Parse the quantity if it's not empty
                    float parsedPrice = Float.parseFloat(productPrice); // Parse the price
                    new AddProductTask().execute(productName, productQuantity, productPrice, productDescription, currentDate, username);
                } else {
                    if (productDescription.length() > 50) {
                        Toast.makeText(AddProductActivity.this, "Your description is too long ᯣ.ᯣ", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(AddProductActivity.this, "Please fill in all fields ᯣ.ᯣ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        product.setText("");
        quantity.setText("");
        price.setText("");
        description.setText("");
    }

    public boolean checkInputs(String productName, String productQuantity, String productPrice, String productDescription) {
        if (productName.isEmpty() || productName.length() > 25 || productQuantity.isEmpty() || productQuantity.equals("0") || productPrice.isEmpty() || productPrice.equals("0") || productDescription.isEmpty() || productDescription.length() > 50) {
            return false;
        } else {
            return true;
        }
    }


    private class AddProductTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String productName = params[0];
            String productQuantity = params[1];
            String productPrice = params[2];
            String productDescription = params[3];
            String currentDate =params[4];
            String username = params[5];

            String serverResponse = null;

            RequestBody requestBody = new FormBody.Builder()
                    .add("name", productName)
                    .add("qty", productQuantity)
                    .add("price", productPrice)
                    .add("date", currentDate)
                    .add("user", username)
                    .add("desc", productDescription)
                    .build();

            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2556863/php_upload.php")
                    .post(requestBody)
                    .build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();

                if (response.isSuccessful()) {
                    serverResponse = response.body().string();
                } else {
                    System.out.println("Server responded with error: " + response.code() + " - " + response.message());
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
                Toast.makeText(AddProductActivity.this, "New product added succesfully ฅ^•ﻌ•^ฅ", Toast.LENGTH_SHORT).show();
                product.setText("");
                description.setText("");
                price.setText("");
                quantity.setText("");
            } else {
                System.out.println("Server response is null or empty.");
            }
        }
    }
}




