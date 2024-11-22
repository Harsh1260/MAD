package com.example.meetings;

public class User {
    public String name;
    public String phone;
    public String rollNumber;
    public String sapId;
    public String branch;

    // No-argument constructor for Firebase
    public User() {}

    // Constructor
    public User(String name, String phone, String rollNumber, String sapId, String branch) {
        this.name = name;
        this.phone = phone;
        this.rollNumber = rollNumber;
        this.sapId = sapId;
        this.branch = branch;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getSapId() {
        return sapId;
    }

    public String getBranch() {
        return branch;
    }
}


