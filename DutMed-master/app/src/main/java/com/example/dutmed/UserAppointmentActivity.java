package com.example.dutmed;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAppointmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentsAdapter appointmentsAdapter;
    private DutMedDbHelper dbHelper;
    private Button refreshButton;
    private int userId;  // To keep email accessible throughout the activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appointments);

        // Retrieve the email passed from the login activity
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);  // Default to -1 if not found
        if (userId == -1) {
            Toast.makeText(this, "Session error or user not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshButton = findViewById(R.id.refreshButton);

        dbHelper = new DutMedDbHelper(this);

        // Initially fetch bookings for the given email
        refreshAppointments();  // Fetch bookings initially and setup the adapter

        // Set up a button click listener to refresh bookings
        refreshButton.setOnClickListener(v -> refreshAppointments());
    }

    private void refreshAppointments() {
        List<Appointment> appointments = dbHelper.getAppointmentsByUserId(userId);
        if (appointmentsAdapter == null) {
            appointmentsAdapter = new AppointmentsAdapter(appointments);
            recyclerView.setAdapter(appointmentsAdapter);
        } else {
            appointmentsAdapter.setAppointments(appointments); // Assuming your adapter has a method to update the data
            appointmentsAdapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
        }
    }
}
