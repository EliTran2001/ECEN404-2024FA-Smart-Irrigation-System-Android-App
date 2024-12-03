package com.example.ecen403.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecen403.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String username = signupUsername.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                if (!isValidName(name)) {
                    Toast.makeText(SignupActivity.this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(SignupActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() > 15) {
                    Toast.makeText(SignupActivity.this, "Password cannot be longer than 15 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 5) {
                    Toast.makeText(SignupActivity.this, "Password must be longer than 5 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.matches(".*[A-Z].*")) {
                    Toast.makeText(SignupActivity.this, "Password must contain at least one uppercase letter.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.matches(".*\\d.*")) {
                    Toast.makeText(SignupActivity.this, "Password must contain at least one number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                HelperClass helperClass = new HelperClass(name, email, username, password);
                reference.child(username).setValue(helperClass);

                Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Validates the name input.
     * Allows only letters and spaces.
     *
     * @param name The name input to validate.
     * @return True if the name is valid, otherwise false.
     */
    private boolean isValidName(String name) {
        // Ensure only letters, spaces, and hyphens
        if (!name.matches("^[A-Za-z\\s'-]+$")) {
            return false;
        }
        if (name.isEmpty()) {
            return false;
        }
        if (name.length() < 2 || name.length() > 30) {
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.endsWith(".com");
    }

}