package com.example.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Sidebar extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidebar); // Ensure this is your layout XML

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        buttonMenu = findViewById(R.id.buttonmenu); // ID should match your XML
        buttonMenu1 = findViewById(R.id.buttonmenu1); // ID should match your XML
        navigationView = findViewById(R.id.navigationView); // Make sure this matches the ID in your XML

        // Setup the drawer functionality within the same activity
        setupDrawer();
    }

    private void setupDrawer() {
        // Open the left drawer when buttonMenu is clicked
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START); // Open the left drawer
            }
        });

        // Show popup menu when buttonMenu1 is clicked
        buttonMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create and display the popup menu
                PopupMenu popup = new PopupMenu(Sidebar.this, view);
                popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.settings) {
                        String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the Auth UID
                        Intent settings = new Intent(Sidebar.this, Profile.class);
                        settings.putExtra("USER_ID", authUid);  // Pass Auth UID to Profile
                        startActivity(settings);
                    } else if (item.getItemId() == R.id.aboutus) {
                        Intent aboutus = new Intent(Sidebar.this,Aboutus.class);
                        startActivity(aboutus);
                    } else if (item.getItemId() == R.id.logout) {
                        Intent logout = new Intent(Sidebar.this, Login.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logout);
                        finish();
                    }
                    return false;
                });
                popup.show();
            }
        });

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navDashboard) {
                    Intent dashboard = new Intent(Sidebar.this, Dashboard.class);
                    startActivity(dashboard);
                }

                if (itemId == R.id.navCalendar) {
                    Intent calendar = new Intent(Sidebar.this, WeeklyCalendarActivity.class);
                    startActivity(calendar);
                }

                if (itemId == R.id.navWorkLogs) {
                    Intent WorkLogData = new Intent(Sidebar.this, Worklogdata.class);
                    startActivity(WorkLogData);
                }

                if (itemId == R.id.navAddLogs) {
                    Intent addWorkLog = new Intent(Sidebar.this, Add_WorkLog.class);
                    startActivity(addWorkLog);
                }

                if (itemId == R.id.navFlightlogs) {
                    Intent addFlightLogdata = new Intent(Sidebar.this, FlightsLogData.class);
                    startActivity(addFlightLogdata);
                }

                if (itemId == R.id.navAddFlight) {
                    Intent addFlightLog = new Intent(Sidebar.this, Add_Flightlog.class);
                    startActivity(addFlightLog);
                }

                if (itemId == R.id.navAddMeetings) {
                    Intent addMeeting = new Intent(Sidebar.this, MainActivity.class);
                    startActivity(addMeeting);
                }

                if (itemId == R.id.navMeetinglogs) {
                    Intent addMeeting = new Intent(Sidebar.this, Meetinglogs.class);
                    startActivity(addMeeting);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}
