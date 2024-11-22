package com.example.meetings;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Add_WorkLog extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;

    private TextInputEditText editTextDate, editTextStartTime, editTextEndTime, editTextTaskDescription, editTextDeveloperName;
    private Button buttonSubmit;
    private Calendar calendar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Worklog worklog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_log);

        drawerLayout = findViewById(R.id.drawerLayout);
        buttonMenu = findViewById(R.id.buttonmenu);
        buttonMenu1 = findViewById(R.id.buttonmenu1);
        navigationView = findViewById(R.id.navigationView);

        setupDrawer();

        // Initialize UI components
        editTextDate = findViewById(R.id.editTextDate);
        editTextStartTime = findViewById(R.id.editTextStartTime);
        editTextEndTime = findViewById(R.id.editTextEndTime);
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);
        editTextDeveloperName = findViewById(R.id.editTextDeveloperName);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        calendar = Calendar.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("Worklog");
        worklog = new Worklog();

        // Set up click listeners
        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextStartTime.setOnClickListener(v -> showTimePicker(editTextStartTime));
        editTextEndTime.setOnClickListener(v -> showTimePicker(editTextEndTime));

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = editTextDate.getText().toString();
                String time1 = editTextStartTime.getText().toString();
                String time2 = editTextEndTime.getText().toString();
                String description = editTextTaskDescription.getText().toString();
                String name = editTextDeveloperName.getText().toString();

                editTextDate.setText("");
                editTextStartTime.setText("");
                editTextEndTime.setText("");
                editTextTaskDescription.setText("");
                editTextDeveloperName.setText("");


                if (TextUtils.isEmpty(date)) {
                    Toast.makeText(Add_WorkLog.this, "Please enter the flight date", Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Required");
                    editTextDate.requestFocus(); // Set focus to the empty field
                } else if (TextUtils.isEmpty(time1)) {
                    Toast.makeText(Add_WorkLog.this, "Please enter the start time", Toast.LENGTH_SHORT).show();
                    editTextStartTime.setError("Required");
                    editTextStartTime.requestFocus();
                } else if (TextUtils.isEmpty(time2)) {
                    Toast.makeText(Add_WorkLog.this, "Please enter the end time", Toast.LENGTH_SHORT).show();
                    editTextEndTime.setError("Required");
                    editTextEndTime.requestFocus();
                } else if (TextUtils.isEmpty(description)) {
                    Toast.makeText(Add_WorkLog.this, "Please enter the flight log description", Toast.LENGTH_SHORT).show();
                    editTextTaskDescription.setError("Required");
                    editTextTaskDescription.requestFocus();
                } else if (TextUtils.isEmpty(name)) {
                    Toast.makeText(Add_WorkLog.this, "Please enter the pilot name", Toast.LENGTH_SHORT).show();
                    editTextDeveloperName.setError("Required");
                    editTextDeveloperName.requestFocus();
                } else {
                    addDatatoDatabase(date, time1, time2, description, name);
                }
            }
        });
    }

    private void setupDrawer() {
        // Handle sidebar button to open the drawer
        buttonMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle popup menu button for settings/logout
        buttonMenu1.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(Add_WorkLog.this, view);
            popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.settings) {
                    String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the Auth UID
                    Intent settings = new Intent(Add_WorkLog.this, Profile.class);
                    settings.putExtra("USER_ID", authUid);  // Pass Auth UID to Profile
                    startActivity(settings);
                } else if (item.getItemId() == R.id.aboutus) {
                    Intent aboutus = new Intent(Add_WorkLog.this,Aboutus.class);
                    startActivity(aboutus);
                } else if (item.getItemId() == R.id.logout) {
                    Intent logout = new Intent(Add_WorkLog.this, Login.class);
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
                Intent dashboard = new Intent(Add_WorkLog.this, Dashboard.class);
                startActivity(dashboard);
            } else if (itemId == R.id.navCalendar) {
                Intent calendar = new Intent(Add_WorkLog.this, WeeklyCalendarActivity.class);
                startActivity(calendar);
            } else if (itemId == R.id.navWorkLogs) {
                Intent WorkLogData = new Intent(Add_WorkLog.this, Worklogdata.class);
                startActivity(WorkLogData);
            } else if (itemId == R.id.navAddLogs) {
                Intent addWorkLog = new Intent(Add_WorkLog.this, Add_WorkLog.class);
                startActivity(addWorkLog);
            } else if (itemId == R.id.navFlightlogs) {
                Intent FlightLog = new Intent(Add_WorkLog.this, FlightsLogData.class);
                startActivity(FlightLog);
            } else if (itemId == R.id.navAddFlight) {
                Intent addFlightLog = new Intent(Add_WorkLog.this, Add_Flightlog.class);
                startActivity(addFlightLog);
            } else if (itemId == R.id.navAddMeetings) {
                Intent addMeeting = new Intent(Add_WorkLog.this, MainActivity.class);
                startActivity(addMeeting);
            } else if (itemId == R.id.navMeetinglogs) {
                Intent meetingLogs = new Intent(Add_WorkLog.this, Meetinglogs.class);
                startActivity(meetingLogs);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void addDatatoDatabase(String date, String time1, String time2, String description, String name) {
        worklog.setDate(date);
        worklog.setStarttime(time1);
        worklog.setEndtime(time2);
        worklog.setDescription(description);
        worklog.setName(name);

        databaseReference.push().setValue(worklog).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Add_WorkLog.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Add_WorkLog.this, "Data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateInView();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set the minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void showTimePicker(final TextInputEditText editText) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    updateTimeInView(editText);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void updateDateInView() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeInView(TextInputEditText editText) {
        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(calendar.getTime()));
    }
}


