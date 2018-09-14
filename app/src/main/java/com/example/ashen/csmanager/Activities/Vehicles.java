package com.example.ashen.csmanager.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vehiclesLV = (ListView)findViewById(R.id.list_view);

        SessionManager sessionManager = new SessionManager(Vehicles.this);
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Vehicles.this,AddNewVehicle.class);
                startActivity(intent);
            }
        });

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
                                deleteVehicle(position);
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

        vehiclesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Vehicles.this,VehicleDetails.class);
                intent.putExtra("vehicleId",idlist.get(position));
                startActivity(intent);
            }
        });

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));
    }

    public void fetchData()
    {
        loadingDialog = new ProgressDialog(Vehicles.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.show();
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
                    loadingDialog.dismiss();
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ token);
                return params;
            }
        };
        MySingleton.getInstance(Vehicles.this).addToRequestQueue(stringRequest);
    }


    public void deleteVehicle(final int x)
    {
        loadingDialog = new ProgressDialog(Vehicles.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Deleting..");
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, deleteUrl+"/"+idlist.get(x).toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(loadingDialog!=null)
                    loadingDialog.dismiss();
                Toast.makeText(Vehicles.this,"Vehicle has been deleted!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Vehicles.this,Vehicles.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ token);
                return params;
            }
        };
        MySingleton.getInstance(Vehicles.this).addToRequestQueue(stringRequest);
    }

}
