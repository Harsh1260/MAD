package com.example.meetings;

public class Flightlog {
    private String name;
    private String date;
    private String starttime;
    private String endtime;
    private String drone;
    private String description;

    // Required empty constructor for Firebase
    public Flightlog() {}

    public Flightlog(String name, String date, String starttime, String endtime, String drone, String description) {
        this.name = name;
        this.date = date;
        this.starttime = starttime;
        this.endtime = endtime;
        this.drone = drone;
        this.description = description;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStarttime() { return starttime; }
    public void setStarttime(String starttime) { this.starttime = starttime; }

    public String getEndtime() { return endtime; }
    public void setEndtime(String endtime) { this.endtime = endtime; }

    public String getDrone() { return drone; }
    public void setDrone(String drone) { this.drone = drone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}