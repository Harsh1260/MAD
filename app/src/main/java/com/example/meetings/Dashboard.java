package com.example.meetings;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private ImageButton buttonMenu, buttonMenu1;
    private NavigationView navigationView;
    private BarChart barChart, barChart1;
    private MaterialCardView addWorkCard, addFlightCard, addMeetingCard;
    private String authUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupDrawer();
        setupCharts();
        setupQuickActionCards();
        authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        buttonMenu = findViewById(R.id.buttonmenu);
        buttonMenu1 = findViewById(R.id.buttonmenu1);
        navigationView = findViewById(R.id.navigationView);
        barChart = findViewById(R.id.barChart);
        barChart1 = findViewById(R.id.barChart1);

        addWorkCard = findViewById(R.id.cardView3);
        addFlightCard = findViewById(R.id.cardView4);
        addMeetingCard = findViewById(R.id.cardView5);
    }

    private void setupQuickActionCards() {
        addWorkCard.setOnClickListener(this);
        addFlightCard.setOnClickListener(this);
        addMeetingCard.setOnClickListener(this);

        addWorkCard.setClickable(true);
        addFlightCard.setClickable(true);
        addMeetingCard.setClickable(true);

        setupCardTouchListener(addWorkCard);
        setupCardTouchListener(addFlightCard);
        setupCardTouchListener(addMeetingCard);
    }

    private void setupCardTouchListener(final MaterialCardView card) {
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    card.setCardElevation(8f);
                    card.startAnimation(AnimationUtils.loadAnimation(this, R.anim.card_press));
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    card.setCardElevation(2f);
                    card.startAnimation(AnimationUtils.loadAnimation(this, R.anim.card_release));
                    break;
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        final Intent intent;
        final String message;

        if (v.getId() == R.id.cardView3) {
            intent = new Intent(Dashboard.this, Add_WorkLog.class);
            message = "Opening Add Work Log";
        } else if (v.getId() == R.id.cardView4) {
            intent = new Intent(Dashboard.this, Add_Flightlog.class);
            message = "Opening Add Flight Log";
        } else if (v.getId() == R.id.cardView5) {
            intent = new Intent(Dashboard.this, MainActivity.class);
            message = "Opening Add Meeting";
        } else {
            return;
        }

        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.card_click));

        v.postDelayed(() -> {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }, 200);
    }

    private void setupDrawer() {
        buttonMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        buttonMenu1.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(Dashboard.this, view);
            popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.settings) {
                    String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the Auth UID
                    Intent settings = new Intent(Dashboard.this, Profile.class);
                    settings.putExtra("USER_ID", authUid);  // Pass Auth UID to Profile
                    startActivity(settings);
                } else if (item.getItemId() == R.id.aboutus) {
                    Intent aboutus = new Intent(Dashboard.this,Aboutus.class);
                    startActivity(aboutus);
                } else if (item.getItemId() == R.id.logout) {
                    Intent logout = new Intent(Dashboard.this, Login.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logout);
                    finish();
                }
                return false;
            });

            popup.show();
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            final Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.navDashboard) {
                intent = new Intent(Dashboard.this, Dashboard.class);
            } else if (itemId == R.id.navCalendar) {
                intent = new Intent(Dashboard.this, WeeklyCalendarActivity.class);
            } else if (itemId == R.id.navWorkLogs) {
                intent = new Intent(Dashboard.this, Worklogdata.class);
            } else if (itemId == R.id.navAddLogs) {
                intent = new Intent(Dashboard.this, Add_WorkLog.class);
            } else if (itemId == R.id.navFlightlogs) {
                intent = new Intent(Dashboard.this, FlightsLogData.class);
            } else if (itemId == R.id.navAddFlight) {
                intent = new Intent(Dashboard.this, Add_Flightlog.class);
            } else if (itemId == R.id.navAddMeetings) {
                intent = new Intent(Dashboard.this, MainActivity.class);
            } else if (itemId == R.id.navMeetinglogs) {
                intent = new Intent(Dashboard.this, Meetinglogs.class);
            } else {
                return false;
            }

            intent.putExtra("USER_ID", authUid);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupCharts() {
        setupBarChart(barChart, generateDummyData(), "Flights");
        setupBarChart(barChart1, generateWorkData(), "Work");
    }

    private void setupBarChart(BarChart chart, List<BarEntry> entries, String label) {
        BarDataSet dataSet = new BarDataSet(entries, label);

        if (label.equals("Flights")) {
            dataSet.setColor(Color.parseColor("#1976D2"));
        } else {
            dataSet.setColor(Color.parseColor("#2E7D32"));
        }

        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int hour = (int) value;
                return String.format("%02d:00", hour);
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setTextSize(12f);
        leftAxis.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);
        chart.setData(barData);
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setExtraOffsets(10f, 10f, 10f, 10f);
        chart.animateY(1000);
        chart.invalidate();
    }

    private List<BarEntry> generateDummyData() {
        List<BarEntry> entries = new ArrayList<>();
        float[] hourlyValues = {
                300, 250, 550, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                550, 550, 1100, 1300, 500,
                650, 650, 900, 200, 500,
                50, 50
        };

        for (int i = 0; i < hourlyValues.length; i++) {
            entries.add(new BarEntry(i, hourlyValues[i]));
        }
        return entries;
    }

    private List<BarEntry> generateWorkData() {
        List<BarEntry> entries = new ArrayList<>();
        float[] hourlyValues = {
                300, 250, 550, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                550, 550, 1100, 1300, 500,
                650, 650, 900, 200, 500,
                50, 50
        };

        for (int i = 0; i < hourlyValues.length; i++) {
            entries.add(new BarEntry(i, hourlyValues[i]));
        }
        return entries;
    }
}