package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    // Declare variables for views
    private EditText usernameEditText, passwordEditText;
    private Button loginButton, signUpButton;

    CheckBox showPass;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize OkHttpClient
        client = new OkHttpClient();

        // Find views by their IDs in the layout
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordTextView);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        showPass =findViewById(R.id.passCheckbox);

        // Set click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempUsername = usernameEditText.getText().toString();
                String tempPassword = passwordEditText.getText().toString();
                checkInputs(tempUsername, tempPassword);
            }
        });


        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });

        // Set click listener for the sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SignUpActivity when sign up button is clicked
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear the input fields when the activity is resumed
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    // Method to check if username and password inputs are valid
    private void checkInputs(String user, String pass) {
        if (TextUtils.isEmpty(user)) {
            // Display toast message if username is empty
            Toast.makeText(LoginActivity.this, "Please enter username ˙ᵕ˙", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            // Display toast message if password is empty
            Toast.makeText(LoginActivity.this, "Please enter password ٩꒰｡•◡•｡꒱۶", Toast.LENGTH_SHORT).show();
        } else {
            // Call the authentication method if inputs are valid
            authenticateLogin(user, pass);
        }
    }

    // Method to authenticate login using the provided username and password
    private void authenticateLogin(String username, String password) {
        String url = "https://lamp.ms.wits.ac.za/home/s2556863/finalLogin.php?USERNAME=" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException exception) {
                exception.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Display toast message if failed to connect to the server
                        Toast.makeText(LoginActivity.this, "Failed to connect to the server ˙◠˙", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseBody = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (response.isSuccessful()) {
                            if (responseBody.equals("\"" + password + "\"")) {
                                // Login successful, navigate to ItemsDisplayActivity
                                Intent intentItemsDisplayActvity = new Intent(LoginActivity.this, ItemsDisplayActvity.class);
                                intentItemsDisplayActvity.putExtra("username", username);
                                startActivity(intentItemsDisplayActvity);
                            } else {
                                // Display toast message if login fails
                                Toast.makeText(LoginActivity.this, "Username or password incorrect :(", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Display toast message if connection fails
                            Toast.makeText(LoginActivity.this, "Connection failed :O", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
