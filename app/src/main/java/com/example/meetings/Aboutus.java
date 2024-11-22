package com.example.meetings;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class Aboutus extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        findViewById(R.id.btn_linkedin_harsh).setOnClickListener(v -> openLinkedIn("www.linkedin.com/in/harsh-jain-b071b424a"));
        findViewById(R.id.btn_linkedin_kapil).setOnClickListener(v -> openLinkedIn("https://www.linkedin.com/in/kapil-agrawal-93069a284/"));
        findViewById(R.id.btn_linkedin_shubham).setOnClickListener(v -> openLinkedIn("https://www.linkedin.com/in/shubham-agarwal-880435251/"));
    }

    private void openLinkedIn(String url) {
        if (!url.startsWith("https://")) {
            url = "https://" + url;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}