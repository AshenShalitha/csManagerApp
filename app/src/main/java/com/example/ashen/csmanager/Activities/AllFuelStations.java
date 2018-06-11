package com.example.ashen.csmanager.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ashen.csmanager.Adapters.TwoColumnListViewAdapter;
import com.example.ashen.csmanager.Others.MySingleton;
import com.example.ashen.csmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ashen.csmanager.Adapters.Constraints.FIRST_COLUMN;
import static com.example.ashen.csmanager.Adapters.Constraints.SECOND_COLUMN;
import static com.example.ashen.csmanager.Adapters.Constraints.THIRD_COLUMN;
import static com.example.ashen.csmanager.Others.EndPoints.ROOT_URL;

public class AllFuelStations extends AppCompatActivity {

    private ArrayList<HashMap<String,String>> fuelStationList = new ArrayList<HashMap<String,String>>();
    private ArrayList<String> idlist = new ArrayList<String>();
    private ListView fuelStationLV;
    private TwoColumnListViewAdapter fsAdapter;
    private ProgressBar spinner;
    private String fsListUrl = ROOT_URL+"fuelStations/getFSNamesAndCity";
    private String idSendUrl = ROOT_URL+"fuelStations/addFuelStation";
    private HashMap<String,String> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_fuel_stations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Fuel Stations");

        fuelStationLV = (ListView) findViewById(R.id.list_view);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        spinner.setVisibility(View.VISIBLE);
        //fetching fuel station names and city
        fetchData();

        fuelStationLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AllFuelStations.this,idlist.get(position),Toast.LENGTH_LONG).show();
                onClick(position);
            }
        });

    }



    //data fetching method
    private void fetchData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fsListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(!response.equals(""))spinner.setVisibility(View.INVISIBLE);
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("address");
                        temp = new HashMap<String, String>();
                        temp.put(FIRST_COLUMN,jsonObject.getString("name"));
                        temp.put(SECOND_COLUMN,jsonObject1.getString("city"));
                        idlist.add(jsonObject.getString("_id"));
                        fuelStationList.add(temp);
                    }
                    fsAdapter = new TwoColumnListViewAdapter(AllFuelStations.this,fuelStationList);
                    fsAdapter.notifyDataSetChanged();
                    fuelStationLV.setAdapter(fsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fsAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(AllFuelStations.this).addToRequestQueue(stringRequest);

    }

    public void onClick(final int x){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, idSendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    Toast.makeText(AllFuelStations.this,"Fuel Station added successfully",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("selectedId",idlist.get(x));
                return params;
            }
        };
        MySingleton.getInstance(AllFuelStations.this).addToRequestQueue(stringRequest);
    }

}
