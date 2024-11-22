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

public class Worklogdata extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;
    private ProgressBar loadingProgressBar;
    private TextView errorTextView;
    private TableLayout worklogTable;
    private SearchView searchView;
    private List<Worklog> worklogList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worklogdata);

        // Initialize UI components
        initializeViews();
        setupDrawer();
        setupSearchView();

        // SearchView expand on click
        searchView.setOnClickListener(view -> {
            searchView.setIconified(false);  // Expands the search bar
            searchView.requestFocus();       // Requests focus for input
        });

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Worklog");
        worklogList = new ArrayList<>();

        // Fetch data
        fetchWorklogData();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        buttonMenu = findViewById(R.id.buttonmenu);
        buttonMenu1 = findViewById(R.id.buttonmenu1);
        navigationView = findViewById(R.id.navigationView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        errorTextView = findViewById(R.id.errorTextView);
        worklogTable = findViewById(R.id.flightDataTable);
        searchView = findViewById(R.id.searchView);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterWorklogData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterWorklogData(newText);
                return true;
            }
        });
    }

    private void fetchWorklogData() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                worklogList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Worklog worklog = snapshot.getValue(Worklog.class);
                    if (worklog != null) {
                        worklogList.add(worklog);
                    }
                }
                if (worklogList.isEmpty()) {
                    showEmptyState();
                } else {
                    updateTableWithData(worklogList);
                }
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loadingProgressBar.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText("Error loading data: " + databaseError.getMessage());
            }
        });
    }

    private void showEmptyState() {
        worklogTable.removeViews(1, worklogTable.getChildCount() - 1);
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText("No worklogs found.");
    }

    private void updateTableWithData(List<Worklog> worklogs) {
        // Clear existing rows except header
        int childCount = worklogTable.getChildCount();
        if (childCount > 1) {
            worklogTable.removeViews(1, childCount - 1);
        }

        // Add data rows in reverse order (newest first)
        for (int i = worklogs.size() - 1; i >= 0; i--) {
            Worklog worklog = worklogs.get(i);
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // Add cells
            row.addView(createTableCell(worklog.getDate()));
            row.addView(createTableCell(worklog.getStarttime()));
            row.addView(createTableCell(worklog.getEndtime()));
            row.addView(createTableCell(worklog.getName()));
            row.addView(createTableCell(worklog.getDescription()));

            // Add alternating row background
            if ((worklogTable.getChildCount() - 1) % 2 == 1) { // Adjust index for header row
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.textSecondary));
            }

            // Row fade-in animation
            row.setAlpha(0f);
            row.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setStartDelay((worklogs.size() - 1 - i) * 100)
                    .start();

            worklogTable.addView(row, 1);
        }
    }

    private TextView createTableCell(String text) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        );
        params.setMargins(2, 2, 2, 2);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setMaxLines(5);
        textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        textView.setGravity(android.view.Gravity.CENTER);
        return textView;
    }

    private void filterWorklogData(String query) {
        List<Worklog> filteredList = new ArrayList<>();
        for (Worklog worklog : worklogList) {
            if (worklog.getName().toLowerCase().contains(query.toLowerCase()) ||
                    worklog.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                    worklog.getDate().toLowerCase().contains(query.toLowerCase()) ||
                    worklog.getStarttime().toLowerCase().contains(query.toLowerCase()) ||
                    worklog.getEndtime().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(worklog);
            }
        }

        if (filteredList.isEmpty()) {
            showEmptyState();
        } else {
            errorTextView.setVisibility(View.GONE);
            updateTableWithData(filteredList);
        }
    }

    private void setupDrawer() {
        buttonMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        buttonMenu1.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(Worklogdata.this, view);
            popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.settings) {
                    String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the Auth UID
                    Intent settings = new Intent(Worklogdata.this, Profile.class);
                    settings.putExtra("USER_ID", authUid);  // Pass Auth UID to Profile
                    startActivity(settings);
                } else if (item.getItemId() == R.id.aboutus) {
                    Intent aboutus = new Intent(Worklogdata.this,Aboutus.class);
                    startActivity(aboutus);
                } else if (item.getItemId() == R.id.logout) {
                    Intent logout = new Intent(Worklogdata.this, Login.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logout);
                    finish();
                }
                return false;
            });
            popup.show();
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navDashboard) {
                Intent dashboard = new Intent(Worklogdata.this, Dashboard.class);
                startActivity(dashboard);
            } else if (itemId == R.id.navCalendar) {
                Intent calendar = new Intent(Worklogdata.this, WeeklyCalendarActivity.class);
                startActivity(calendar);
            } else if (itemId == R.id.navWorkLogs) {
                Intent workLogIntent = new Intent(Worklogdata.this, Worklogdata.class);
                startActivity(workLogIntent);
            } else if (itemId == R.id.navAddLogs) {
                Intent addWorkLog = new Intent(Worklogdata.this, Add_WorkLog.class);
                startActivity(addWorkLog);
            } else if (itemId == R.id.navFlightlogs) {
                Intent FlightLog = new Intent(Worklogdata.this, FlightsLogData.class);
                startActivity(FlightLog);
            } else if (itemId == R.id.navAddFlight) {
                Intent addFlightLog = new Intent(Worklogdata.this, Add_Flightlog.class);
                startActivity(addFlightLog);
            } else if (itemId == R.id.navAddMeetings) {
                Intent addMeeting = new Intent(Worklogdata.this, MainActivity.class);
                startActivity(addMeeting);
            } else if (itemId == R.id.navMeetinglogs) {
                Intent meetingLog = new Intent(Worklogdata.this, Meetinglogs.class);
                startActivity(meetingLog);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}
