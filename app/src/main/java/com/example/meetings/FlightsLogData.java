package com.example.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FlightsLogData extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;
    private ProgressBar loadingProgressBar;
    private TextView errorTextView;
    private TableLayout flightDataTable;
    private SearchView searchView;
    private List<Flightlog> flightlogList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights_log_data);

        initializeViews();
        setupDrawer();
        setupSearchView();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnClickListener(view -> {
            searchView.setIconified(false);  // Expands the search bar
            searchView.requestFocus();       // Requests focus for input
        });

        // Initialize Firebase and fetch data
        databaseReference = FirebaseDatabase.getInstance().getReference("Flightlog");
        flightlogList = new ArrayList<>();
        fetchFlightLogs();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        buttonMenu = findViewById(R.id.buttonmenu);
        buttonMenu1 = findViewById(R.id.buttonmenu1);
        navigationView = findViewById(R.id.navigationView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        errorTextView = findViewById(R.id.errorTextView);
        flightDataTable = findViewById(R.id.flightDataTable);
        searchView = findViewById(R.id.searchView);

        // Setup popup menu for profile button
        buttonMenu1.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(FlightsLogData.this, view);
            popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.settings) {
                    String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the Auth UID
                    Intent settings = new Intent(FlightsLogData.this, Profile.class);
                    settings.putExtra("USER_ID", authUid);  // Pass Auth UID to Profile
                    startActivity(settings);
                } else if (item.getItemId() == R.id.aboutus) {
                    Intent aboutus = new Intent(FlightsLogData.this,Aboutus.class);
                    startActivity(aboutus);
                } else if (item.getItemId() == R.id.logout) {
                    Intent logout = new Intent(FlightsLogData.this, Login.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logout);
                    finish();
                }
                return false;
            });

            popup.show();
        });
    }

    private void fetchFlightLogs() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flightlogList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Flightlog flightlog = snapshot.getValue(Flightlog.class);
                        if (flightlog != null) {
                            flightlogList.add(flightlog);
                        }
                    } catch (Exception e) {
                        Toast.makeText(FlightsLogData.this, "Error parsing data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                displayFlightLogs(flightlogList);
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingProgressBar.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText("Error loading data: " + databaseError.getMessage());
            }
        });
    }

    private void displayFlightLogs(List<Flightlog> logs) {
        // Clear existing rows except header
        int childCount = flightDataTable.getChildCount();
        if (childCount > 1) {
            flightDataTable.removeViews(1, childCount - 1);
        }

        // Add data rows in reverse order (newest first)
        for (int i = logs.size() - 1; i >= 0; i--) {
            Flightlog log = logs.get(i);
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            row.setPadding(8, 8, 8, 8);

            int backgroundColor = (i % 2 == 0)
                    ? ContextCompat.getColor(this, R.color.white)
                    : ContextCompat.getColor(this, R.color.textSecondary);
            row.setBackgroundColor(backgroundColor);

            // Add cells with data from the Flightlog model
            addCell(row, log.getDate());
            addCell(row, log.getDrone());
            addCell(row, log.getStarttime());
            addCell(row, log.getEndtime());
            addCell(row, log.getName());
            addCell(row, log.getDescription());

            row.setAlpha(0f);
            row.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setStartDelay((logs.size() - 1 - i) * 100)
                    .start();


            flightDataTable.addView(row, 1);
        }
    }


    private void addCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        textView.setLayoutParams(params);
        textView.setText(text != null ? text : "");
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setMaxLines(5);
        textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        row.addView(textView);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFlightLogs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFlightLogs(newText);
                return false;
            }
        });
    }

    private void filterFlightLogs(String query) {
        List<Flightlog> filteredList = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase().trim();

        for (Flightlog log : flightlogList) {
            if (log.getDate().toLowerCase().contains(lowercaseQuery) ||
                    log.getDrone().toLowerCase().contains(lowercaseQuery) ||
                    log.getName().toLowerCase().contains(lowercaseQuery) ||
                    log.getDescription().toLowerCase().contains(lowercaseQuery) ||
                    log.getStarttime().toLowerCase().contains(lowercaseQuery) ||
                    log.getEndtime().toLowerCase().contains(lowercaseQuery)) {
                filteredList.add(log);
            }
        }

        displayFlightLogs(filteredList);
    }

    private void setupDrawer() {
        buttonMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navDashboard) {
                Intent dashboard = new Intent(FlightsLogData.this, Dashboard.class);
                startActivity(dashboard);
            } else if (itemId == R.id.navCalendar) {
                Intent calendar = new Intent(FlightsLogData.this,WeeklyCalendarActivity.class);
                startActivity(calendar);
            } else if (itemId == R.id.navWorkLogs) {
                Intent WorkLogData = new Intent(FlightsLogData.this, Worklogdata.class);
                startActivity(WorkLogData);
            } else if (itemId == R.id.navAddLogs) {
                Intent addWorkLog = new Intent(FlightsLogData.this, Add_WorkLog.class);
                startActivity(addWorkLog);
            } else if (itemId == R.id.navFlightlogs) {
                Intent FlightLog = new Intent(FlightsLogData.this, FlightsLogData.class);
                startActivity(FlightLog);
            } else if (itemId == R.id.navAddFlight) {
                Intent addFlightLog = new Intent(FlightsLogData.this, Add_Flightlog.class);
                startActivity(addFlightLog);
            } else if (itemId == R.id.navAddMeetings) {
                Intent addMeeting = new Intent(FlightsLogData.this, MainActivity.class);
                startActivity(addMeeting);
            } else if (itemId == R.id.navMeetinglogs) {
                Intent meetingLogs = new Intent(FlightsLogData.this, Meetinglogs.class);
                startActivity(meetingLogs);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}