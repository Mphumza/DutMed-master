package com.example.dutmed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.activity.OnBackPressedCallback;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setting up the email in the drawer header
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.text_view_id); // Make sure you have a TextView with this ID in your nav_header
        userNameTextView.setText(getUserEmail()); // Set the email

        setupDrawerContent(navigationView);
        setupBackButtonHandling();
        setupHeaderView();

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HealthTopicsFragment())
                    .commit();
        }

        final OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Delegate the back press to the system.
                    setEnabled(false);
                    onBackPressed();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

    }


    private void setupBackButtonHandling() {
        final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.profileFragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .addToBackStack(null)
                        .commit();
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(MainActivity.this, UserAppointmentActivity.class));
            } else if (id == R.id.nav_appointment) {
                startActivity(new Intent(MainActivity.this, AppointmentActivity.class));
            } else if (id == R.id.nav_symptom_checker) {
                startActivity(new Intent(MainActivity.this, QuestionAnswerActivity.class));
            } else if (id == R.id.nav_chatbot) {
                startActivity(new Intent(MainActivity.this, ChatbotActivity.class));
            } else if (id == R.id.nav_feedback) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FeedbackFragment())
                        .addToBackStack(null)
                        .commit();
            } else if (id == R.id.nav_about) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AboutFragment())
                        .addToBackStack(null)
                        .commit();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEmailInView();
    }

    private void setupHeaderView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        updateEmailInView();
    }

    private void updateEmailInView() {
        runOnUiThread(() -> {
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView userNameTextView = headerView.findViewById(R.id.text_view_id);
            String userEmail = getUserEmail();
            userNameTextView.setText(userEmail);
            Log.d("UpdateEmail", "Email set in header: " + userEmail); // Confirm this in logs
        });
    }



    private String getUserEmail() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", "No Email Set");
        Log.d("MainActivity", "Fetched User Email: " + email); // Check logcat for this output
        return email;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_exit) {
            clearUserSession();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearUserSession() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("userEmail"); // Clear user email
        // Optionally clear role if you do not need to remember it after logout
        // editor.remove("userRole");
        editor.apply();

        // Optionally restart login activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }




    @Override
    public boolean onSupportNavigateUp() {
        // This method is called when the up button is pressed. Just handle it for NavComponent
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }


}
