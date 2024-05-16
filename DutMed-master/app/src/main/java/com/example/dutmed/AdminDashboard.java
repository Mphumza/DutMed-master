package com.example.dutmed;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard); // Ensure this matches the layout file name.
        setupBottomNavigationView();
        setupButtonListeners();
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the ID of the selected item

            if (itemId == R.id.AdminDashboard) {
                // Optionally do nothing or refresh the dashboard view
                return true;
            } else if (itemId == R.id.AdminLogout) {
                performLogout();
                return true;
            } else if (itemId == R.id.Notify) {
                // Start NotifyActivity instead of replacing a fragment
                Intent intent = new Intent(AdminDashboard.this, NotifyActivity.class);
                startActivity(intent);
                return true;
            }
            return false; // If none of the above, return false
        });
    }




    private void replaceFragment(AdminProfile fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }

    private void setupButtonListeners() {
        findViewById(R.id.AdminView).setOnClickListener(v -> {
            // Assume ViewAppointmentsActivity is an activity for viewing appointments
            startActivity(new Intent(this, AdminViewAppointment.class));
        });

        /*findViewById(R.id.button2).setOnClickListener(v -> {
            // Assume BookingHistoryActivity is an activity for viewing booking history
            startActivity(new Intent(this, BookingHistoryActivity.class));
        });

        findViewById(R.id.button3).setOnClickListener(v -> {
            // Assume ManageHealthResourcesActivity is an activity to manage health resources
            startActivity(new Intent(this, ManageHealthResources.class));
        });

        findViewById(R.id.button4).setOnClickListener(v -> {
            // Assume FeedbackActivity is an activity to manage feedback
            startActivity(new Intent(this, Feedback.class));
        });**/
    }

    private void performLogout() {
        // Clear preferences or session management
        Intent intent = new Intent(AdminDashboard.this, AdminLogin.class);
        startActivity(intent);
        finish();
    }
}

