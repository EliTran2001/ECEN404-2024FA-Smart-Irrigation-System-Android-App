package com.example.ecen403.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecen403.R;

public class ProfilePage extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userUsernameTextView;
    private ImageButton backButton; // Declare your back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize TextViews
        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userUsernameTextView = findViewById(R.id.userUsernameTextView);

        // Initialize the back button
        backButton = findViewById(R.id.back_button);

        // Set an OnClickListener on the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, Home.class);
                startActivity(intent);
                // Finish the current activity and return to the previous one
                finish();
            }
        });

        // Get user data from the User class
        String name = User.getName();
        String email = User.getEmail();
        String username = User.getUsername();

        // Display the user data in TextViews
        userNameTextView.setText("Name: " + (name != null ? name : "Not available"));
        userEmailTextView.setText("Email: " + (email != null ? email : "Not available"));
        userUsernameTextView.setText("Username: " + (username != null ? username : "Not available"));
    }
}
