package com.example.dutmed;

import android.annotation.SuppressLint;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.view.View;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class AppointmentActivity extends AppCompatActivity {


    private Spinner spinnerCampus;
    private DatePicker datePicker;
    private Spinner spinnerTimeSlots;
    private Button btnSubmit;
    private DutMedDbHelper dbHelper; // Database helper object


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_appointments); // Make sure to create and reference the correct layout
        dbHelper = new DutMedDbHelper(this);

        spinnerCampus = findViewById(R.id.spinnerCampus);
        datePicker = findViewById(R.id.datePicker);
        spinnerTimeSlots = findViewById(R.id.spinnerTimeSlots);
        btnSubmit = findViewById(R.id.btnBook);

        setupCampusSpinner();
        setupDatePicker();
        setupTimeSlots();
        setupBookingButton();
    }

    // Your method implementations remain the same...
    private void setupCampusSpinner() {
        // Sample campus names, replace with actual campus names
        String[] campuses = new String[] {
                "Steve Biko Campus", "Ritson Campus", "ML Sultan Campus", "City Campus"
        };
        ArrayAdapter<String> campusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, campuses);
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCampus.setAdapter(campusAdapter);
    }

    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);  // Start from the next day

        // Set the minimum date to tomorrow
        datePicker.setMinDate(calendar.getTimeInMillis());

        // Disable weekends in the DatePicker dialog
        datePicker.setCalendarViewShown(false); // Deprecate the calendar view
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datePicker.getMinDate());
        while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            datePicker.setMinDate(cal.getTimeInMillis());
        }

        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (newDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || newDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    if (newDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        newDate.add(Calendar.DAY_OF_MONTH, 2); // Skip to Monday
                    } else {
                        newDate.add(Calendar.DAY_OF_MONTH, 1); // Skip to Monday
                    }
                    datePicker.updateDate(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH));
                }
            }
        });
    }

    private void setupTimeSlots() {
        // Sample time slots, replace with dynamic data if needed
        String[] timeSlots = new String[]{
                "09:00 AM - 09:20 AM", "09:25 AM - 09:45 AM", "10:10 AM - 10:30 AM", "10:35 AM - 10:55 AM", "11:00 AM - 11:20 AM", "11:25 AM - 11:45 AM", "11:50 AM - 12:10 PM", "12:15 PM - 12:35 PM", "12:40 PM - 01:00 PM",
                //Break time
                "02:00 PM - 02:15 PM", "02:20 PM - 02:40 PM", "02:45 PM - 03:05 PM", "03:10 PM - 03:30 PM", "03:35 PM - 03:55 PM", "03:50 PM - 04:10 PM", "04:15 PM - 04:35 PM", "04:40 PM - 05:00 PM"
        };
        ArrayAdapter<String> timeSlotAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, timeSlots);
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeSlots.setAdapter(timeSlotAdapter);
    }

    private void setupBookingButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAppointment();
            }
        });
    }


    public void attemptAppointment() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);  // Default to -1 if not found
        if (userId == -1) {
            Toast.makeText(this, "Session error or user not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedCampus = spinnerCampus.getSelectedItem().toString();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String selectedTimeSlot = spinnerTimeSlots.getSelectedItem().toString();

        // Format the date into a string
        @SuppressLint("DefaultLocale") String formattedDate = String.format("%d-%02d-%02d", year, month + 1, day);

        if (!dbHelper.isSlotBooked(selectedCampus, selectedTimeSlot, formattedDate)) {
            // Slot is not booked, proceed with booking
            long result = dbHelper.insertAppointments(userId, selectedCampus, formattedDate, selectedTimeSlot);
            if (result != -1) {
                NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.bell_icon)
                        .setContentTitle("Notification")
                        .setContentText("Booking for " + formattedDate + " at: " + selectedTimeSlot + " on " + selectedCampus + ", has been Confirmed!");

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, mbuilder.build());
                Toast.makeText(this, "Booking successful!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AppointmentActivity.this, UserAppointmentActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Failed to book slot.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Slot already booked for this date.", Toast.LENGTH_LONG).show();
        }
    }




}
