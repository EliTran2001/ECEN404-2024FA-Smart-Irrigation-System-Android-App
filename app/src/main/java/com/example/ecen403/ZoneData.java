package com.example.ecen403;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ecen403.Activities.Weather;
import com.example.ecen403.ZoneDataActivites.Zone1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZoneData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZoneData extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mParam1;


    public ZoneData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.

     * @return A new instance of fragment ZoneData.
     */
    // TODO: Rename and change types and number of parameters
    public static ZoneData newInstance(String param1) {
        ZoneData fragment = new ZoneData();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Intent intent = new Intent(getActivity(), Zone1.class);
            startActivity(intent);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zone_data, container, false);
        TextView zoneData = view.findViewById(R.id.zoneData);
        //zoneData.setText(mParam1);
        return view;
    }
}