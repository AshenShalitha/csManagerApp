package com.example.ashen.csmanager.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.ashen.csmanager.models.Vehicle;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ashen.csmanager.Adapters.Constraints.FIRST_COLUMN;
import static com.example.ashen.csmanager.Adapters.Constraints.SECOND_COLUMN;
import static com.example.ashen.csmanager.Others.EndPoints.ROOT_URL;

public class Vehicles extends AppCompatActivity {

    private ListView vehiclesLV;
    private TwoColumnListViewAdapter vAdapter;
    private ArrayList<HashMap<String,String>> vehicleList = new ArrayList<HashMap<String,String>>();
    private ArrayList<String> idlist = new ArrayList<String>();
    private HashMap<String,String> temp;
    private String token,customerId;
    private String addUrl = ROOT_URL+"vehicles/vehiclesByOwnersId";
    private String deleteUrl = ROOT_URL+"vehicles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vehiclesLV = (ListView)findViewById(R.id.list_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Vehicles.this,AddNewVehicle.class);
                startActivity(intent);
                finish();
            }
        });

        SessionManager sessionManager = new SessionManager(Vehicles.this);
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");

        fetchData();

        vehiclesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Vehicles.this);
                alertDialogBuilder.setTitle("Delete Vehicle?");
                alertDialogBuilder
                        .setMessage("Click yes to confirm")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(Vehicles.this,"Vehicle has been deleted!",Toast.LENGTH_LONG).show();
                                deleteVehicle(position);
                                Intent intent = new Intent(Vehicles.this,Vehicles.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    public void fetchData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, addUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        temp = new HashMap<String, String>();
                        temp.put(FIRST_COLUMN,jsonObject.getString("vehicleName"));
                        temp.put(SECOND_COLUMN,jsonObject.getString("vehicleNumber"));
                        idlist.add(jsonObject.getString("_id"));
                        vehicleList.add(temp);
                    }
                    vAdapter = new TwoColumnListViewAdapter(Vehicles.this,vehicleList);
                    vAdapter.notifyDataSetChanged();
                    vehiclesLV.setAdapter(vAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("ownersId",customerId);
                return params;
            }
        };
        MySingleton.getInstance(Vehicles.this).addToRequestQueue(stringRequest);
    }


    public void deleteVehicle(final int x)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, deleteUrl+"/"+idlist.get(x).toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(Vehicles.this).addToRequestQueue(stringRequest);
    }

}
