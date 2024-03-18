// MainActivity.java
package com.example.eulapticketscanner_dnd.textrecognition;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.eulapticketscanner_dnd.LoginActivity;
import com.example.eulapticketscanner_dnd.R;

public class IndexActivity extends AppCompatActivity {
    CardView continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            // Handle the button click event
            Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

}