package com.example.ashen.csmanager.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ashen.csmanager.Adapters.TwoColumnListViewAdapter;
import com.example.ashen.csmanager.Others.MySingleton;
import com.example.ashen.csmanager.Others.SessionManager;
import com.example.ashen.csmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ashen.csmanager.Adapters.Constraints.FIRST_COLUMN;
import static com.example.ashen.csmanager.Adapters.Constraints.SECOND_COLUMN;
import static com.example.ashen.csmanager.Others.EndPoints.ROOT_URL;


public class SelectedFuelStations extends AppCompatActivity {

    private ListView selectedFsLV;
    private TwoColumnListViewAdapter fsAdapter;
    private ArrayList<HashMap<String,String>> fuelStationList = new ArrayList<HashMap<String,String>>();
    private HashMap<String,String> temp;
    private String token,customerId;
    private String getUrl = ROOT_URL+"customers/viewSelectedFuelStations";
    private ProgressDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_fuel_stations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Selected Fuel Stations");

        selectedFsLV = (ListView)findViewById(R.id.list_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedFuelStations.this,AllFuelStations.class);
                startActivity(intent);
                finish();
            }
        });

        SessionManager sessionManager = new SessionManager(SelectedFuelStations.this);
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");

        fetchData();

    }


    private void fetchData()
    {
        loadingDialog = new ProgressDialog(SelectedFuelStations.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONArray jsonArray = jsonObject1.getJSONArray("fuelStationIds");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        temp = new HashMap<String, String>();
                        temp.put(FIRST_COLUMN,jsonObject2.getJSONObject("fsid").getString("name"));
                        temp.put(SECOND_COLUMN,jsonObject2.getString("status"));
                        fuelStationList.add(temp);
                    }
                    fsAdapter = new TwoColumnListViewAdapter(SelectedFuelStations.this,fuelStationList);
                    fsAdapter.notifyDataSetChanged();
                    selectedFsLV.setAdapter(fsAdapter);
                    loadingDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
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
                params.put("_id",customerId);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ token);
                return params;
            }
        };
        MySingleton.getInstance(SelectedFuelStations.this).addToRequestQueue(stringRequest);
    }

}
