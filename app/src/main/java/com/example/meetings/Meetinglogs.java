package com.example.meetings;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class Meetinglogs extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TableLayout meetingDataTable;
    private ProgressBar loadingProgressBar;
    private TextView errorTextView;
    private List<Meeting> meetingsList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetinglogs);

        // Initialize views
        meetingDataTable = findViewById(R.id.meetingDataTable);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        errorTextView = findViewById(R.id.errorTextView);

        mDatabase = FirebaseDatabase.getInstance().getReference("Meeting");

        // Show loading indicator
        showLoadingIndicator();

        // Initialize SearchView and set the listener for filtering
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnClickListener(view -> {
            searchView.setIconified(false);  // Expands the search bar
            searchView.requestFocus();       // Requests focus for input
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // We handle search in real-time, so no action needed on submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMeetings(newText);
                return true;
            }
        });

        // Fetch meeting data from Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                meetingsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Meeting meeting = postSnapshot.getValue(Meeting.class);
                    if (meeting != null) {
                        meetingsList.add(0, meeting);
                    }
                }
                hideLoadingIndicator();
                displayFilteredMeetings(meetingsList); // Display the data after it's loaded
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoadingIndicator();
                Log.e("FirebaseError", "DatabaseError: " + databaseError.getMessage());
                showErrorMessage("Failed to load meetings. Please try again later.");
            }
        });

        drawerLayout = findViewById(R.id.drawerLayout);
        buttonMenu = findViewById(R.id.buttonmenu);
        buttonMenu1 = findViewById(R.id.buttonmenu1);
        navigationView = findViewById(R.id.navigationView);

        setupDrawer();
    }

    // Method to filter meetings based on search text
    private void filterMeetings(String searchText) {
        List<Meeting> filteredList = new ArrayList<>();

        for (Meeting meeting : meetingsList) {
            if (meeting.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                    meeting.getDate().toLowerCase().contains(searchText.toLowerCase()) ||
                    meeting.getMeetingType().toLowerCase().contains(searchText.toLowerCase()) ||
                    meeting.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(meeting);
            }
        }

        // Display filtered meetings
        displayFilteredMeetings(filteredList);
    }

    // Method to display filtered meetings
    // Method to display filtered meetings
    private void displayFilteredMeetings(List<Meeting> filteredMeetings) {
        // Clear previous data (except for the header)
        meetingDataTable.removeViewsInLayout(1, meetingDataTable.getChildCount() - 1);

        // Reverse the order to show the newest meetings at the top
        for (int i = filteredMeetings.size() - 1; i >= 0; i--) {
            Meeting meeting = filteredMeetings.get(i);
            TableRow meetingRow = new TableRow(this);
            meetingRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // Alternate row colors
            int backgroundColor = (i % 2 == 0)
                    ? ContextCompat.getColor(this, R.color.white)
                    : ContextCompat.getColor(this, R.color.textSecondary);
            meetingRow.setBackgroundColor(backgroundColor);

            // Add meeting details
            addStyledCell(meetingRow, meeting.getTitle(), true);
            addStyledCell(meetingRow, meeting.getDate(), false);
            addStyledCell(meetingRow, meeting.getTime(), false);
            addStyledCell(meetingRow, meeting.getMeetingType(), false);
            addStyledCell(meetingRow, meeting.getDescription(), false);

            // Add animation
            meetingRow.setAlpha(0f);
            meetingRow.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setStartDelay((filteredMeetings.size() - 1 - i) * 100);

            meetingDataTable.addView(meetingRow, 1);
        }
    }

    private void setupDrawer() {
        buttonMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle popup menu button for settings/logout
        buttonMenu1.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(Meetinglogs.this, view);
            popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.settings) {
                    Intent settings = new Intent(Meetinglogs.this,Profile.class);
                    startActivity(settings);
                } else if (item.getItemId() == R.id.aboutus) {
                    Intent aboutus = new Intent(Meetinglogs.this,Aboutus.class);
                    startActivity(aboutus);
                } else if (item.getItemId() == R.id.logout) {
                    Intent logout = new Intent(Meetinglogs.this, Login.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logout);
                    finish();
                }
                return false;
            });
            popup.show();
        });

        // Handle navigation item selection from the sidebar
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navDashboard) {
                String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the Auth UID
                Intent settings = new Intent(Meetinglogs.this, Profile.class);
                settings.putExtra("USER_ID", authUid);  // Pass Auth UID to Profile
                startActivity(settings);
            } else if (itemId == R.id.navCalendar) {
                Intent calendar = new Intent(Meetinglogs.this, WeeklyCalendarActivity.class);
                startActivity(calendar);
            } else if (itemId == R.id.navWorkLogs) {
                Intent WorkLogData = new Intent(Meetinglogs.this, Worklogdata.class);
                startActivity(WorkLogData);
            } else if (itemId == R.id.navAddLogs) {
                Intent addWorkLog = new Intent(Meetinglogs.this, Add_WorkLog.class);
                startActivity(addWorkLog);
            } else if (itemId == R.id.navFlightlogs) {
                Intent FlightLog = new Intent(Meetinglogs.this, FlightsLogData.class);
                startActivity(FlightLog);
            } else if (itemId == R.id.navAddFlight) {
                Intent addFlightLog = new Intent(Meetinglogs.this, Add_Flightlog.class);
                startActivity(addFlightLog);
            } else if (itemId == R.id.navAddMeetings) {
                Intent addMeeting = new Intent(Meetinglogs.this, MainActivity.class);
                startActivity(addMeeting);
            } else if (itemId == R.id.navMeetinglogs) {
                Intent meetingLogs = new Intent(Meetinglogs.this, Meetinglogs.class);
                startActivity(meetingLogs);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void addStyledCell(TableRow row, String text, boolean isTitle) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1));
        textView.setPadding(16, 24, 16, 24);
        textView.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
        textView.setTextSize(isTitle ? 15 : 14);
        textView.setTypeface(null, isTitle ? Typeface.BOLD : Typeface.NORMAL);
        textView.setGravity(Gravity.CENTER);
        textView.setElevation(2f);

        row.addView(textView);
    }

    private void showLoadingIndicator() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        if (errorTextView != null) {
            errorTextView.setVisibility(View.GONE);
        }
    }

    private void hideLoadingIndicator() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.GONE);
        }
    }

    private void showErrorMessage(String message) {
        if (errorTextView != null) {
            errorTextView.setText(message);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }
}