package com.example.meetings;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeeklyCalendarAdapter extends RecyclerView.Adapter<WeeklyCalendarAdapter.ViewHolder> {
    private List<Meeting> meetings;
    private Context context;
    private Calendar[] displayedWeekDates;
    private Calendar selectedDate;

    public WeeklyCalendarAdapter(Context context, List<Meeting> meetings, Calendar[] displayedWeekDates) {
        this.context = context;
        this.meetings = meetings;
        this.displayedWeekDates = displayedWeekDates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day_meetings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar day = displayedWeekDates[position];
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE d", Locale.getDefault());
        String formattedDate = dayFormat.format(day.getTime());
        holder.dateAndWeekday.setText(formattedDate);

        // Set text color based on theme
        int textColor = isDarkMode() ?
                context.getResources().getColor(android.R.color.white) :
                context.getResources().getColor(android.R.color.black);

        holder.dateAndWeekday.setTextColor(textColor);
        holder.meetingDetails.setTextColor(textColor);

        StringBuilder meetingInfo = new StringBuilder();

        // Only display meetings for the selected date
        if (selectedDate != null && selectedDate.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)) {
            for (Meeting meeting : meetings) {
                if (meeting.getDate().equals(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(day.getTime()))) {
                    meetingInfo.append("Title: ").append(meeting.getTitle())
                            .append("\nDate: ").append(meeting.getDate())
                            .append("\nTime: ").append(meeting.getTime())
                            .append("\n\n");
                }
            }
        }

        holder.meetingDetails.setText(meetingInfo.toString().isEmpty() ? "No Meetings" : meetingInfo.toString());
    }

    private boolean isDarkMode() {
        return (context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public void clearMeetingDetails() {
        meetings.clear();
    }

    public void addMeetingForSelectedDate(String title, String date, String time) {
        Meeting meeting = new Meeting(title, date, time, null, null,null);
        meetings.add(meeting);
    }

    @Override
    public int getItemCount() {
        return displayedWeekDates.length;
    }

    public void setDisplayedWeekDates(Calendar[] displayedWeekDates) {
        this.displayedWeekDates = displayedWeekDates;
        notifyDataSetChanged();
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateAndWeekday;
        TextView meetingDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateAndWeekday = itemView.findViewById(R.id.date_and_weekday);
            meetingDetails = itemView.findViewById(R.id.meeting_details);
        }
    }
}