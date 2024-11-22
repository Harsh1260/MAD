package com.example.meetings;

public class Meeting {
    private String title;
    private String date;
    private String time;
    private String meetingType;
    private String description;
    private String scheduledBy; // New field to track who scheduled the meeting

    // Required empty constructor for Firebase
    public Meeting() {
    }

    // Constructor with parameters
    public Meeting(String title, String date, String time, String meetingType, String description, String scheduledBy) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.meetingType = meetingType;
        this.description = description;
        this.scheduledBy = scheduledBy;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScheduledBy() {
        return scheduledBy;
    }

    public void setScheduledBy(String scheduledBy) {
        this.scheduledBy = scheduledBy;
    }
}
