package com.example.eulapticketscanner_dnd;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;





public class LoginActivity extends AppCompatActivity {

    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_first_step);

        // Remove the data type from the local variable declaration
        submitButton = findViewById(R.id.submitButton);

        // Set onClickListener using lambda expression
        submitButton.setOnClickListener(v -> {
            try {
                EditText keyCode1 = findViewById(R.id.keyCode1);
                EditText keyCode2 = findViewById(R.id.keyCode2);
                EditText keyCode3 = findViewById(R.id.keyCode3);
                EditText keyCode4 = findViewById(R.id.keyCode4);

                String code1 = keyCode1.getText().toString().trim();
                String code2 = keyCode2.getText().toString().trim();
                String code3 = keyCode3.getText().toString().trim();
                String code4 = keyCode4.getText().toString().trim();

                if (code1.isEmpty() || code2.isEmpty() || code3.isEmpty() || code4.isEmpty()) {
                    onFailure("Invalid keycode");
                } else {
                    // Simulate user verification
                    boolean userVerified = verifyUser(code1, code2, code3, code4);

                    if (userVerified) {
                        onSuccess();
                    } else {
                        onFailure("User verification failed");
                    }
                }
            } catch (Exception e) {
                onFailure("An error occurred: " + e.getMessage());
            }
        });

        // Additional initialization or logic for the login activity can be added here
    }

    // Method to verify user
    private boolean verifyUser(String code1, String code2, String code3, String code4) {
        // Implement your user verification logic here
        // For demonstration purposes, return true if all codes are "1234"
        return code1.equals("1") && code2.equals("2") && code3.equals("3") && code4.equals("4");
    }

    // Define your onSuccess method
    private void onSuccess() {
        // Display success message using Toast
        Toast.makeText(getApplicationContext(), "User verified successfully", Toast.LENGTH_SHORT).show();
    }

    // Define your onFailure method with error message parameter
    private void onFailure(String errorMessage) {
        // Display error message using Toast
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}