package com.example.ecen403.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.ecen403.R
import org.json.JSONArray
import org.json.JSONObject

public class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        textView = findViewById(R.id.dummy_text)

    val back_btn = findViewById<Button>(R.id.back_btn)

        back_btn.setOnClickListener{
            val Intent = Intent(this, Home::class.java)
            startActivity(Intent)
    }

        val jsonData = applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "dummy",
                "raw",
                applicationContext.packageName
            )

        ).bufferedReader().use {it.readText()}

        val outputJsonString = JSONObject(jsonData)

        val MCUdata = outputJsonString.getJSONArray("MCUdata") as JSONArray

        for (i in 0 until MCUdata.length()) {               // reading the JSON file

            val Zone = MCUdata.getJSONObject(i).getString("Zone")

            val soilMoisture = MCUdata.getJSONObject(i).getString("soilMoisture")

            val temperature = MCUdata.getJSONObject(i).getString("temperature")

            val rainLevel = MCUdata.getJSONObject(i).getString("rainLevel")

            val humidity = MCUdata.getJSONObject(i).getString("humidity")

            val previousData = textView.text

            var data:String = // display data from JSON file
                    "Zone: $Zone" +
                    "\n Soil Moisture: $soilMoisture" +
                    "\n Temperature: $temperature" +
                    "\n Rain Level: $rainLevel" +
                    "\n Humidity: $humidity" +
                    "\n \n"

            textView.text = previousData.toString() + data
        }
    }

}