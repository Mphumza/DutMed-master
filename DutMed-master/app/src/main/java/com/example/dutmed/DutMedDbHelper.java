package com.example.dutmed;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DutMedDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DutMed.db";
    private static final int DATABASE_VERSION = 7;
    private Context context; // Add this line to store the context

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PROFILES = "profiles";
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String TABLE_SYMPTOM_CHECKER = "symptom_checker";
    public static final String TABLE_HEALTH_RESOURCES = "health_resources";
    public static final String TABLE_FEEDBACK = "feedback";
    private static final String COLUMN_SALT = "salt";

    // Users table columns
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_ROLE = "role";

    // Profiles table columns
    public static final String COLUMN_PROFILE_ID = "profile_id";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_CURRENT_TREATMENTS = "current_treatments";
    public static final String COLUMN_ALLERGIES = "allergies";

    // Appointments table columns
    public static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME_SLOT = "time_slot";
    private static final String COLUMN_CAMPUS = "campus";

    // Symptom Checker table columns
    public static final String COLUMN_ENTRY_ID = "entry_id";
    public static final String COLUMN_SYMPTOMS = "symptoms";
    public static final String COLUMN_SEVERITY_SCORE = "severity_score";
    public static final String COLUMN_ADVICE = "advice";

    // Health Resources table columns
    public static final String COLUMN_RESOURCE_ID = "resource_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_IMAGE_URL = "image_url";

    // Feedback table columns
    public static final String COLUMN_FEEDBACK_ID = "feedback_id";
    public static final String COLUMN_FEEDBACK_TEXT = "feedback_text";
    public static final String COLUMN_SUGGESTIONS = "suggestions";
    public static final String COLUMN_DATE_SUBMITTED = "date_submitted";

    // SQL statements to create tables
    private static final String CREATE_TABLE_USERS = "CREATE TABLE users (" +
            "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "email TEXT UNIQUE NOT NULL, " +
            "password TEXT NOT NULL, " +
            "salt TEXT NOT NULL, " + // Ensure this line is added
            "first_name TEXT, " +
            "last_name TEXT, " +
            "role TEXT)";

    private static final String CREATE_TABLE_PROFILES = "CREATE TABLE profiles (" +
            "profile_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "gender TEXT, " +
            "age INTEGER, " +
            "current_treatments TEXT, " +
            "allergies TEXT, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id))";

    private static final String CREATE_TABLE_APPOINTMENTS = "CREATE TABLE appointments (" +
            "appointment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "date TEXT NOT NULL, " +
            "time_slot TEXT NOT NULL, "+
            "campus TEXT NOT NULL, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE)";


    private static final String CREATE_TABLE_SYMPTOM_CHECKER = "CREATE TABLE symptom_checker (" +
            "entry_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "symptoms TEXT, " +
            "severity_score INTEGER, " +
            "advice TEXT, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id))";

    private static final String CREATE_TABLE_HEALTH_RESOURCES = "CREATE TABLE health_resources (" +
            "resource_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT NOT NULL, " +
            "content TEXT NOT NULL, " +
            "type TEXT NOT NULL, " +
            "image_url TEXT)";

    private static final String CREATE_TABLE_FEEDBACK = "CREATE TABLE feedback (" +
            "feedback_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "entry_id INTEGER NOT NULL, " +
            "feedback_text TEXT, " +
            "suggestions TEXT, " +
            "date_submitted TEXT NOT NULL, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
            "FOREIGN KEY(entry_id) REFERENCES symptom_checker(entry_id))";

    //Constructor
    public DutMedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // Store the context
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    //void methods
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PROFILES);
        db.execSQL(CREATE_TABLE_APPOINTMENTS);
        db.execSQL(CREATE_TABLE_SYMPTOM_CHECKER);
        db.execSQL(CREATE_TABLE_HEALTH_RESOURCES);
        db.execSQL(CREATE_TABLE_FEEDBACK);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old appointments table if exists (or any other outdated tables)
        // Add any additional tables or updates needed here
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS profiles");
        db.execSQL("DROP TABLE IF EXISTS appointments");
        db.execSQL("DROP TABLE IF EXISTS symptom_checker");
        db.execSQL("DROP TABLE IF EXISTS health_resources");
        db.execSQL("DROP TABLE IF EXISTS feedback");
    }


    String generateSalt() {
        Random random = new Random();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    public String hashPassword(String password, String salt) {
        // Ensure this method is public if called outside the DBHelper class
        try {
            password = salt + password; // Combine salt and password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }


    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    //system--------------------------------------------------------------------------------Break--------------------------------------------------------------------


    //MORE Voids
    public void handleDeleteAppointment(int userId, int appointmentId, String userEmail) {
        String userRole = getUserRole(userEmail); // Now userEmail is passed as a parameter
        if (deleteAppointment(userId, appointmentId, userRole)) {
            System.out.println("Appointment deleted successfully.");
        } else {
            System.out.println("Failed to delete the appointment.");
        }
    }

    public Cursor loginUser(String email, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Include the email in the columns array
        String[] columns = {COLUMN_USER_ID, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_SALT, COLUMN_FIRST_NAME};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_ROLE + " = ?";
        String[] selectionArgs = {email, role};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD);
            int saltIndex = cursor.getColumnIndexOrThrow(COLUMN_SALT);
            String storedPassword = cursor.getString(passwordIndex);
            String salt = cursor.getString(saltIndex);

            if (storedPassword != null && storedPassword.equals(hashPassword(password, salt))) {
                return cursor;  // Return the cursor containing user data
            }
            cursor.close();  // Close the cursor to avoid memory leaks
        }
        db.close();
        return null;  // Return null if login fails
    }


    //display
    public void displayHealthResources() {
        DutMedDbHelper dbHelper = new DutMedDbHelper(getContext());
        List<HealthResource> resources = dbHelper.getAllHealthResources();

        // You can process the list of resources however you need. For example, you could log the details:
        for (HealthResource resource : resources) {
            Log.d("HealthResource", "Title: " + resource.getTitle() + ", Content: " + resource.getContent());
        }

        dbHelper.close();
    }

    public boolean checkUserCredentials(String email, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_PASSWORD, COLUMN_SALT};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_ROLE + " = ?";
        String[] selectionArgs = {email, role};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            int saltIndex = cursor.getColumnIndex(COLUMN_SALT);

            if (passwordIndex == -1 || saltIndex == -1) {
                cursor.close();
                db.close();
                return false;
            }

            String storedPassword = cursor.getString(passwordIndex);
            String salt = cursor.getString(saltIndex);
            cursor.close();
            db.close();

            return storedPassword.equals(hashPassword(password, salt));
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return false;
    }


    //void classes (add)

    public long insertUser(String email, String password, String role, String firstName, String lastName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Generate a new salt
        String salt = generateSalt();

        // Hash the password along with the salt
        String hashedPassword = hashPassword(password, salt);

        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_SALT, salt); // Correctly store the generated salt
        values.put(COLUMN_ROLE, role);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);

        long id = db.insert(TABLE_USERS, null, values);
        db.close(); // Close the database connection to prevent leaks
        return id;
    }

    public long insertAppointments(int user_Id, String campus, String date, String timeSlot) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user_Id);  // Ensure this column is defined in your database helper constants
        values.put(COLUMN_CAMPUS, campus);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME_SLOT, timeSlot);

        long newRowId = -1;
        try {
            newRowId = db.insertOrThrow(TABLE_APPOINTMENTS, null, values);
        } catch (SQLiteConstraintException e) {
            Log.e("Database", "Constraint failure: " + e.getMessage());
        } finally {
            db.close();
        }
        return newRowId;
    }



    public boolean isSlotBooked(String campusName, String slotTime, String bookingDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE "
                + COLUMN_CAMPUS + " = ? AND "
                + COLUMN_TIME_SLOT + " = ? AND "
                + COLUMN_DATE + " = ?";
        String[] selectionArgs = {campusName, slotTime, bookingDate};

        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean isBooked = cursor.moveToFirst(); // True if cursor is not empty
        cursor.close();
        db.close();
        return isBooked;
    }


    public long addProfile(int userId, String gender, int age, String treatments, String allergies) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_CURRENT_TREATMENTS, treatments);
        values.put(COLUMN_ALLERGIES, allergies);

        long result = db.insert(TABLE_PROFILES, null, values);
        db.close();
        return result;
    }

    public boolean updateProfile(int userId, String gender, int age, String treatments, String allergies) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_CURRENT_TREATMENTS, treatments);
        values.put(COLUMN_ALLERGIES, allergies);

        // Update profile where user_id matches
        int updatedRows = db.update(TABLE_PROFILES, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return updatedRows > 0;
    }


    public long addSymptomData(int userId, String symptoms, int severityScore, String advice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SYMPTOMS, symptoms);
        values.put(COLUMN_SEVERITY_SCORE, severityScore);
        values.put(COLUMN_ADVICE, advice);

        long result = db.insert(TABLE_SYMPTOM_CHECKER, null, values);
        db.close();
        return result;
    }

    public long addHealthResource(String title, String content, String type, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_IMAGE_URL, imageUrl);

        long result = db.insert(TABLE_HEALTH_RESOURCES, null, values);
        db.close();
        return result;
    }

    public long addFeedback(int userId, int entryId, String feedbackText, String suggestions, String dateSubmitted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_ENTRY_ID, entryId);
        values.put(COLUMN_FEEDBACK_TEXT, feedbackText);
        values.put(COLUMN_SUGGESTIONS, suggestions);
        values.put(COLUMN_DATE_SUBMITTED, dateSubmitted);

        long result = db.insert(TABLE_FEEDBACK, null, values);
        db.close();
        return result;
    }

    public long insertAdminUser(String email, String hashedPassword, String salt, String firstName, String lastName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_SALT, salt);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_ROLE, "admin"); // Assuming the role is hardcoded as 'admin' for this method

        long userId = db.insert(TABLE_USERS, null, values);
        db.close();
        return userId;
    }


    public boolean adminExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_EMAIL + " = ? AND " + COLUMN_ROLE + " = 'admin'",
                new String[]{email}, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }



    //retrieval (get)
    @SuppressLint("Range")
    public String getSaltByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String salt = null;
        Cursor cursor = db.query(
                TABLE_USERS,         // The table to query
                new String[]{COLUMN_SALT}, // The columns to return
                COLUMN_EMAIL + " = ?", // The columns for the WHERE clause
                new String[]{email}, // The values for the WHERE clause
                null,  // don't group the rows
                null,  // don't filter by row groups
                null   // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {
            salt = cursor.getString(cursor.getColumnIndex(COLUMN_SALT));
            cursor.close();
        }
        db.close();
        return salt;
    }

    public String getUserRole(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ROLE};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        String role = null;
        if (cursor != null && cursor.moveToFirst()) {
            int roleIndex = cursor.getColumnIndex(COLUMN_ROLE);
            if (roleIndex != -1) {  // Check if the index is valid
                role = cursor.getString(roleIndex);
            }
            cursor.close();
        }
        db.close();
        return role;  // Returns the role of the user or null if not found or if the column index is invalid.
    }

    public List<Appointment> getAppointmentsByUserId(int user_id) {
        List<Appointment> appointmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Assuming TABLE_USERS is your user table and COLUMN_USER_EMAIL is the email field
        String sql = "SELECT a.*, u." + COLUMN_EMAIL +
                " FROM " + TABLE_APPOINTMENTS + " a " +
                " JOIN " + TABLE_USERS + " u ON a." + COLUMN_USER_ID + " = u." + COLUMN_USER_ID +
                " WHERE a." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(user_id)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Appointment appointment = new Appointment(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CAMPUS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TIME_SLOT))
                );
                appointmentList.add(appointment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return appointmentList;
    }



    //Admin

    public List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FEEDBACK, null, null, null, null, null, null);

        int idIndex = cursor.getColumnIndex(COLUMN_FEEDBACK_ID);
        int userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID);
        int entryIdIndex = cursor.getColumnIndex(COLUMN_ENTRY_ID);
        int textIndex = cursor.getColumnIndex(COLUMN_FEEDBACK_TEXT);
        int suggestionsIndex = cursor.getColumnIndex(COLUMN_SUGGESTIONS);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE_SUBMITTED);

        if (idIndex == -1 || userIdIndex == -1 || entryIdIndex == -1 || textIndex == -1 || suggestionsIndex == -1 || dateIndex == -1) {
            Log.e("Database", "One or more columns not found in the cursor.");
            cursor.close();
            db.close();
            return feedbackList; // Return empty list or handle error appropriately
        }

        if (cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback(
                        cursor.getInt(userIdIndex),
                        cursor.getInt(entryIdIndex),
                        cursor.getString(textIndex),
                        cursor.getString(suggestionsIndex),
                        cursor.getString(dateIndex)
                );
                feedbackList.add(feedback);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return feedbackList;
    }


    public Cursor getProfileByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_PROFILE_ID, COLUMN_GENDER, COLUMN_AGE, COLUMN_CURRENT_TREATMENTS, COLUMN_ALLERGIES};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        return db.query(TABLE_PROFILES, columns, selection, selectionArgs, null, null, null);
    }


    public List<HealthResource> getAllHealthResources() {
        List<HealthResource> resources = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HEALTH_RESOURCES, null, null, null, null, null, null);

        int resourceIdIndex = cursor.getColumnIndex(COLUMN_RESOURCE_ID);
        int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
        int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
        int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
        int imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL);

        if (cursor.moveToFirst()) {
            do {
                if (resourceIdIndex != -1 && titleIndex != -1 && contentIndex != -1 &&
                        typeIndex != -1 && imageUrlIndex != -1) {
                    HealthResource resource = new HealthResource(
                            cursor.getInt(resourceIdIndex),
                            cursor.getString(titleIndex),
                            cursor.getString(contentIndex),
                            cursor.getString(typeIndex),
                            cursor.getString(imageUrlIndex)
                    );
                    resources.add(resource);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resources;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Corrected SQL to include a JOIN with the Users table to access the email
        String sql = "SELECT a.*, u.email FROM " + TABLE_APPOINTMENTS + " a " +
                "JOIN " + TABLE_USERS + " u ON a." + COLUMN_USER_ID + " = u." + COLUMN_USER_ID;

        Cursor cursor = db.rawQuery(sql, null);

        // Get column indices safely
        int userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID);
        int emailIndex = cursor.getColumnIndex("email");
        int campusIndex = cursor.getColumnIndex(COLUMN_CAMPUS);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
        int timeSlotIndex = cursor.getColumnIndex(COLUMN_TIME_SLOT);

        if (userIdIndex == -1 || emailIndex == -1 || campusIndex == -1 || dateIndex == -1 || timeSlotIndex == -1) {
            Log.e("DatabaseError", "One or more columns not found in the cursor.");
            cursor.close();
            db.close();
            return appointmentList; // Return empty list or handle error appropriately
        }

        if (cursor.moveToFirst()) {
            do {
                Appointment booking = new Appointment(
                        cursor.getInt(userIdIndex),
                        cursor.getString(emailIndex),
                        cursor.getString(campusIndex),
                        cursor.getString(dateIndex),
                        cursor.getString(timeSlotIndex)
                );
                appointmentList.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return appointmentList;
    }




    public List<Appointment> getAppointmentsByCampus(String campus) {
        List<Appointment> appointmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Construct SQL query to join appointments with users based on user_id and filter by campus
        String sql = "SELECT a.*, u." + COLUMN_EMAIL +
                " FROM " + TABLE_APPOINTMENTS + " a " +
                " JOIN " + TABLE_USERS + " u ON a." + COLUMN_USER_ID + " = u." + COLUMN_USER_ID +
                (campus.equals("All Campuses") ? "" : " WHERE a." + COLUMN_CAMPUS + " = ?");


        // Prepare the selection arguments if filtering by a specific campus
        String[] selectionArgs = campus.equals("All Campuses") ? null : new String[]{campus};

        // Execute the query
        Cursor cursor = campus.equals("All Campuses") ?
                db.rawQuery(sql, null) :
                db.rawQuery(sql, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Appointment booking = new Appointment(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CAMPUS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TIME_SLOT))
                );
                appointmentList.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return appointmentList;
    }




    //deletion (destroy)--------------------------------------------------------------------------------------------------------------------------------------------------
    public boolean deleteAppointment(int userId, int appointmentId, String userRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_APPOINTMENT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(appointmentId)};

        if (!userRole.equals("admin")) {
            // For regular users, add an additional check to ensure they only delete their own appointments
            whereClause += " AND " + COLUMN_USER_ID + " = ?";
            whereArgs = new String[]{String.valueOf(appointmentId), String.valueOf(userId)};
        }

        int deletedRows = db.delete(TABLE_APPOINTMENTS, whereClause, whereArgs);
        db.close();
        return deletedRows > 0;
    }

    public boolean deleteHealthResource(int resourceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_HEALTH_RESOURCES, COLUMN_RESOURCE_ID + " = ?", new String[]{String.valueOf(resourceId)});
        db.close();
        return deletedRows > 0;
    }

//------------------------------------------------------------------------------------------------------------------

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}



