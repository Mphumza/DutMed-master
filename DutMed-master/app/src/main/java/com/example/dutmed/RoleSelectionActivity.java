package com.example.dutmed;

import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        Button patientButton = findViewById(R.id.patientButton);
        Button adminButton = findViewById(R.id.adminButton);

        patientButton.setOnClickListener(v -> {
            // Navigate to Patient Login Activity
            Intent intent = new Intent(RoleSelectionActivity.this, Login.class);
            intent.putExtra("userRole", "Patient"); // Add role as an extra in the intent
            startActivity(intent);
        });

        adminButton.setOnClickListener(v -> {
            // Navigate to Admin Login Activity
            Intent intent = new Intent(RoleSelectionActivity.this, AdminLogin.class);
            intent.putExtra("userRole", "Admin"); // Add role as an extra in the intent
            startActivity(intent);
        });
    }
}