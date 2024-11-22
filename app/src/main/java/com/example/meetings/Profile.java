package com.example.meetings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextRollNumber, editTextSapId, editTextBranch;
    private Button saveChangesButton;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the views
        editTextName = findViewById(R.id.editTextText);
        editTextPhone = findViewById(R.id.editTextText1);
        editTextRollNumber = findViewById(R.id.editTextText2);
        editTextSapId = findViewById(R.id.editTextText3);
        editTextBranch = findViewById(R.id.editTextText4);
        saveChangesButton = findViewById(R.id.saveChangesButton);

        // Fetch the user data
        String authUid = getIntent().getStringExtra("USER_ID");
        if (authUid != null) {
            fetchUserData(authUid);  // Fetch user data using Auth UID
        } else {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
        }

        // Save changes on button click
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData(authUid); // Pass the authUid for saving
            }
        });
    }

    private void fetchUserData(String authUid) {
        // Get the reference to the user's data in Firebase
        userRef = FirebaseDatabase.getInstance().getReference("users").child(authUid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Fill the EditText fields with the retrieved user data
                        editTextName.setText(user.getName());
                        editTextPhone.setText(user.getPhone());
                        editTextRollNumber.setText(user.getRollNumber());
                        editTextSapId.setText(user.getSapId());
                        editTextBranch.setText(user.getBranch());
                    }
                } else {
                    Toast.makeText(Profile.this, "User data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String authUid) {
        // Ensure user data is entered in all fields
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String rollNumber = editTextRollNumber.getText().toString().trim();
        String sapId = editTextSapId.getText().toString().trim();
        String branch = editTextBranch.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(rollNumber) || TextUtils.isEmpty(sapId) || TextUtils.isEmpty(branch)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Now that userRef is initialized in fetchUserData(), we can safely update the user's data
        userRef.child("name").setValue(name);
        userRef.child("phone").setValue(phone);
        userRef.child("rollNumber").setValue(rollNumber);
        userRef.child("sapId").setValue(sapId);
        userRef.child("branch").setValue(branch)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Data updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
