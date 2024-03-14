//LoginActivity.java
package com.example.eulapticketscanner_dnd;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

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
                    onFailure();
                } else {
                    // Simulate user verification
                    boolean userVerified = verifyUser(code1, code2, code3, code4);

                    if (userVerified) {
                        onSuccess();
                    } else {
                        onFailureWithDelay("User verification failed");
                    }
                }
            } catch (Exception e) {
                onFailureWithDelay("An error occurred: " + e.getMessage());
            }
        });
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
    private void onFailure() {
        // Display error message using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verification Failed")
                .setMessage("Invalid keycode")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Do nothing or handle as needed
                    dialog.dismiss(); // Dismiss the dialog
                })
                .setCancelable(false); // Make it non-cancelable
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        paramsErrorMessage.setMargins(0, 20, 0, 0); // Adjust top margin as needed
        textViewErrorMessage.setLayoutParams(paramsErrorMessage);

        textViewErrorMessage.setText(errorMessage);
        textViewErrorMessage.setTextColor(Color.RED);
        textViewErrorMessage.setPadding(20, 20, 20, 20);

        // Add the error message TextView to the parent RelativeLayout
        parentLayout.addView(textViewErrorMessage);

        // Move the "Enter Key Code" text above the key codes
        TextView enterKeyText = findViewById(R.id.enterKeyText);
        RelativeLayout.LayoutParams paramsEnterKeyText = (RelativeLayout.LayoutParams) enterKeyText.getLayoutParams();
        paramsEnterKeyText.addRule(RelativeLayout.BELOW, textViewErrorMessage.getId()); // Place "Enter Key Code" text below error message
        enterKeyText.setLayoutParams(paramsEnterKeyText);
    }


}




