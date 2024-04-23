package com.example.ecen403.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecen403.Adapter.TommorrowAdapter;
import com.example.ecen403.Domains.TommorrowDomain;
import com.example.ecen403.R;

import java.util.ArrayList;

public class TommorrowActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterTommorrow;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tommorrow);

        initRecyclerView();
        setVariable();
    }

    private void setVariable() {
        ConstraintLayout backBtn=findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> startActivity(new Intent(TommorrowActivity.this, Weather.class)));
    }
    private void initRecyclerView() {
        ArrayList<TommorrowDomain> items=new ArrayList<>();

        items.add(new TommorrowDomain("Sat","storm","Storm",25,10));
        items.add(new TommorrowDomain("Sun","cloudy","Rainy_Sunny",23,16));
        items.add(new TommorrowDomain("Mon","cloudy_3","Cloudy",29,15));
        items.add(new TommorrowDomain("Tue","cloudy_sunny","Cloudy_Sunny",22,13));
        items.add(new TommorrowDomain("Wen","sun","Sunny",28,11));
        items.add(new TommorrowDomain("Thu","rainy","Rainy",23,12));

        recyclerView=findViewById(R.id.view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        adapterTommorrow=new TommorrowAdapter(items);
        recyclerView.setAdapter(adapterTommorrow);
    }
}