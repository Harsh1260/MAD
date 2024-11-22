package com.example.meetings;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Add_Flightlog extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;

    private TextInputEditText editTextDate, editTextStartTime, editTextEndTime, editTextFlightLog, editTextPilotName;
    private AutoCompleteTextView autoCompleteDrone;
    private Button buttonSubmit;
    private Calendar calendar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Flightlog flightlog;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_flightlog);  // Change this if your XML file name is different

            // Initialize views

            editTextDate = findViewById(R.id.editTextDate);
            editTextStartTime = findViewById(R.id.editTextStartTime);
            editTextEndTime = findViewById(R.id.editTextEndTime);
            editTextFlightLog = findViewById(R.id.editTextFlightLog);
            editTextPilotName = findViewById(R.id.editTextPilotName);
            autoCompleteDrone = findViewById(R.id.autoCompleteDrone);
            buttonSubmit = findViewById(R.id.buttonSubmit);
            calendar = Calendar.getInstance();

            drawerLayout = findViewById(R.id.drawerLayout);
            buttonMenu = findViewById(R.id.buttonmenu);
            buttonMenu1 = findViewById(R.id.buttonmenu1);
            navigationView = findViewById(R.id.navigationView);

            setupDrawer();

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.drone, R.layout.spinner_item);
            autoCompleteDrone.setAdapter(adapter);

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference= firebaseDatabase.getReference("Flightlog");
            flightlog = new Flightlog();

            editTextDate.setOnClickListener(v -> showDatePicker());
            editTextStartTime.setOnClickListener(v -> showTimePicker(editTextStartTime));
            editTextEndTime.setOnClickListener(v -> showTimePicker(editTextEndTime));

            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String date = editTextDate.getText().toString();
                    String time1 = editTextStartTime.getText().toString();
                    String time2 = editTextEndTime.getText().toString();
                    String description = editTextFlightLog.getText().toString();
                    String name = editTextPilotName.getText().toString();
                    String drone = autoCompleteDrone.getText().toString();

                    editTextDate.setText("");
                    editTextStartTime.setText("");
                    editTextEndTime.setText("");
                    editTextFlightLog.setText("");
                    editTextPilotName.setText("");


                    if (TextUtils.isEmpty(date)) {
                        Toast.makeText(Add_Flightlog.this, "Please enter the flight date", Toast.LENGTH_SHORT).show();
                        editTextDate.setError("Required");
                        editTextDate.requestFocus();
                    } else if (TextUtils.isEmpty(time1)) {
                        Toast.makeText(Add_Flightlog.this, "Please enter the start time", Toast.LENGTH_SHORT).show();
                        editTextStartTime.setError("Required");
                        editTextStartTime.requestFocus();
                    } else if (TextUtils.isEmpty(time2)) {
                        Toast.makeText(Add_Flightlog.this, "Please enter the end time", Toast.LENGTH_SHORT).show();
                        editTextEndTime.setError("Required");
                        editTextEndTime.requestFocus();
                    } else if (TextUtils.isEmpty(description)) {
                        Toast.makeText(Add_Flightlog.this, "Please enter the flight log description", Toast.LENGTH_SHORT).show();
                        editTextFlightLog.setError("Required");
                        editTextFlightLog.requestFocus();
                    } else if (TextUtils.isEmpty(name)) {
                        Toast.makeText(Add_Flightlog.this, "Please enter the pilot name", Toast.LENGTH_SHORT).show();
                        editTextPilotName.setError("Required");
                        editTextPilotName.requestFocus();
                    } else if (TextUtils.isEmpty(drone)) {
                        Toast.makeText(Add_Flightlog.this, "Please select a drone", Toast.LENGTH_SHORT).show();
                        autoCompleteDrone.setError("Required");
                        autoCompleteDrone.requestFocus();
                    } else {
                        addDatatoDatabase(date, time1, time2, description, name, drone);
                    }

                }
            });
        }

    private void setupDrawer() {
        buttonMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle popup menu button for settings/logout
        buttonMenu1.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(Add_Flightlog.this, view);
            popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.settings) {
                    String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the Auth UID
                    Intent settings = new Intent(Add_Flightlog.this, Profile.class);
                    settings.putExtra("USER_ID", authUid);  // Pass Auth UID to Profile
                    startActivity(settings);
                } else if (item.getItemId() == R.id.aboutus) {
                    Intent aboutus = new Intent(Add_Flightlog.this,Aboutus.class);
                    startActivity(aboutus);
                } else if (item.getItemId() == R.id.logout) {
                    Intent logout = new Intent(Add_Flightlog.this, Login.class);
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
                Intent dashboard = new Intent(Add_Flightlog.this, Dashboard.class);
                startActivity(dashboard);
            } else if (itemId == R.id.navCalendar) {
                Intent calendar= new Intent(Add_Flightlog.this, WeeklyCalendarActivity.class);
                startActivity(calendar);
            } else if (itemId == R.id.navWorkLogs) {
                Intent WorkLogData = new Intent(Add_Flightlog.this, Worklogdata.class);
                startActivity(WorkLogData);
            } else if (itemId == R.id.navAddLogs) {
                Intent addWorkLog = new Intent(Add_Flightlog.this, Add_WorkLog.class);
                startActivity(addWorkLog);
            } else if (itemId == R.id.navFlightlogs) {
                Intent FlightLog = new Intent(Add_Flightlog.this, FlightsLogData.class);
                startActivity(FlightLog);
            } else if (itemId == R.id.navAddFlight) {
                Intent addFlightLog = new Intent(Add_Flightlog.this, Add_Flightlog.class);
                startActivity(addFlightLog);
            } else if (itemId == R.id.navAddMeetings) {
                Intent addMeeting = new Intent(Add_Flightlog.this, MainActivity.class);
                startActivity(addMeeting);
            } else if (itemId == R.id.navMeetinglogs) {
                Intent meetingLogs = new Intent(Add_Flightlog.this, Meetinglogs.class);
                startActivity(meetingLogs);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void addDatatoDatabase(String date, String time1, String time2, String description, String name, String drone) {
        flightlog.setDate(date);
        flightlog.setStarttime(time1);
        flightlog.setEndtime(time2);
        flightlog.setDescription(description);
        flightlog.setName(name);
        flightlog.setDrone(drone);

        // Use push() to generate a unique key and setValue() to write the data
        databaseReference.push().setValue(flightlog).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Add_Flightlog.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Add_Flightlog.this, "Data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(java.util.Calendar.YEAR, year);
                    calendar.set(java.util.Calendar.MONTH, month);
                    calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateInView();
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
        );

        // Set the minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void showTimePicker(final TextInputEditText editText) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(java.util.Calendar.MINUTE, minute);
                    updateTimeInView(editText);
                },
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE),
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