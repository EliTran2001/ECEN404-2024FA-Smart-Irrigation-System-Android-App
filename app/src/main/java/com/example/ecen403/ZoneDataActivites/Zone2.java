package com.example.ecen403.ZoneDataActivites;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecen403.DatabasseHelpers.DatabaseHelper2;
import com.example.ecen403.DatabasseHelpers.RateSpeedDatabaseHelper2;
import com.example.ecen403.DatabasseHelpers.WaterAmountDatabaseHelper2;
import com.example.ecen403.R;
import com.example.ecen403.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Zone2 extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String rtvTemp1, rtvMoisture1, rtvPrecipitation1, rtvStatus1, rtvHumidity1, zone2, water_amount, time_active, rate_speed;
    TextView txtTemp1, txtMoisture1, txtPrecipitation1, txtStatus1, txtHumidity1, graphTitle, graphTitle2, graphTitle3;
    Switch statusSwitch;
    GraphView graph, waterAmountGraph, rateSpeedGraph;

    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>();
    List<Double> timeActiveValues = new ArrayList<>();
    List<Double> waterAmountValues = new ArrayList<>();
    List<Double> rateSpeedValues = new ArrayList<>();
    List<Timestamp> dateValues = new ArrayList<>(); // Declare dateValues list

    boolean userInteracting = false;
    SwipeRefreshLayout swipeRefreshLayout; // Declare SwipeRefreshLayout
    private DatabaseHelper2 databaseHelper2;
    private RateSpeedDatabaseHelper2 rateSpeedDatabaseHelper2;
    private WaterAmountDatabaseHelper2 waterAmountDatabaseHelper2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_zone2);

        databaseHelper2 = new DatabaseHelper2(this); // Initialize your DatabaseHelper
        rateSpeedDatabaseHelper2 = new RateSpeedDatabaseHelper2(this);
        waterAmountDatabaseHelper2 = new WaterAmountDatabaseHelper2(this);

        txtTemp1 = findViewById(R.id.rtvTemp1);
        txtMoisture1 = findViewById(R.id.rtvMoisture1);
        txtHumidity1 = findViewById(R.id.rtvHumidity1);
        txtPrecipitation1 = findViewById(R.id.rtvPrecipitation1);
        txtStatus1 = findViewById(R.id.rtvStatus1);
        statusSwitch = findViewById(R.id.statusSwitch);

        graph = findViewById(R.id.graph);
        waterAmountGraph = findViewById(R.id.graph_water_amount);
        rateSpeedGraph = findViewById(R.id.graph_rate_speed);

        graphTitle = findViewById(R.id.graphTitle);
        graphTitle2 = findViewById(R.id.graphTitle2);
        graphTitle3 = findViewById(R.id.graphTitle3);

        graphTitle.setText("Time Active Graph:");
        graphTitle2.setText("Water Amount Graph:");
        graphTitle3.setText("Rate of Speed for Water Dispensed:");

        zone2 = "zone2";
        water_amount = "water_amount";
        time_active = "time_active";
        rate_speed = "rate_speed";

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout2); // Ensure this ID matches your layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(); // Call method to load data
                setupGraph();
                setupWaterAmountGraph();
                setupRateSpeedGraph();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (userInteracting) { // Only update Firestore if the user interacted
                    updateStatusInFirestore(isChecked);
                }
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

        loadData();
        loadDataFromDatabase();
        loadDataFromWaterAmountDatabase();
        loadDataFromRateSpeedDatabase();

    }

    private void updateStatusInFirestore(boolean status) {
        db.collection("zones")
                .document(zone2)
                .update("status", status ? "on" : "off")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Zone2.this, "Status updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Zone2.this, "Error updating status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadData() {

        db.collection("zones")
                .document(zone2)
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
                        swipeRefreshLayout.setRefreshing(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Zone2.this, "Error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        db.collection("zones")
                .document(zone2)
                .collection("datapoints2")
                .document("stored2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()) {
                                // Get the current time_active and previous_value from Firestore
                                String timeActiveStr = documentSnapshot.getString("time_active");
                                String waterAmountStr = documentSnapshot.getString("water_amount");
                                String rateSpeedStr = documentSnapshot.getString("rate_speed");
                                String previousWaterAmountStr = documentSnapshot.getString("previous_water");
                                String previousValueStr = documentSnapshot.getString("previous_value");
                                String previousRateSpeedStr = documentSnapshot.getString("previous_rate");
                                Timestamp dateTimestamp = documentSnapshot.getTimestamp("date");

                                if (timeActiveStr != null && previousValueStr != null && waterAmountStr != null && previousWaterAmountStr != null && rateSpeedStr != null && previousRateSpeedStr != null) {
                                    try {

                                        double timeActive = Double.parseDouble(timeActiveStr); // Convert time_active to double
                                        double dateInMillis = dateTimestamp.toDate().getTime(); // Get the time in milliseconds from the timestamp
                                        double previousValue = Double.parseDouble(previousValueStr);
                                        double waterAmount = Double.parseDouble(waterAmountStr);
                                        double previousWaterAmount = Double.parseDouble(previousWaterAmountStr);
                                        double rateSpeed = Double.parseDouble(rateSpeedStr);
                                        double previousRateSpeed = Double.parseDouble(previousRateSpeedStr);

                                        // Add data point using date as X and time_active as Y
                                        timeActiveValues.add(timeActive);
                                        waterAmountValues.add(waterAmount);
                                        rateSpeedValues.add(rateSpeed);
                                        appendDataToGraph(dateInMillis, timeActive);
                                        appendDataToWaterGraph(dateInMillis, waterAmount);
                                        appendDataToRateSpeedGraph(dateInMillis, rateSpeed);

                                        series.setThickness(5);
                                        series2.setThickness(5);
                                        series3.setThickness(5);

                                        databaseHelper2.insertData(dateInMillis, timeActive);
                                        rateSpeedDatabaseHelper2.insertData(dateInMillis, rateSpeed);
                                        waterAmountDatabaseHelper2.insertData(dateInMillis, waterAmount);

                                        if (timeActive != previousValue) {
                                            // Values are different, so update the date and previous_value
                                            updateDateAndPreviousValue("stored2", timeActive);
                                        } else {
                                            // Values are the same, no update needed
                                            Log.d("Firestore Check", "No update needed, values are the same.");
                                        }

                                        if (waterAmount != previousWaterAmount) {
                                            // Values are different, so update the date and previous_value
                                            updateDateAndPreviousWaterValue("stored2", waterAmount);
                                        } else {
                                            // Values are the same, no update needed
                                            Log.d("Firestore Check", "No update needed, values are the same.");
                                        }

                                        if (rateSpeed != previousRateSpeed) {
                                            updateDateAndPreviousRateSpeed("stored2", rateSpeed);
                                        } else {
                                            Log.d("Firestore Check", "No update needed, values are the same.");
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.w("Data Parsing Error", "Failed to parse time_active or previous_value.", e);
                                    }
                                }
                            } else {
                                Log.d("Firestore Check", "Document does not exist.");
                            }
                        } else{
                            Log.w("Firestore Check", "Failed to get document.", task.getException());
                        }
                    }
                });

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // Convert milliseconds back to a Date
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd \n hh:mm", Locale.getDefault());
                    return sdf.format(new Date((long) value)); // Format and return the date
                } else {
                    return super.formatLabel(value, isValueX); // Default formatting for y-axis
                }
            }
        });

        waterAmountGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // Convert milliseconds back to a Date
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd \n hh:mm", Locale.getDefault());
                    return sdf.format(new Date((long) value)); // Format and return the date
                } else {
                    return super.formatLabel(value, isValueX); // Default formatting for y-axis
                }
            }
        });

        rateSpeedGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // Convert milliseconds back to a Date
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd \n hh:mm", Locale.getDefault());
                    return sdf.format(new Date((long) value)); // Format and return the date
                } else {
                    return super.formatLabel(value, isValueX); // Default formatting for y-axis
                }
            }
        });
    }

    private DataPoint[] createDataPoints(List<Double> timeActiveValues, List<Timestamp> dateValues) {
        DataPoint[] dataPoints = new DataPoint[timeActiveValues.size()];
        for (int i = 0; i < timeActiveValues.size(); i++) {
            double xValue = dateValues.get(i).toDate().getTime(); // Convert timestamp to milliseconds
            double yValue = timeActiveValues.get(i);              // Use time_active for Y-axis
            dataPoints[i] = new DataPoint(xValue, yValue);         // Create a DataPoint
        }
        return dataPoints;
    }

    private DataPoint[] createDataPoints2(List<Double> waterAmountValues, List<Timestamp> dateValues) {
        DataPoint[] dataPoints2 = new DataPoint[waterAmountValues.size()];
        for (int i = 0; i < waterAmountValues.size(); i++) {
            double xValue = dateValues.get(i).toDate().getTime(); // Convert timestamp to milliseconds
            double yValue = waterAmountValues.get(i);
            dataPoints2[i] = new DataPoint(xValue, yValue);         // Create a DataPoint
        }
        return dataPoints2;
    }

    private DataPoint[] createDataPoints3(List<Double> rateSpeedValues, List<Timestamp> dateValues) {
        DataPoint[] dataPoints3 = new DataPoint[rateSpeedValues.size()];
        for (int i = 0; i < rateSpeedValues.size(); i++) {
            double xValue = dateValues.get(i).toDate().getTime(); // Convert timestamp to milliseconds
            double yValue = rateSpeedValues.get(i);
            dataPoints3[i] = new DataPoint(xValue, yValue);         // Create a DataPoint
        }
        return dataPoints3;
    }
    private void updateDateAndPreviousValue(String documentId, double newTimeActive) {
        // Update the date and previous_value fields in Firestore
        db.collection("zones")
                .document(zone2)
                .collection("datapoints2")
                .document(documentId)
                .update(
                        "date", FieldValue.serverTimestamp(),  // Update date to the current server time
                        "previous_value", String.valueOf(newTimeActive) // Update previous_value to the new time_active
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore Update", "Date and previous_value successfully updated.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore Error", "Error updating date and previous_value: " + e.getMessage());
                    }
                });
    }

    private void updateDateAndPreviousWaterValue(String documentId, double newWaterAmount) {
        // Update the date and previous_value fields in Firestore
        db.collection("zones")
                .document(zone2)
                .collection("datapoints2")
                .document(documentId)
                .update(
                        "date", FieldValue.serverTimestamp(),  // Update date to the current server time
                        "previous_water", String.valueOf(newWaterAmount) // Update previous_value to the new time_active
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore Update", "Date and previous_water successfully updated.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore Error", "Error updating date and previous_water: " + e.getMessage());
                    }
                });
    }

    private void updateDateAndPreviousRateSpeed(String documentId, double newRateSpeed) {
        // Update the date and previous_value fields in Firestore
        db.collection("zones")
                .document(zone2)
                .collection("datapoints2")
                .document(documentId)
                .update(
                        "date", FieldValue.serverTimestamp(),  // Update date to the current server time
                        "previous_rate", String.valueOf(newRateSpeed) // Update previous_value to the new time_active
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore Update", "Date and previous_rate successfully updated.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore Error", "Error updating date and previous_rate: " + e.getMessage());
                    }
                });
    }

    private void setupGraph() {
        // Setup grid, viewport, and label formatting logic here.

        GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (Month/Day Hour:Minute)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Valve Time Amount Active (Minutes)");

        gridLabelRenderer.setTextSize(20); // Set text size for all labels
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
        graph.getGridLabelRenderer().setNumHorizontalLabels(6);

        long zoomTimeRange = 1 * 60 * 60 * 1000; // Last hour (in milliseconds)

        if (!dateValues.isEmpty()) {
            graph.getViewport().setXAxisBoundsManual(true);

            // Get the current time and set the viewport to show the last hour
            long endTime = dateValues.get(dateValues.size() - 1).toDate().getTime();
            long startTime = endTime - zoomTimeRange;

            graph.getViewport().setMinX(startTime);
            graph.getViewport().setMaxX(endTime);

            // Enable horizontal scrolling
            graph.getViewport().setScrollable(true);
        }

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(500);

        graph.addSeries(series); // Add the updated series to the graph

        graph.getViewport().scrollToEnd();
        graph.getViewport().setScalable(true);
    }

    private void setupWaterAmountGraph() {
        // Setup grid, viewport, and label formatting logic here.

        GridLabelRenderer gridLabelRenderer = waterAmountGraph.getGridLabelRenderer();

        waterAmountGraph.getGridLabelRenderer().setHorizontalAxisTitle("Time (Month/Day Hour:Minute)");
        waterAmountGraph.getGridLabelRenderer().setVerticalAxisTitle("Amount of Water Dispensed (Liters)");

        gridLabelRenderer.setTextSize(20); // Set text size for all labels
        waterAmountGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        waterAmountGraph.getGridLabelRenderer().setGridColor(Color.BLACK);
        waterAmountGraph.getGridLabelRenderer().setNumHorizontalLabels(6);

        long zoomTimeRange = 1 * 60 * 60 * 1000; // Last hour (in milliseconds)

        if (!dateValues.isEmpty()) {
            waterAmountGraph.getViewport().setXAxisBoundsManual(true);

            // Get the current time and set the viewport to show the last hour
            long endTime = dateValues.get(dateValues.size() - 1).toDate().getTime();
            long startTime = endTime - zoomTimeRange;

            waterAmountGraph.getViewport().setMinX(startTime);
            waterAmountGraph.getViewport().setMaxX(endTime);

            // Enable horizontal scrolling
            waterAmountGraph.getViewport().setScrollable(true);
        }

        waterAmountGraph.getViewport().setYAxisBoundsManual(true);
        waterAmountGraph.getViewport().setMinY(0);
        waterAmountGraph.getViewport().setMaxY(500);

        waterAmountGraph.addSeries(series2); // Add the updated series to the graph
        waterAmountGraph.invalidate();

        waterAmountGraph.getViewport().scrollToEnd();
        waterAmountGraph.getViewport().setScalable(true);

        Log.d("Water Graph Setup", "Success");
    }

    private void setupRateSpeedGraph() {
        // Setup grid, viewport, and label formatting logic here.

        GridLabelRenderer gridLabelRenderer = rateSpeedGraph.getGridLabelRenderer();

        rateSpeedGraph.getGridLabelRenderer().setHorizontalAxisTitle("Time (Month/Day Hour:Minute)");
        rateSpeedGraph.getGridLabelRenderer().setVerticalAxisTitle("Rate of Speed for Water (Liters/Seconds)");

        gridLabelRenderer.setTextSize(20); // Set text size for all labels
        rateSpeedGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        rateSpeedGraph.getGridLabelRenderer().setGridColor(Color.BLACK);
        rateSpeedGraph.getGridLabelRenderer().setNumHorizontalLabels(6);

        long zoomTimeRange = 1 * 60 * 60 * 1000; // Last hour (in milliseconds)

        if (!dateValues.isEmpty()) {
            rateSpeedGraph.getViewport().setXAxisBoundsManual(true);

            // Get the current time and set the viewport to show the last hour
            long endTime = dateValues.get(dateValues.size() - 1).toDate().getTime();
            long startTime = endTime - zoomTimeRange;

            rateSpeedGraph.getViewport().setMinX(startTime);
            rateSpeedGraph.getViewport().setMaxX(endTime);

            // Enable horizontal scrolling
            rateSpeedGraph.getViewport().setScrollable(true);
        }

        rateSpeedGraph.getViewport().setYAxisBoundsManual(true);
        rateSpeedGraph.getViewport().setMinY(0);
        rateSpeedGraph.getViewport().setMaxY(500);

        rateSpeedGraph.addSeries(series3); // Add the updated series to the graph
        rateSpeedGraph.invalidate();

        rateSpeedGraph.getViewport().scrollToEnd();
        rateSpeedGraph.getViewport().setScalable(true);
    }

    private void appendDataToGraph(double dateInMillis, double timeActive) {
        Log.d("Appending", "Appended");
        series.appendData(new DataPoint(dateInMillis, timeActive), false, 500); // Add to graph
        graph.invalidate();
        databaseHelper2.insertData(dateInMillis, timeActive); // Save to database
    }

    private void appendDataToWaterGraph(double dateInMillis, double waterAmount) {
        Log.d("Appending", "Appended");
        Log.d("Value", String.valueOf(waterAmount));
        series2.appendData(new DataPoint(dateInMillis, waterAmount), false, 500); // Add to graph
        waterAmountGraph.invalidate();
        waterAmountDatabaseHelper2.insertData(dateInMillis, waterAmount); // Save to database
    }

    private void appendDataToRateSpeedGraph(double dateInMillis, double rateSpeed) {
        Log.d("Appending", "Appended");
        Log.d("Value", String.valueOf(rateSpeed));
        series3.appendData(new DataPoint(dateInMillis, rateSpeed), false, 500); // Add to graph
        rateSpeedGraph.invalidate();
        rateSpeedDatabaseHelper2.insertData(dateInMillis, rateSpeed); // Save to database
    }

    private void loadDataFromDatabase() {
        Log.d("Loading", "Loaded");
        Cursor cursor = databaseHelper2.getAllData();

        if (cursor.moveToFirst()) {
            do {
                double timestamp = cursor.getDouble(1);
                double timeActive = cursor.getDouble(2);
                series.appendData(new DataPoint(timestamp, timeActive), false, 500); // Append data to graph
                long timestampMillis = (long) timestamp; // Cast to long
                dateValues.add(new Timestamp(new Date(timestampMillis))); // Create Timestamp from Date
            } while (cursor.moveToNext());
        }
        cursor.close();
        setupGraph();
    }

    private void loadDataFromWaterAmountDatabase() {
        Log.d("Loading", "Loaded Water Data");
        Cursor cursor = waterAmountDatabaseHelper2.getAllData();

        if (cursor.moveToFirst()) {
            do {
                double timestamp = cursor.getDouble(1);
                double waterAmount = cursor.getDouble(2);
                series2.appendData(new DataPoint(timestamp, waterAmount), false, 500); // Append data to graph
                long timestampMillis = (long) timestamp; // Cast to long
                dateValues.add(new Timestamp(new Date(timestampMillis))); // Create Timestamp from Date
            } while (cursor.moveToNext());
        }
        cursor.close();
        setupWaterAmountGraph();
    }

    private void loadDataFromRateSpeedDatabase() {
        Cursor cursor = rateSpeedDatabaseHelper2.getAllData();

        if (cursor.moveToFirst()) {
            do {
                double timestamp = cursor.getDouble(1);
                double rateSpeed = cursor.getDouble(2);
                series3.appendData(new DataPoint(timestamp, rateSpeed), false, 500); // Append data to graph
                long timestampMillis = (long) timestamp; // Cast to long
                dateValues.add(new Timestamp(new Date(timestampMillis))); // Create Timestamp from Date
            } while (cursor.moveToNext());
        }
        cursor.close();
        setupRateSpeedGraph();
    }
}