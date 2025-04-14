package com.example.meetings;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;

    private TextInputEditText editTextTitle, editTextDate, editTextTime, editTextDescription;
    private AutoCompleteTextView spinnerMeetingType;
    private MaterialButton buttonAddMeeting;
    private Calendar calendar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Meeting meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        buttonMenu = findViewById(R.id.buttonmenu);
        buttonMenu1 = findViewById(R.id.buttonmenu1);
        navigationView = findViewById(R.id.navigationView);

        setupDrawer();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerMeetingType = findViewById(R.id.spinnerMeetingType);
        buttonAddMeeting = findViewById(R.id.buttonAddMeeting);
        calendar = Calendar.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Meeting");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meeting_types, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMeetingType.setAdapter(adapter);

        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker(editTextTime));

        buttonAddMeeting.setOnClickListener(view -> {
            String title = editTextTitle.getText().toString();
            String date = editTextDate.getText().toString();
            String time = editTextTime.getText().toString();
            String description = editTextDescription.getText().toString();
            String type = spinnerMeetingType.getText().toString();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time) ||
                    TextUtils.isEmpty(description) || TextUtils.isEmpty(type)) {
                Toast.makeText(MainActivity.this, "Enter all required fields", Toast.LENGTH_SHORT).show();
            } else {
                addDatatoDatabase(title, date, time, description, type);
            }
        });

        saveUserTokenToDatabase();
    }

    private void addDatatoDatabase(String title, String date, String time, String description, String type) {
        String scheduledBy = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        meeting = new Meeting(title, date, time, type, description, scheduledBy);

        String meetingID = databaseReference.push().getKey();
        if (meetingID != null) {
            databaseReference.child(meetingID).setValue(meeting).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Meeting Scheduled Successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                    sendNotificationToAllUsers(meeting);
                    startActivity(new Intent(MainActivity.this, Dashboard.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to schedule meeting", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendNotificationToAllUsers(Meeting meeting) {
        String notificationTitle = "New Meeting Scheduled: " + meeting.getTitle();
        String notificationBody = "Scheduled By: " + meeting.getScheduledBy() +
                "\nDate: " + meeting.getDate() +
                "\nTime: " + meeting.getTime();

        DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("UserTokens");
        tokensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot tokenSnapshot : task.getResult().getChildren()) {
                    String userToken = tokenSnapshot.getValue(String.class);
                    if (userToken != null) {
                        FCMNotificationHelper.sendFCMNotification(this, userToken, notificationTitle, notificationBody);
                    }
                }
            }
        });
    }

    private void saveUserTokenToDatabase() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("UserTokens");
                            tokensRef.child(userID).setValue(token);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FCM", "Failed to get FCM token", e));
    }

    private void clearForm() {
        editTextTitle.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
        editTextDescription.setText("");
        spinnerMeetingType.setText("", false);
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeInView(TextInputEditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        editText.setText(sdf.format(calendar.getTime()));
    }

    private void setupDrawer() {
        buttonMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        buttonMenu1.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.settings) {
                    startActivity(new Intent(MainActivity.this, Profile.class));
                } else if (item.getItemId() == R.id.aboutus) {
                    startActivity(new Intent(MainActivity.this, Aboutus.class));
                } else if (item.getItemId() == R.id.logout) {
                    Intent logout = new Intent(MainActivity.this, Login.class);
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
                startActivity(new Intent(MainActivity.this, Profile.class));
            } else if (itemId == R.id.navCalendar) {
                startActivity(new Intent(MainActivity.this, WeeklyCalendarActivity.class));
            } else if (itemId == R.id.navWorkLogs) {
                startActivity(new Intent(MainActivity.this, Worklogdata.class));
            } else if (itemId == R.id.navAddLogs) {
                startActivity(new Intent(MainActivity.this, Add_WorkLog.class));
            } else if (itemId == R.id.navFlightlogs) {
                startActivity(new Intent(MainActivity.this, FlightsLogData.class));
            } else if (itemId == R.id.navAddFlight) {
                startActivity(new Intent(MainActivity.this, Add_Flightlog.class));
            } else if (itemId == R.id.navAddMeetings) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            } else if (itemId == R.id.navMeetinglogs) {
                startActivity(new Intent(MainActivity.this, Meetinglogs.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}
