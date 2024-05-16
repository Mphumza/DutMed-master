package com.example.dutmed;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
    private EditText genderField, ageField, treatmentsField, allergiesField;
    private DutMedDbHelper dbHelper;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews(view);
        dbHelper = new DutMedDbHelper(getContext());
        userId = getUserId();  // Retrieve user ID here after context is available

        if (userId == -1) {
            Toast.makeText(getContext(), "Invalid user ID, please log in again.", Toast.LENGTH_LONG).show();
            // Optionally consider navigating the user to a login screen
            return view;
        }

        //loadProfileData();

        Button updateButton = view.findViewById(R.id.update_profile_button);
        updateButton.setOnClickListener(v -> updateProfile());
        return view;
    }

    private void initializeViews(View view) {
        genderField = view.findViewById(R.id.gender);
        ageField = view.findViewById(R.id.age);
        treatmentsField = view.findViewById(R.id.current_treatments);
        allergiesField = view.findViewById(R.id.allergies);
    }

    private void loadProfileData() {
        Cursor cursor = dbHelper.getProfileByUserId(userId);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    safelyLoadProfileData(cursor);
                } else {
                    Toast.makeText(getContext(), "Failed to load profile data!", Toast.LENGTH_LONG).show();
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void safelyLoadProfileData(Cursor cursor) {
        try {
            int genderIndex = cursor.getColumnIndexOrThrow(DutMedDbHelper.COLUMN_GENDER);
            int ageIndex = cursor.getColumnIndexOrThrow(DutMedDbHelper.COLUMN_AGE);
            int treatmentsIndex = cursor.getColumnIndexOrThrow(DutMedDbHelper.COLUMN_CURRENT_TREATMENTS);
            int allergiesIndex = cursor.getColumnIndexOrThrow(DutMedDbHelper.COLUMN_ALLERGIES);

            String gender = cursor.getString(genderIndex);
            int age = cursor.getInt(ageIndex);
            String treatments = cursor.getString(treatmentsIndex);
            String allergies = cursor.getString(allergiesIndex);

            genderField.setText(gender);
            ageField.setText(String.valueOf(age));
            treatmentsField.setText(treatments);
            allergiesField.setText(allergies);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Database schema error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateProfile() {
        String gender = genderField.getText().toString();
        try {
            int age = Integer.parseInt(ageField.getText().toString());
            String treatments = treatmentsField.getText().toString();
            String allergies = allergiesField.getText().toString();

            boolean updateSuccessful = dbHelper.updateProfile(userId, gender, age, treatments, allergies);
            if (updateSuccessful) {
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update profile!", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid input format: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("USER_ID", -1);
    }
}
