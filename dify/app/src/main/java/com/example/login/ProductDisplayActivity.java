package com.example.login;

import static java.lang.System.out;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class ProductDisplayActivity extends AppCompatActivity {
    LinearLayout productLayout;
    LinearLayout ratingsLayout;

    Intent itemDisplay;

    String user;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_display);

        productLayout=(LinearLayout) findViewById(R.id.productLayout);
        ratingsLayout=(LinearLayout) findViewById(R.id.ratingsLayout);
        Button back = (Button) findViewById(R.id.backItemsButton);

        itemDisplay=getIntent();
        String itemID = itemDisplay.getStringExtra("itemID");

        new ProductDisplayTask().execute(itemID);
        new RatingDisplayTask().execute(itemID);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(ProductDisplayActivity.this, ItemsDisplayActvity.class);
                i.putExtra("username",user);
                startActivity(i);

            }
        });
    }
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        itemDisplay=getIntent();
        String itemID = itemDisplay.getStringExtra("itemID");
        user = itemDisplay.getStringExtra("username");

        new ProductDisplayTask().execute(itemID);
        new RatingDisplayTask().execute(itemID);
    }
    private class RatingDisplayTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String itemID = params[0];

            String serverResponse = null;

            RequestBody requestBody = new FormBody.Builder()
                    .add("ITEM_ID", itemID)
                    .build();

            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2556863/rating.php")
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
                processRatingJSON(serverResponse);
            } else {
                out.println("Server response is null or empty.");
            }
        }
    }
    public void processRatingJSON(String json) {
        try {
            ratingsLayout.removeAllViews();

            TextView ratingsLabel = new TextView(this);
            ratingsLabel.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            ratingsLabel.setTextColor(Color.parseColor("#000000"));
            ratingsLabel.setText("Ratings");
            ratingsLabel.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ratingsLabel.setTextSize(40);
            GradientDrawable borderWhite = new GradientDrawable();
            borderWhite.setStroke(2, Color.BLACK);
            borderWhite.setColor(Color.parseColor("#FFFFFF"));
            ratingsLabel.setBackground(borderWhite);
            ratingsLayout.addView(ratingsLabel);

            JSONArray output = new JSONArray(json);
            int numRatings = output.length();
            int ratingsAdded = 0;

            if (numRatings == 0) {
                TextView empty = new TextView(this);
                empty.setText("There are no reviews for this product");
                empty.setPadding(10, 20, 10, 20);
                empty.setTextSize(25);
                empty.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
                empty.setTextColor(Color.parseColor("#000000"));
                empty.setBackground(borderWhite);
                ratingsLayout.addView(empty);

                TextView add = new TextView(this);
                add.setText("Add rating");
                add.setPadding(20, 20, 10, 20);
                add.setTextSize(20);
                add.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
                add.setPaintFlags(add.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                add.setGravity(Gravity.CENTER_HORIZONTAL);
                add.setTextColor(Color.parseColor("#000000"));
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ratingsDisplay = new Intent(ProductDisplayActivity.this, RatingsActivity.class);
                        Intent k = getIntent();
                        String username = k.getStringExtra("username");
                        String itemID = k.getStringExtra("itemID");
                        ratingsDisplay.putExtra("username", username);
                        ratingsDisplay.putExtra("itemID", itemID);
                        startActivity(ratingsDisplay);
                    }
                });
                ratingsLayout.addView(add);
            } else if (numRatings == 1) {
                JSONObject out = output.getJSONObject(0);
                ProductRatingLayout pr = new ProductRatingLayout(this);
                pr.populate(out);
                ratingsLayout.addView(pr);

                TextView add = new TextView(this);
                add.setText("Add rating");
                add.setPadding(20, 20, 10, 20);
                add.setTextSize(20);
                add.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
                add.setPaintFlags(add.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                add.setGravity(Gravity.CENTER_HORIZONTAL);
                add.setTextColor(Color.parseColor("#000000"));
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ratingsDisplay = new Intent(ProductDisplayActivity.this, RatingsActivity.class);
                        Intent k = getIntent();
                        String username = k.getStringExtra("username");
                        String itemID = k.getStringExtra("itemID");
                        ratingsDisplay.putExtra("username", username);
                        ratingsDisplay.putExtra("itemID", itemID);
                        startActivity(ratingsDisplay);
                    }
                });
                ratingsLayout.addView(add);
            } else if (numRatings == 2) {
                for (int i = 0; i < numRatings; i++) {
                    JSONObject out = output.getJSONObject(i);
                    ProductRatingLayout pr = new ProductRatingLayout(this);
                    pr.populate(out);
                    ratingsLayout.addView(pr);
                    ratingsAdded++;
                }

                TextView add = new TextView(this);
                add.setText("Add rating");
                add.setPadding(20, 20, 10, 20);
                add.setTextSize(20);
                add.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
                add.setPaintFlags(add.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                add.setGravity(Gravity.CENTER_HORIZONTAL);
                add.setTextColor(Color.parseColor("#000000"));
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ratingsDisplay = new Intent(ProductDisplayActivity.this, RatingsActivity.class);
                        Intent k = getIntent();
                        String username = k.getStringExtra("username");
                        String itemID = k.getStringExtra("itemID");
                        ratingsDisplay.putExtra("username", username);
                        ratingsDisplay.putExtra("itemID", itemID);
                        startActivity(ratingsDisplay);
                    }
                });
                ratingsLayout.addView(add);
            } else if (numRatings == 3) {
                for (int i = 0; i < numRatings; i++) {
                    JSONObject out = output.getJSONObject(i);
                    ProductRatingLayout pr = new ProductRatingLayout(this);
                    pr.populate(out);
                    ratingsLayout.addView(pr);
                    ratingsAdded++;
                }

                TextView add = new TextView(this);
                add.setText("Add rating");
                add.setPadding(20, 20, 10, 20);
                add.setTextSize(20);
                add.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
                add.setPaintFlags(add.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                add.setGravity(Gravity.CENTER_HORIZONTAL);
                add.setTextColor(Color.parseColor("#000000"));
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ratingsDisplay = new Intent(ProductDisplayActivity.this, RatingsActivity.class);
                        Intent k = getIntent();
                        String username = k.getStringExtra("username");
                        String itemID = k.getStringExtra("itemID");
                        ratingsDisplay.putExtra("username", username);
                        ratingsDisplay.putExtra("itemID", itemID);
                        startActivity(ratingsDisplay);
                    }
                });
                ratingsLayout.addView(add);
            } else if (numRatings > 3) {
                for (int i = 0; i < 3; i++) {
                    JSONObject out = output.getJSONObject(i);
                    ProductRatingLayout pr = new ProductRatingLayout(this);
                    pr.populate(out);
                    ratingsLayout.addView(pr);
                    ratingsAdded++;
                }

                TextView more = new TextView(this);
                more.setText("More ratings...");
                more.setPadding(20, 20, 10, 20);
                more.setTextSize(20);
                more.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
                more.setPaintFlags(more.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                more.setGravity(Gravity.CENTER_HORIZONTAL);
                more.setTextColor(Color.parseColor("#000000"));
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ratingsDisplay = new Intent(ProductDisplayActivity.this, RatingsActivity.class);
                        Intent k = getIntent();
                        String username = k.getStringExtra("username");
                        String itemID = k.getStringExtra("itemID");
                        ratingsDisplay.putExtra("username", username);
                        ratingsDisplay.putExtra("itemID", itemID);
                        startActivity(ratingsDisplay);
                    }
                });
                ratingsLayout.addView(more);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class ProductRatingLayout extends LinearLayout {
        TextView username, rating, review;

        public ProductRatingLayout(Context context) {
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
            username.setTextColor(Color.BLACK);
            username.setTypeface(username.getTypeface(), Typeface.BOLD);
            innerLayout.addView(username);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.setMargins(margin, 0, margin, 0);
            rating.setTextSize(25);
            rating.setGravity(Gravity.END);
            rating.setTextColor(Color.BLACK);
            rating.setTypeface(rating.getTypeface(), Typeface.BOLD);
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
                rating.setText("["+ratingText + "/5]");
                review.setText(reviewText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class ProductDisplayTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String itemID = params[0];

            String serverResponse = null;

            RequestBody requestBody = new FormBody.Builder()
                    .add("item_id", itemID)
                    .build();

            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2556863/php_product.php")
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
                processProductJSON(serverResponse);
            } else {
                out.println("Server response is null or empty.");
            }
        }
    }

    public void processProductJSON(String json) {
        try {
            productLayout.removeAllViews();
            JSONArray output = new JSONArray(json);
            for (int i = 0; i < output.length(); i++) {
                JSONObject out = output.getJSONObject(i);
                ProductDisplayLayout pd = new ProductDisplayLayout(this);
                pd.populate(out);
                productLayout.addView(pd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ProductDisplayLayout extends LinearLayout {
        TextView productName;
        TextView productPrice;
        TextView productDesc;
        TextView productUser;
        TextView productQty;
        TextView productDate;
        TextView productPhone;

        TextView productAvgRating;
        public ProductDisplayLayout(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            productName = new TextView(context);

            productName = new TextView(context);
            productPrice = new TextView(context);
            productDesc =new TextView(context);
            productUser = new TextView(context);
            productQty = new TextView(context);
            productDate = new TextView(context);
            productPhone = new TextView(context);
            productAvgRating= new TextView(context);

            productName.setPadding(10, 20, 10, 20);
            productName.setTextSize(30);
            productName.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productName.setTextColor(Color.parseColor("#000000"));
            GradientDrawable borderPurple = new GradientDrawable();
            borderPurple.setStroke(2, Color.BLACK);
            borderPurple.setColor(Color.parseColor("#CE93D8"));
            productName.setBackground(borderPurple);

            LinearLayout infoLayout = new LinearLayout(context);
            infoLayout.setOrientation(LinearLayout.HORIZONTAL);
            infoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            productPrice.setTextSize(20);
            productPrice.setPadding(10, 10, 10, 10);
            productPrice.setTextSize(15);
            productPrice.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productPrice.setTextColor(Color.parseColor("#000000"));

            productQty.setText("Quantity: ");
            productQty.setPadding(10, 10, 10, 10);
            productQty.setTextSize(15);
            productQty.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productQty.setTextColor(Color.parseColor("#000000"));

            productAvgRating.setText("Average Rating: [");
            productAvgRating.setPadding(10, 10, 20, 10);
            productAvgRating.setTextSize(15);
            productAvgRating.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productAvgRating.setTextColor(Color.parseColor("#000000"));

            GradientDrawable borderWhite = new GradientDrawable();
            borderWhite.setStroke(2, Color.BLACK);
            borderWhite.setColor(Color.parseColor("#FFFFFF"));

            infoLayout.addView(productPrice);
            infoLayout.addView(productQty);
            infoLayout.addView(productAvgRating);
            infoLayout.setBackground(borderWhite);

            TextView descLabel = new TextView(context);
            descLabel.setPaintFlags(descLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            descLabel.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            descLabel.setTextColor(Color.parseColor("#000000"));
            descLabel.setText("Description:");
            descLabel.setBackgroundColor(Color.parseColor("#CE93D8"));
            descLabel.setTextSize(25);

            LinearLayout descLayout = new LinearLayout(context);
            descLayout.setOrientation(LinearLayout.VERTICAL);

            productDesc.setTextSize(30);
            productDesc.setPadding(10, 10, 10, 10);
            productDesc.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productDesc.setTextColor(Color.parseColor("#000000"));
            productDesc.setText("");

            descLayout.addView(descLabel);
            descLayout.addView(productDesc);
            descLayout.setBackground(borderPurple);


            TextView contactSellerLabel = new TextView(context);
            contactSellerLabel.setPaintFlags(contactSellerLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            contactSellerLabel.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            contactSellerLabel.setTextColor(Color.parseColor("#000000"));
            contactSellerLabel.setText("To contact seller:");
            contactSellerLabel.setBackgroundColor(Color.parseColor("#FFFFFF"));
            contactSellerLabel.setTextSize(25);

            LinearLayout contactLayout = new LinearLayout(context);
            contactLayout.setOrientation(LinearLayout.VERTICAL);

            productUser.setTextSize(25);
            productUser.setPadding(10, 10, 10, 10);
            productUser.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productUser.setTextColor(Color.parseColor("#000000"));
            productUser.setText("Username: ");

            productPhone.setTextSize(25);
            productPhone.setPadding(10, 10, 10, 10);
            productPhone.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productPhone.setTextColor(Color.parseColor("#000000"));
            productPhone.setText("Cell: ");

            contactLayout.addView(contactSellerLabel);
            contactLayout.addView(productUser);
            contactLayout.addView(productPhone);
            contactLayout.setBackground(borderWhite);

            productDate.setPadding(10, 10, 10, 10);
            productDate.setBackgroundColor(Color.parseColor("#FFFFFF"));
            productDate.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productDate.setTextSize(20);
            productDate.setTextColor(Color.parseColor("#000000"));
            productDate.setText("Date posted: ");
            productDate.setBackground(borderPurple);

            TextView productLabel = new TextView(context);
            productLabel.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            productLabel.setTextColor(Color.parseColor("#000000"));
            productLabel.setText("Product Details");
            productLabel.setBackgroundColor(Color.parseColor("#FFFFFF"));
            productLabel.setTextSize(40);
            productLabel.setBackground(borderWhite);

            addView(productLabel);
            addView(productName, layoutParams);
            addView(infoLayout);
            addView(descLayout);
            addView(contactLayout);
            addView(productDate, layoutParams);
        }


        public void populate(JSONObject out) throws JSONException {
            productName.append(out.getString("ITEM_NAME"));
            productPrice.append("R " + out.getString("ITEM_PRICE"));
            productDesc.append(out.getString("ITEM_DESC"));
            productUser.append(out.getString("USERNAME"));
            productQty.append(out.getString("ITEM_QTY"));
            productDate.append(out.getString("ITEM_DATE_POSTED"));
            productPhone.append(out.getString("USER_PHONE"));
            if (out.getString("AVG_RATING") != "null") {
                productAvgRating.append(out.getString("AVG_RATING") + "/5]");
            } else {
                    productAvgRating.setText("No rating");
            }
        }
    }



}