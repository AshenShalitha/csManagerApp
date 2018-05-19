package com.example.ashen.csmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ashen.csmanager.Adapters.FuelStationsAdapter;
import com.example.ashen.csmanager.models.Address;
import com.example.ashen.csmanager.models.FuelStation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class AllFuelStations extends AppCompatActivity {

    private List<FuelStation> fuelStationList = new ArrayList<FuelStation>();
    private RecyclerView fuelStationRV;
    private FuelStationsAdapter fsAdapter;
    private FuelStation[] fuelStations;
    private String url = "http://192.168.8.103:3000/fuelStations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_fuel_stations);
        setTitle("Add Fuel Stations");

        fuelStationRV = (RecyclerView)findViewById(R.id.recycler_view);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                fuelStations = gson.fromJson(response, FuelStation[].class);
                for(int i=0;i<fuelStations.length;i++)
                {
                    fuelStationList.add(fuelStations[i]);
                }
                fsAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(AllFuelStations.this).addToRequestQueue(stringRequest);

        fsAdapter = new FuelStationsAdapter(fuelStationList);
        RecyclerView.LayoutManager fsLayoutManager = new LinearLayoutManager(getApplicationContext());
        fuelStationRV.setLayoutManager(fsLayoutManager);
        fuelStationRV.setItemAnimator(new DefaultItemAnimator());
        fuelStationRV.setAdapter(fsAdapter);
    }
}
