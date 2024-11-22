package com.example.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, rollEditText, sapIdEditText;
    private RadioGroup branchRadioGroup;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata);

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        rollEditText = findViewById(R.id.rollEditText);
        sapIdEditText = findViewById(R.id.sapIdEditText);
        branchRadioGroup = findViewById(R.id.branchRadioGroup);
        submitButton = findViewById(R.id.submitButton);

        // Initialize Firebase Realtime Database reference
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String authUid = currentUser.getUid(); // Get Auth UID
                    saveUserData(authUid); // Pass Auth UID to the save method
                } else {
                    Toast.makeText(ProfileActivity.this, "User not authenticated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserData(String authUid) {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String rollNumber = rollEditText.getText().toString().trim();
        String sapId = sapIdEditText.getText().toString().trim();
        int selectedBranchId = branchRadioGroup.getCheckedRadioButtonId();

        // Input validation
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            phoneEditText.setError("Enter a valid 10-digit phone number");
            phoneEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(rollNumber)) {
            rollEditText.setError("Roll Number is required");
            rollEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(sapId) || sapId.length() != 11) {
            sapIdEditText.setError("Enter a valid 11-digit SAP ID");
            sapIdEditText.requestFocus();
            return;
        }

        if (selectedBranchId == -1) {
            Toast.makeText(this, "Please select a branch", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected branch text
        RadioButton selectedBranch = findViewById(selectedBranchId);
        String branch = selectedBranch.getText().toString();

        // Use authUid as the key for the user data
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(authUid);

        User user = new User(name, phone, rollNumber, sapId, branch); // Create user object

        // Save data using the Firebase Authentication UID as the key
        userRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                // Navigate to Dashboard or any other screen
                Intent intent = new Intent(ProfileActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}