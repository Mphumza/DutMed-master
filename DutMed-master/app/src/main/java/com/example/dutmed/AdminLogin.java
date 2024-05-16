package com.example.dutmed;

import static com.example.dutmed.R.id.AdminloginButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import org.checkerframework.checker.units.qual.A;

public class AdminLogin extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private DutMedDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        dbHelper = new DutMedDbHelper(this);

        emailField = findViewById(R.id.adminEmail);
        passwordField = findViewById(R.id.AdminPassword);

        Button AdminloginButton = findViewById(R.id.AdminloginButton);
        AdminloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        // Retrieve salt for the user from the database first
        String salt = dbHelper.getSaltByEmail(email); // You need to implement this method in dbHelper
        String hashedPassword = dbHelper.hashPassword(password, salt);

        if (dbHelper.checkUserCredentials(email,password, "admin")) {
            // Success, navigate to the admin dashboard
            Intent intent = new Intent(AdminLogin.this, AdminDashboard.class);
            startActivity(intent);
            finish();
        } else {
            // Failure, show an error message
            Toast.makeText(this, "Invalid email or password for admin account", Toast.LENGTH_LONG).show();
        }
    }
}



