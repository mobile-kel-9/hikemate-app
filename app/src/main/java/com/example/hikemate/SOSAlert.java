package com.example.hikemate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SOSAlert extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_gyroalerttest);

        Button backButton = findViewById(R.id.back_btn);

            backButton.setOnClickListener(view -> {
            Intent intent = new Intent(SOSAlert.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
