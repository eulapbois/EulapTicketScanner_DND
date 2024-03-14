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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button submitButton;
    ImageView verificationImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_first_step);

        submitButton = findViewById(R.id.submitButton);
        verificationImage = findViewById(R.id.verification);

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

    private boolean verifyUser(String code1, String code2, String code3, String code4) {
        return code1.equals("1") && code2.equals("2") && code3.equals("3") && code4.equals("4");
    }

    private void onSuccess() {
        Toast.makeText(getApplicationContext(), "User verified successfully", Toast.LENGTH_SHORT).show();
    }

    private void onFailure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verification Failed")
                .setMessage("Invalid keycode")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Replace verification image with error logo
        verificationImage.setImageResource(R.drawable.error);
    }

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

        // Create a TextView to hold the error message
        TextView textViewErrorMessage = new TextView(getApplicationContext());
        textViewErrorMessage.setText(errorMessage);
        textViewErrorMessage.setTextColor(Color.RED);

        // Set padding for the TextView (optional)
        textViewErrorMessage.setPadding(20, 20, 20, 20);

        // Create a LinearLayout to hold the icon and the error message
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER); // Set gravity to center

        // Create an ImageView for the error icon
        ImageView imageViewError = new ImageView(getApplicationContext());
        imageViewError.setImageResource(R.drawable.error);
        // Set padding for the ImageView (optional)
        imageViewError.setPadding(0, 0, 20, 0);

        // Add the error icon and the error message to the LinearLayout
        layout.addView(imageViewError);
        layout.addView(textViewErrorMessage);

        // Create and show custom toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);  // Set the LinearLayout as the view for the toast
        toast.setGravity(Gravity.CENTER, 0, 0); // Center the toast on the screen
        toast.show();
    }

}
