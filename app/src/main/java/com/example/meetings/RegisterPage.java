package com.example.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPage extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword,editTextPassword1;
    Button signUp;
    TextView signIn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPassword1 = findViewById(R.id.cnfpassword);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(RegisterPage.this, Login.class);
                startActivity(in);
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, repassword;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                repassword = String.valueOf(editTextPassword1.getText());

                // Check for empty fields
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterPage.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterPage.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(repassword)) {
                    Toast.makeText(RegisterPage.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Password validation
                if (!password.matches("^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=.*[0-9])(?=.*[a-z]).{8,}$")) {
                    Toast.makeText(RegisterPage.this, "Password must be 8+ characters, include 1 uppercase, 1 special symbol, and 1 number", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!password.equals(repassword)) {
                    Toast.makeText(RegisterPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase registration
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    Intent in = new Intent(RegisterPage.this, ProfileActivity.class);
                                    startActivity(in);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterPage.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}