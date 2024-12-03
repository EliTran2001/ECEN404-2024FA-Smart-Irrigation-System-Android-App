package com.example.ecen403;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.content.Intent;


import com.example.ecen403.ZoneDataActivites.Zone1;
import com.example.ecen403.ZoneDataActivites.Zone10;
import com.example.ecen403.ZoneDataActivites.Zone2;
import com.example.ecen403.ZoneDataActivites.Zone3;
import com.example.ecen403.ZoneDataActivites.Zone4;
import com.example.ecen403.ZoneDataActivites.Zone5;
import com.example.ecen403.ZoneDataActivites.Zone6;
import com.example.ecen403.ZoneDataActivites.Zone7;
import com.example.ecen403.ZoneDataActivites.Zone8;
import com.example.ecen403.ZoneDataActivites.Zone9;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class home extends Fragment implements RecyclerViewAdapter.ItemClickListener{
    private ArrayList<ZoneAdapter> list = new ArrayList<> ();

    public home() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static home newInstance() {
        home fragment = new home();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        buildListData();
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(list, this);
        recyclerView.setAdapter(adapter);
    }


    private void buildListData () {
        list.add(new ZoneAdapter("Zone 1"));
        list.add(new ZoneAdapter("Zone 2"));
        list.add(new ZoneAdapter("Zone 3"));
        list.add(new ZoneAdapter("Zone 4"));
        list.add(new ZoneAdapter("Zone 5"));
        list.add(new ZoneAdapter("Zone 6"));
        list.add(new ZoneAdapter("Zone 7"));
        list.add(new ZoneAdapter("Zone 8"));
        list.add(new ZoneAdapter("Zone 9"));
        list.add(new ZoneAdapter("Zone 10"));
    }

    @Override
    public void onItemClick(ZoneAdapter zoneAdapter) {
        //Fragment fragment = ZoneData.newInstance(zoneAdapter.getZone());

        //FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.replace(R.id.frame_container,fragment, "zoneData_fragment");
        //fragmentTransaction.addToBackStack(null);
        //fragmentTransaction.commit();

        Intent intent = null;

        // Determine which zone was clicked and start the corresponding activity
        switch (zoneAdapter.getZone()) {
            case "Zone 1":
                intent = new Intent(getActivity(), Zone1.class);
                break;
            case "Zone 2":
                intent = new Intent(getActivity(), Zone2.class);
                break;
            case "Zone 3":
                intent = new Intent(getActivity(), Zone3.class);
                break;
            case "Zone 4":
                intent = new Intent(getActivity(), Zone4.class);
                break;
            case "Zone 5":
                intent = new Intent(getActivity(), Zone5.class);
                break;
            case "Zone 6":
                intent = new Intent(getActivity(), Zone6.class);
                break;
            case "Zone 7":
                intent = new Intent(getActivity(), Zone7.class);
                break;
            case "Zone 8":
                intent = new Intent(getActivity(), Zone8.class);
                break;
            case "Zone 9":
                intent = new Intent(getActivity(), Zone9.class);
                break;
            case "Zone 10":
                intent = new Intent(getActivity(), Zone10.class);
                break;
        }

        if(intent != null) {
            Log.d("HomeFragment", "Starting" + zoneAdapter.getZone());
            startActivity(intent);
        }
    }
}