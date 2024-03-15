//LoginActivity.java
package com.example.eulapticketscanner_dnd;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class    LoginActivity extends AppCompatActivity {

    Button submitButton;
    TextView textViewErrorMessage; // Declare textViewErrorMessage as a class member

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_first_step);

        // Remove the data type from the local variable declaration
        submitButton = findViewById(R.id.submitButton);
        textViewErrorMessage = new TextView(this); // Initialize textViewErrorMessage

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
                    isEmpty();
                } else {
                    // Simulate user verification
                    boolean userVerified = verifyUser(code1, code2, code3, code4);

                    if (userVerified) {
                        onSuccess();
                    } else {
                        onFailureWithDelay("Invalid key code. Please try again.");
                    }
                }
            } catch (Exception e) {
                onFailureWithDelay("An error occurred: " + e.getMessage());
            }
        });
    }

    // Method to verify user
    private boolean verifyUser(String code1, String code2, String code3, String code4) {
        // Add an array of valid codes
        String[] validCodes = {"5268", "3940", "7812", "0436", "6351", "9780", "5820", "1934", "8276", "3201"};

        // Concatenate the provided codes into a single string for comparison
        String concatenatedInput = code1 + code2 + code3 + code4;

        // Check if the concatenated input matches any of the valid codes
        for (String validCode : validCodes) {
            if (concatenatedInput.equals(validCode)) {
                return true;
            }
        }
        return false;
    }


    // Define your onSuccess method
    private void onSuccess() {
        // Change the layout to layoutVerified
        setContentView(R.layout.login_sucess);

        // Remove any previous error message views to avoid duplication
        RelativeLayout parentLayout = findViewById(R.id.layoutVerified); // Assuming parent layout id is "layoutVerified"
        if (textViewErrorMessage != null) {
            parentLayout.removeView(textViewErrorMessage); // Remove any existing error message
        }

        // Create a new TextView to display error message
        textViewErrorMessage = new TextView(this);
        textViewErrorMessage.setTextColor(Color.BLACK);
        textViewErrorMessage.setGravity(Gravity.CENTER);
        textViewErrorMessage.setPadding(10, 10, 10, 10);
        textViewErrorMessage.setBackgroundColor(Color.WHITE);
        textViewErrorMessage.setTextSize(20);

        // Add the error message TextView to the parent RelativeLayout
        parentLayout.addView(textViewErrorMessage);

    }


    // Define your isEmpty method to display error message when fields are empty
    @SuppressLint("SetTextI18n")
    private void isEmpty() {
        // Display error message using AlertDialog
        ImageView verificationImage = findViewById(R.id.verification);
        verificationImage.setImageResource(R.drawable.error);

        // Change the border color of the EditTexts to red
        EditText keyCode1 = findViewById(R.id.keyCode1);
        EditText keyCode2 = findViewById(R.id.keyCode2);
        EditText keyCode3 = findViewById(R.id.keyCode3);
        EditText keyCode4 = findViewById(R.id.keyCode4);

        keyCode1.setBackgroundResource(R.drawable.border_red);
        keyCode2.setBackgroundResource(R.drawable.border_red);
        keyCode3.setBackgroundResource(R.drawable.border_red);
        keyCode4.setBackgroundResource(R.drawable.border_red);

        // Remove any previous error message views to avoid duplication
        RelativeLayout parentLayout = findViewById(R.id.login); // Assuming parent layout id is "login"
        if (textViewErrorMessage != null) {
            parentLayout.removeView(textViewErrorMessage); // Remove any existing error message
        }

        // Create a TextView to hold the error message
        textViewErrorMessage = new TextView(this);

        // Set up layout parameters for the error message
        RelativeLayout.LayoutParams paramsErrorMessage = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        paramsErrorMessage.addRule(RelativeLayout.BELOW, R.id.keyCodesContainer); // Place error message below key codes
        paramsErrorMessage.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsErrorMessage.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);// Center horizontally
        paramsErrorMessage.setMargins(0, 20, 0, 380); // Adjust top margin as needed
        textViewErrorMessage.setLayoutParams(paramsErrorMessage);

        textViewErrorMessage.setText("Please Enter key code");
        textViewErrorMessage.setTextColor(Color.RED);
        textViewErrorMessage.setPadding(20, 20, 20, 20);

        // Add the error message TextView to the parent RelativeLayout
        parentLayout.addView(textViewErrorMessage);

        // Move the "Enter Key Code" text above the key codes
        TextView enterKeyText = findViewById(R.id.enterKeyText);
        RelativeLayout.LayoutParams paramsEnterKeyText = (RelativeLayout.LayoutParams) enterKeyText.getLayoutParams();
        paramsEnterKeyText.addRule(RelativeLayout.ABOVE, textViewErrorMessage.getId()); // Place "Enter Key Code" text above error message
        enterKeyText.setLayoutParams(paramsEnterKeyText);
    }


    // Define your onFailure method with error message parameter and delay
    private void onFailureWithDelay(String errorMessage) {
        // Change the image to errorLogo
        ImageView verificationImage = findViewById(R.id.verification);
        verificationImage.setImageResource(R.drawable.error);

        // Change the border color of the EditTexts to red
        EditText keyCode1 = findViewById(R.id.keyCode1);
        EditText keyCode2 = findViewById(R.id.keyCode2);
        EditText keyCode3 = findViewById(R.id.keyCode3);
        EditText keyCode4 = findViewById(R.id.keyCode4);

        keyCode1.setBackgroundResource(R.drawable.border_red);
        keyCode2.setBackgroundResource(R.drawable.border_red);
        keyCode3.setBackgroundResource(R.drawable.border_red);
        keyCode4.setBackgroundResource(R.drawable.border_red);

        // Remove any previous error message views to avoid duplication
        RelativeLayout parentLayout = findViewById(R.id.login); // Assuming parent layout id is "login"
        if (textViewErrorMessage != null) {
            parentLayout.removeView(textViewErrorMessage); // Remove any existing error message
        }

        // Create a TextView to hold the error message
        textViewErrorMessage = new TextView(this);

        // Set up layout parameters for the error message
        RelativeLayout.LayoutParams paramsErrorMessage = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        paramsErrorMessage.addRule(RelativeLayout.BELOW, R.id.keyCodesContainer); // Place error message below key codes
        paramsErrorMessage.addRule(RelativeLayout.CENTER_HORIZONTAL); // Center horizontally
        paramsErrorMessage.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // Align at the bottom
        paramsErrorMessage.setMargins(0, 0, 0, 380); // Adjust bottom margin as needed
        textViewErrorMessage.setLayoutParams(paramsErrorMessage);

        textViewErrorMessage.setText(errorMessage);
        textViewErrorMessage.setTextColor(Color.RED);
        textViewErrorMessage.setPadding(20, 20, 20, 20);

        // Add the error message TextView to the parent RelativeLayout
        parentLayout.addView(textViewErrorMessage);

        // Move the "Enter Key Code" text above the key codes
        TextView enterKeyText = findViewById(R.id.enterKeyText);
        RelativeLayout.LayoutParams paramsEnterKeyText = (RelativeLayout.LayoutParams) enterKeyText.getLayoutParams();
        paramsEnterKeyText.addRule(RelativeLayout.ABOVE, textViewErrorMessage.getId()); // Place "Enter Key Code" text above error message
        enterKeyText.setLayoutParams(paramsEnterKeyText);
    }



}




