package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RatingsActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    ScrollView scroll;
    LinearLayout layout;
    EditText ratingEdt, reviewEdt;
    String tempUsername, tempRating, tempReview;
    Button add, reload, back;
    String username;
    String ServerURL = "https://lamp.ms.wits.ac.za/home/s2556863/addRating.php";

    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        Intent temp = getIntent();
        username = temp.getStringExtra("username");
        itemId = temp.getStringExtra("itemID");

        System.out.println(username);
        System.out.println(itemId);

        ratingEdt = findViewById(R.id.edtRating);
        reviewEdt = findViewById(R.id.edtReview);
        add = findViewById(R.id.Add_Review);
        scroll = findViewById(R.id.scroll);
        layout = findViewById(R.id.layout);
        back = findViewById(R.id.backProductButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RatingsActivity.this, ProductDisplayActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("itemID",itemId);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetData();
                checkInputs(tempRating, tempReview);
            }
        });

        new LoadDataAsyncTask().execute(itemId);
    }

    private class LoadDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String ItemID=params[0];

            String url = "https://lamp.ms.wits.ac.za/home/s2556863/rating.php?ITEM_ID=" + ItemID;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String responseStr) {
            if (responseStr != null) {
                processJSON(responseStr);
            } else {
                // Handle the case where the request was not successful
            }
        }
    }


    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        ratingEdt.setText("");
        reviewEdt.setText("");
        Intent itemDisplay=getIntent();
        String itemID = itemDisplay.getStringExtra("itemID");
        new LoadDataAsyncTask().execute(itemID);
    }
    public void processJSON(String json) {
        try {
            layout.removeAllViews();
            JSONArray output = new JSONArray(json);
            for (int i = 0; i < output.length(); i++) {
                JSONObject out = output.getJSONObject(i);
                RatingListLayout rl = new RatingListLayout(this);
                rl.populate(out);
                layout.addView(rl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Call post(String url, String json, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public class RatingListLayout extends LinearLayout {
        TextView username, rating, review;

        public RatingListLayout(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = getResources().getDimensionPixelSize(R.dimen.rating_list_layout_margin);
            layoutParams.setMargins(margin, margin / 4, margin, margin / 4);
            setLayoutParams(layoutParams);

            GradientDrawable borderDrawable = new GradientDrawable();
            borderDrawable.setColor(ContextCompat.getColor(context, R.color.border_color));
            borderDrawable.setStroke(1, ContextCompat.getColor(context, R.color.border_color));
            setBackground(borderDrawable);

            LinearLayout innerLayout = new LinearLayout(context);
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.setMargins(margin, margin / 4, margin, margin / 4);
            innerLayout.setLayoutParams(lp1);

            username = new TextView(context);
            rating = new TextView(context);

            username.setTextSize(25);
            username.setMaxLines(1);
            username.setEllipsize(TextUtils.TruncateAt.END);
            username.setTextColor(Color.parseColor("#000000"));
            innerLayout.addView(username);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.setMargins(margin, 0, margin, 0);
            rating.setTextSize(25);
            rating.setGravity(Gravity.END);
            innerLayout.addView(rating, lp2);

            addView(innerLayout);

            review = new TextView(context);
            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp3.setMargins(margin, 0, margin, 0);
            review.setTextSize(25);
            addView(review, lp3);
        }

        public void populate(JSONObject data) {
            try {
                String usernameText = data.getString("USERNAME");
                String ratingText = data.getString("RATING");
                String reviewText = data.getString("REVIEW");

                username.setText(usernameText);
                username.setTextColor(Color.BLACK);
                username.setTypeface(null, Typeface.BOLD);
                rating.setText("["+ratingText + "/5]");
                rating.setTextColor(Color.BLACK);
                rating.setTypeface(null, Typeface.BOLD);
                review.setText(reviewText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void GetData() {
        tempUsername = username;
        tempRating = ratingEdt.getText().toString();
        tempReview = reviewEdt.getText().toString();
    }
    private void checkInputs(String user, String pass) {
        if (TextUtils.isEmpty(user)) {
            // Display toast message if username is empty
            Toast.makeText(RatingsActivity.this, "You haven't rated the product ¯\\_(•᷄\u200E n •́)_/¯?", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            // Display toast message if password is empty
            Toast.makeText(RatingsActivity.this, "You haven't left a review ¯\\_(•᷄\u200E n •́)_/¯?", Toast.LENGTH_SHORT).show();
        } else {
            // Call the authentication method if inputs are valid
            InsertData(tempUsername, tempRating, tempReview);
        }
    }

    private void InsertData(String username, String rating, String review) {
        if (username == null || rating == null || review == null) {
            return;
        }
        System.out.println(username);
        System.out.println(itemId);
        System.out.println(rating);
        System.out.println(review);

        RequestBody formBody = new FormBody.Builder()
                .add("USERNAME", username)
                .add("RATING", rating)
                .add("REVIEW", review)
                .add("ITEM_ID", itemId)
                .build();

        Request request = new Request.Builder()
                .url(ServerURL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RatingsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RatingsActivity.this, "Review added successfully (｡◕‿‿◕｡)", Toast.LENGTH_SHORT).show();
                        ratingEdt.setText("");
                        reviewEdt.setText("");
                        new LoadDataAsyncTask().execute(itemId);
                    }
                });
            }
        });
    }

}
