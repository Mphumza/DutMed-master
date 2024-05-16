package com.example.dutmed;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private String userRole;  // Store user role
    private DutMedDbHelper dbHelper;  // Database helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = new DutMedDbHelper(this);  // Initialize the database helper

        // Initialize the EditText fields
        email = findViewById(R.id.email_input);
        pass = findViewById(R.id.password_input);

        // Get role either from intent or SharedPreferences
        userRole = getIntent().getStringExtra("userRole");
        if (userRole == null) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            userRole = prefs.getString("userRole", ""); // Fetch saved role, default to empty string if not found
        }

        Log.d("Login", "Received Role: " + userRole);

        Button signUpButton = findViewById(R.id.signup_btn);
        if (signUpButton != null) {
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToRegister();  // Navigate to Register activity with role
                }
            });
        } else {
            Toast.makeText(this, "Sign Up button not initialized", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin(email.getText().toString(), pass.getText().toString());
            }
        });
    }

    private void performLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty() || userRole == null) {
            Toast.makeText(Login.this, "Email, password, or role cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor userCursor = dbHelper.loginUser(email, password, userRole);
        if (userCursor != null && userCursor.moveToFirst()) {
            int userIdIndex = userCursor.getColumnIndexOrThrow(DutMedDbHelper.COLUMN_USER_ID);
            int userId = userCursor.getInt(userIdIndex);
            userCursor.close();  // Close cursor immediately after use

            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("userId", userId); // Store the userId for global access
            editor.putString("userRole", userRole); // Store the role if needed elsewhere in the app
            editor.apply();

            userCursor.close();

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (userCursor != null) {
                userCursor.close();
            }
            Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }




    private boolean areCredentialsValid(String email, String password) {
        return dbHelper.checkUserCredentials(email, password, userRole);
    }


    private void navigateToRegister() {
        Intent intent = new Intent(Login.this, Register.class);
        intent.putExtra("userRole", userRole);  // Ensure role is included in the intent
        Log.d("Login", "Sending Role to Register: " + userRole);
        startActivity(intent);
    }
}
