package com.example.ashen.csmanager.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import com.example.ashen.csmanager.Fragments.FuelStationTransactionsFragment;
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
import static com.example.ashen.csmanager.Adapters.Constraints.THIRD_COLUMN;
import static com.example.ashen.csmanager.Others.EndPoints.ROOT_URL;

public class AllFuelStations extends AppCompatActivity {

    private ArrayList<HashMap<String,String>> fuelStationList = new ArrayList<HashMap<String,String>>();
    private ArrayList<String> idlist = new ArrayList<String>();
    private ListView fuelStationLV;
    private TwoColumnListViewAdapter fsAdapter;
    private String fsListUrl = ROOT_URL+"fuelStations/getFSNamesAndCity";
    private String sendDataToCustomerUrl = ROOT_URL+"customers/addFillingStation/";
    private String sendDataToFuelStationUrl = ROOT_URL+"fuelStations/addFillingStation";
    private HashMap<String,String> temp;
    private String token,customerId;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_fuel_stations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Filling Stations");

        SessionManager sessionManager = new SessionManager(AllFuelStations.this);
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");
        fuelStationLV = (ListView) findViewById(R.id.list_view);

        //fetching fuel station names and city
        fetchData();

        fuelStationLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllFuelStations.this,FuelStationDetailsActivity.class);
                intent.putExtra("fuelStationId",idlist.get(position));
                startActivity(intent);
            }
        });
        fuelStationLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AllFuelStations.this);
                alertDialogBuilder.setTitle("Add Filling Station");
                alertDialogBuilder
                        .setMessage("Click yes to confirm")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(AllFuelStations.this,"Your request has been sent!",Toast.LENGTH_LONG).show();
                                sendDataToCustomer(position);   //update customer document as pending
//                                sendDataToFuelStation(position);  //update fuel station document as pending
                                Intent intent = new Intent(AllFuelStations.this,SelectedFuelStations.class);
                                startActivity(intent);
                                finish();
                                Intent intent1 = new Intent("finish_activity");
                                sendBroadcast(intent1);
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


    //Fetch all fuel stations names and city
    private void fetchData()
    {
        loadingDialog = new ProgressDialog(AllFuelStations.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fsListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
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
                    loadingDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fsAdapter.notifyDataSetChanged();

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
                params.put("Content-Type","application/json");
                params.put("Authorization", "Bearer "+ token);
                return params;
            }
        };
        MySingleton.getInstance(AllFuelStations.this).addToRequestQueue(stringRequest);

    }

    //update customer document as pending
    public void sendDataToCustomer(final int x){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendDataToCustomerUrl+customerId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AllFuelStations.this,error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("status","pending");
                params.put("fsid",idlist.get(x));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ token);
                return params;
            }
        };
        MySingleton.getInstance(AllFuelStations.this).addToRequestQueue(stringRequest);
    }

    //update fillingStation document
    public void sendDataToFuelStation(final int x){

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, sendDataToFuelStationUrl, new Response.Listener<String>() {
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ token);
                return params;
            }
        };
        MySingleton.getInstance(AllFuelStations.this).addToRequestQueue(stringRequest);
    }

}
