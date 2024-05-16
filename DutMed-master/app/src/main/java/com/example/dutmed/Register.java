package com.example.dutmed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private String userRole;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private DutMedDbHelper dbHelper;  // Database helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DutMedDbHelper(this);  // Initialize the database helper

        // Initialize UI components
        firstNameInput = findViewById(R.id.firstname_input);
        lastNameInput = findViewById(R.id.lastname_input);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirmpassword_input);
        TextView roleTextView = findViewById(R.id.userRoleTextView);

        // Get user role from Intent
        userRole = getIntent().getStringExtra("userRole");
        Log.d("Register", "Received role: " + userRole);

        // Set the role in TextView or show default text if not received
        if (userRole != null && !userRole.isEmpty()) {
            roleTextView.setText(userRole);
        } else {
            roleTextView.setText(R.string.role_not_received);
            Log.e("Register", "User role not received");
        }

        Button createProfileButton = findViewById(R.id.createprofile_btn);
        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                if (validateInputs(firstName, lastName, email, password, confirmPassword)) {
                    registerAccount(firstName, lastName, email, password);  // Make sure all parameters are passed
                }
            }
        });

    }

    private void registerAccount(String firstName, String lastName, String email, String password) {
        long userId = dbHelper.insertUser(email, password, userRole, firstName, lastName);  // Assuming this is your method signature
        if (userId == -1) {
            Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        }
    }


    private boolean validateInputs(String firstName, String lastName, String email, String password, String confirmPassword) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!email.contains("@")) {
            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the register activity
    }
}
