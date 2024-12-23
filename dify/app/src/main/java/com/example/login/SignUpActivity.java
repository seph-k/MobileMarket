package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    String ServerURL = "https://lamp.ms.wits.ac.za/home/s2556863/get.php";
    EditText first, last, email, phone, username, password;
    Button add, back;

    Spinner spinnerYear, spinnerMonth, spinnerDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        first = findViewById(R.id.new_fname);
        last = findViewById(R.id.new_lname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        username = findViewById(R.id.new_username);
        password = findViewById(R.id.new_password);
        add = findViewById(R.id.Add);
        back = findViewById(R.id.Back_Main);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = first.getText().toString();
                String lastName = last.getText().toString();
                String userEmail = email.getText().toString();
                String userPhone = phone.getText().toString();
                String userUsername = username.getText().toString();
                String userPassword = password.getText().toString();

                String selectedYear = spinnerYear.getSelectedItem().toString();
                String selectedMonth = spinnerMonth.getSelectedItem().toString();
                String selectedDay = spinnerDay.getSelectedItem().toString();

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(SignUpActivity.this, "Please enter first name ٩(ˊᗜˋ*)و", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(SignUpActivity.this, "Please enter last name ˊᗜˋ", Toast.LENGTH_SHORT).show();
                } else if (selectedYear.equals("YYYY") || selectedMonth.equals("MM") || selectedDay.equals("DD")) {
                    Toast.makeText(SignUpActivity.this, "Please select a valid date ୨୧° ♡ °୨୧", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(SignUpActivity.this, "Please enter email ૮₍ ˃ ᵕ ˂ ₎ა", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userPhone)) {
                    Toast.makeText(SignUpActivity.this, "Please enter phone number ✧(˵ •̀ ᴗ - ˵ ) ✧", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userUsername)) {
                    Toast.makeText(SignUpActivity.this, "Please enter username ᵔ ᵕ ᵔ", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please enter password ᵕ̈ ", Toast.LENGTH_SHORT).show();
                } else {
                    // Call the method to insert data
                    insertData(firstName, lastName, userEmail, userPhone, userUsername, userPassword);
                }
            }
        });

        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerDay = findViewById(R.id.spinnerDay);

        // Set up spinners
        setupYearSpinner();
        setupMonthSpinner();
        setupDaySpinner();

    }

    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        first.setText("");
        last.setText("");
        email.setText("");
        phone.setText("");
        username.setText("");
        password.setText("");


    }

    private void setupYearSpinner() {
        List<String> years = new ArrayList<>();
        // Add years to the list (e.g., 1903 to 2007)
        years.add("YYYY");
        for (int year = 1903; year <= 2007; year++) {
            years.add(String.valueOf(year));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
    }

    private void setupMonthSpinner() {
        // Modify this part according to your month values requirement
        String[] months = {"MM","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
    }

    private void setupDaySpinner() {
        // Modify this part according to your day values requirement
        String[] days = {"DD","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
                "29", "30", "31"};

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);
    }

    private void insertData(final String firstName, final String lastName, final String email, final String phone,
                            final String username, final String password) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("USER_FNAME", firstName)
                        .add("USER_LNAME", lastName)
                        .add("USER_DOB", getSelectedDate())
                        .add("USER_EMAIL", email)
                        .add("USER_PHONE", phone)
                        .add("USERNAME", username)
                        .add("USER_PASS", password)
                        .build();

                Request request = new Request.Builder()
                        .url(ServerURL)
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        return "Data Inserted Successfully";
                    } else {
                        return "Username may be already in use";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "Data Insertion Failed";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Toast.makeText(SignUpActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(firstName, lastName, email, phone, username, password);
    }

    private String getSelectedDate() {
        String selectedYear = spinnerYear.getSelectedItem().toString();
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        String selectedDay = spinnerDay.getSelectedItem().toString();
        return selectedYear + "-" + selectedMonth + "-" + selectedDay;
    }
}