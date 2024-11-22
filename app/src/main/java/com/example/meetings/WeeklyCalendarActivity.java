package com.example.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class WeeklyCalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WeeklyCalendarAdapter adapter;
    private List<Meeting> meetings;
    private Calendar currentCalendar;
    private int currentWeekOffset = 0;
    private TextView monthText;
    private TextView[] dayTexts = new TextView[7];
    private TextView[] dateTexts = new TextView[7];
    private Calendar[] displayedWeekDates = new Calendar[7];
    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        currentCalendar = Calendar.getInstance();
        initializeViews();
        meetings = new ArrayList<>();
        adapter = new WeeklyCalendarAdapter(this, meetings, displayedWeekDates);
        recyclerView.setAdapter(adapter);

        updateCalendarDisplay();

        drawerLayout = findViewById(R.id.drawerLayout);
        buttonMenu = findViewById(R.id.buttonmenu);
        buttonMenu1 = findViewById(R.id.buttonmenu1);
        navigationView = findViewById(R.id.navigationView);

        setupDrawer();

        // Set up navigation buttons
        ImageView prevWeek = findViewById(R.id.prev_week);
        ImageView nextWeek = findViewById(R.id.next_week);
        prevWeek.setOnClickListener(v -> navigateToWeek(-1));
        nextWeek.setOnClickListener(v -> navigateToWeek(1));

        fetchMeetingsFromFirebase();
    }

    private void initializeViews() {
        monthText = findViewById(R.id.month_text);
        recyclerView = findViewById(R.id.recycler_view_calendar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize day header TextViews
        dayTexts[0] = findViewById(R.id.day_sun);
        dayTexts[1] = findViewById(R.id.day_mon);
        dayTexts[2] = findViewById(R.id.day_tue);
        dayTexts[3] = findViewById(R.id.day_wed);
        dayTexts[4] = findViewById(R.id.day_thu);
        dayTexts[5] = findViewById(R.id.day_fri);
        dayTexts[6] = findViewById(R.id.day_sat);

        // Initialize date TextViews
        dateTexts[0] = findViewById(R.id.date_sun);
        dateTexts[1] = findViewById(R.id.date_mon);
        dateTexts[2] = findViewById(R.id.date_tue);
        dateTexts[3] = findViewById(R.id.date_wed);
        dateTexts[4] = findViewById(R.id.date_thu);
        dateTexts[5] = findViewById(R.id.date_fri);
        dateTexts[6] = findViewById(R.id.date_sat);

        for (int i = 0; i < dayTexts.length; i++) {
            final int index = i;
            dayTexts[i].setOnClickListener(v -> onDayHeaderClicked(displayedWeekDates[index]));
        }

        // Apply the correct text color based on the current theme
        applyTextColorBasedOnTheme();
    }

    private void applyTextColorBasedOnTheme() {
        // Check the current theme (dark or light mode)
        int textColor = getResources().getColor(android.R.color.black);
        if (isDarkMode()) {
            textColor = getResources().getColor(android.R.color.white);
        }

        // Set text color for day headers and month
        monthText.setTextColor(textColor);
        for (TextView dayText : dayTexts) {
            dayText.setTextColor(textColor);
        }

        // Set text color for dates, except for current date which will remain black
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < dateTexts.length; i++) {
            Calendar displayedDate = displayedWeekDates[i];
            if (displayedDate != null && !isToday(displayedDate)) {
                dateTexts[i].setTextColor(textColor);
            }
        }
    }

    private boolean isDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    private void setupDrawer() {
        buttonMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle popup menu button for settings/logout
        buttonMenu1.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(WeeklyCalendarActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.settings) {
                    String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the Auth UID
                    Intent settings = new Intent(WeeklyCalendarActivity.this, Profile.class);
                    settings.putExtra("USER_ID", authUid);  // Pass Auth UID to Profile
                    startActivity(settings);
                } else if (item.getItemId() == R.id.aboutus) {
                    Intent aboutus = new Intent(WeeklyCalendarActivity.this,Aboutus.class);
                    startActivity(aboutus);
                } else if (item.getItemId() == R.id.logout) {
                    Intent logout = new Intent(WeeklyCalendarActivity.this, Login.class);
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
                Intent dashboard = new Intent(WeeklyCalendarActivity.this, Dashboard.class);
                startActivity(dashboard);
            } else if (itemId == R.id.navCalendar) {
                Intent calendar = new Intent(WeeklyCalendarActivity.this, WeeklyCalendarActivity.class);
                startActivity(calendar);
            } else if (itemId == R.id.navWorkLogs) {
                Intent WorkLogData = new Intent(WeeklyCalendarActivity.this, Worklogdata.class);
                startActivity(WorkLogData);
            } else if (itemId == R.id.navAddLogs) {
                Intent addWorkLog = new Intent(WeeklyCalendarActivity.this, Add_WorkLog.class);
                startActivity(addWorkLog);
            } else if (itemId == R.id.navFlightlogs) {
                Intent FlightLog = new Intent(WeeklyCalendarActivity.this, FlightsLogData.class);
                startActivity(FlightLog);
            } else if (itemId == R.id.navAddFlight) {
                Intent addFlightLog = new Intent(WeeklyCalendarActivity.this, Add_Flightlog.class);
                startActivity(addFlightLog);
            } else if (itemId == R.id.navAddMeetings) {
                Intent addMeeting = new Intent(WeeklyCalendarActivity.this, MainActivity.class);
                startActivity(addMeeting);
            } else if (itemId == R.id.navMeetinglogs) {
                Intent meetingLogs = new Intent(WeeklyCalendarActivity.this, Meetinglogs.class);
                startActivity(meetingLogs);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void updateCalendarDisplay() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthText.setText(monthFormat.format(currentCalendar.getTime()));

        Calendar weekStart = (Calendar) currentCalendar.clone();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            Calendar day = (Calendar) weekStart.clone();
            day.add(Calendar.DAY_OF_WEEK, i);
            displayedWeekDates[i] = day;

            dateTexts[i].setText(dateFormat.format(day.getTime()));

            // Set styling for current date
            if (isToday(day)) {
                dateTexts[i].setBackgroundResource(R.drawable.current_day_background);
                dateTexts[i].setTextColor(getResources().getColor(android.R.color.black)); // Force black text for current date
            } else {
                dateTexts[i].setBackgroundResource(0);
                // Let the theme handling set the color for non-current dates
                int textColor = isDarkMode() ?
                        getResources().getColor(android.R.color.white) :
                        getResources().getColor(android.R.color.black);
                dateTexts[i].setTextColor(textColor);
            }

            dateTexts[i].setOnClickListener(v -> onDateClicked(day));
        }

        adapter.setDisplayedWeekDates(displayedWeekDates);
        adapter.notifyDataSetChanged();
    }

    private boolean isToday(Calendar day) {
        Calendar today = Calendar.getInstance();
        return (today.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR));
    }

    private void onDateClicked(Calendar selectedDate) {
        adapter.setSelectedDate(selectedDate);
    }

    private void navigateToWeek(int offset) {
        currentWeekOffset += offset;
        currentCalendar.add(Calendar.WEEK_OF_YEAR, offset);
        updateCalendarDisplay();
        fetchMeetingsFromFirebase();
    }

    private void onDayHeaderClicked(Calendar selectedDate) {
        adapter.setSelectedDate(selectedDate);
        updateCalendarDisplay();
    }

    private void fetchMeetingsFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("meetings");

        database.orderByChild("date").startAt(getStartOfWeek()).endAt(getEndOfWeek())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        meetings.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Meeting meeting = dataSnapshot.getValue(Meeting.class);
                            if (meeting != null) {
                                meetings.add(meeting);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to fetch meetings.");
                    }
                });
    }

    private String getStartOfWeek() {
        Calendar startOfWeek = (Calendar) currentCalendar.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(startOfWeek.getTime());
    }

    private String getEndOfWeek() {
        Calendar endOfWeek = (Calendar) currentCalendar.clone();
        endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(endOfWeek.getTime());
    }
}