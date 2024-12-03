package com.example.ecen403.ZoneDataActivites;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecen403.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Zone8 extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String rtvTemp1, rtvMoisture1, rtvPrecipitation1, rtvStatus1, rtvHumidity1, zone8;
    TextView txtTemp1, txtMoisture1, txtPrecipitation1, txtStatus1, txtHumidity1;
    Switch statusSwitch;
    boolean userInteracting = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_zone8);

        txtTemp1 = findViewById(R.id.rtvTemp1);
        txtMoisture1 = findViewById(R.id.rtvMoisture1);
        txtHumidity1 = findViewById(R.id.rtvHumidity1);
        txtPrecipitation1 = findViewById(R.id.rtvPrecipitation1);
        txtStatus1 = findViewById(R.id.rtvStatus1);
        statusSwitch = findViewById(R.id.statusSwitch);

        zone8 = "zone8";

        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (userInteracting) { // Only update Firestore if the user interacted
                    updateStatusInFirestore(isChecked);
                }
            }
        });

        db.collection("zones")
                .document(zone8)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot != null && documentSnapshot.exists()) {
                                String rtvTemp1 = documentSnapshot.getString("temp");
                                txtTemp1.setText(rtvTemp1);
                                String rtvMoisture1 = documentSnapshot.getString("moisture");
                                txtMoisture1.setText(rtvMoisture1);
                                String rtvHumidity1 = documentSnapshot.getString("humidity");
                                txtHumidity1.setText(rtvHumidity1);
                                String rtvPrecipitation1 = documentSnapshot.getString("precipitation");
                                txtPrecipitation1.setText(rtvPrecipitation1);
                                String rtvStatus1 = documentSnapshot.getString("status");
                                txtStatus1.setText(rtvStatus1);

                                userInteracting = false; // Set the flag to false before setting the state
                                if ("active".equals(rtvStatus1) || "on".equals(rtvStatus1)) {
                                    statusSwitch.setChecked(true);
                                } else {
                                    statusSwitch.setChecked(false);
                                }
                                userInteracting = true;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Zone8.this, "Error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and return to the previous one
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateStatusInFirestore(boolean status) {
        db.collection("zones")
                .document(zone8)
                .update("status", status ? "on" : "off")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Zone8.this, "Status updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Zone8.this, "Error updating status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}