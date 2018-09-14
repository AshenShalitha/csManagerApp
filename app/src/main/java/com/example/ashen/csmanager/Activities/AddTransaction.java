package com.example.ashen.csmanager.Activities;

import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ashen.csmanager.Others.MySingleton;
import com.example.ashen.csmanager.Others.SessionManager;
import com.example.ashen.csmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.ashen.csmanager.Adapters.Constraints.FIRST_COLUMN;
import static com.example.ashen.csmanager.Others.EndPoints.ROOT_URL;

public class AddTransaction extends AppCompatActivity {

    private ArrayAdapter<String> fuelStationAdapter;
    private ArrayAdapter<String> vehicleAdapter;
    private ArrayAdapter<CharSequence> fuelTypeAdapter;
    private ArrayList<String> fuelStationIdList;
    private ArrayList<String> vehicleIdList;
    private Spinner fuelStationSP;
    private Spinner vehicleSP;
    private Spinner fuelTypeSP;
    private ProgressDialog loadingDialog;
    private ProgressDialog progressDialog;
    private String fuelStationUrl = ROOT_URL+"customers/viewSelectedFuelStations";
    private String vehicleUrl = ROOT_URL+"vehicles/vehiclesByOwnersId";
    private String sendDataUrl = ROOT_URL+"purchaseOrders";
    private String token,customerId;
    private String fuelStation,vehicle,fuelType,addedBy,amount,vehicleId,fuelStationId;

    @BindView(R.id.addedBy) EditText addedbyET;
    @BindView(R.id.amount) EditText amountET;
    @BindView(R.id.btn_submit) Button submitBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add New Transaction");
        ButterKnife.bind(this);

        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");

        setSpinners();

        fuelStationSP = (Spinner)findViewById(R.id.fuelStation);
        vehicleSP = (Spinner)findViewById(R.id.vehicle);
        fuelTypeSP = (Spinner)findViewById(R.id.fuelType);

        fuelTypeAdapter = ArrayAdapter.createFromResource(this,R.array.fueltype,R.layout.spinner_item);
        fuelTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        fuelTypeSP.setAdapter(fuelTypeAdapter);

        fuelStationSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fuelStation = parent.getItemAtPosition(position).toString();
                fuelStationId = fuelStationIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        vehicleSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicle = parent.getItemAtPosition(position).toString();
                vehicleId = vehicleIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fuelTypeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fuelType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    private void setSpinners(){

        final ArrayList<String> fslist = new ArrayList<String>();
        final ArrayList<String> vehiclelist = new ArrayList<String>();

        loadingDialog = new ProgressDialog(AddTransaction.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, fuelStationUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                fuelStationIdList = new ArrayList<String>();
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONArray jsonArray = jsonObject1.getJSONArray("fuelStationIds");
                    fslist.add("Select Fuel Station");
                    fuelStationIdList.add("default");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        String name = jsonObject2.getJSONObject("fsid").getString("name");
                        String city = jsonObject2.getJSONObject("fsid").getJSONObject("address").getString("city");
                        String fsId = jsonObject2.getJSONObject("fsid").getString("_id");
                        if(jsonObject2.getString("status").equals("accepted"))
                        {
                            fslist.add(name+", "+city);
                            fuelStationIdList.add(fsId);
                        }

                    }
                    fuelStationAdapter = new ArrayAdapter<String>(AddTransaction.this,R.layout.spinner_item,fslist);
                    fuelStationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    fuelStationSP.setAdapter(fuelStationAdapter);
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
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, vehicleUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                vehicleIdList = new ArrayList<String>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    vehiclelist.add("Select Vehicle");
                    vehicleIdList.add("default");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String s = jsonObject.getString("vehicleName")+" ("+jsonObject.getString("vehicleNumber")+")";
                        vehicleId = jsonObject.getString("_id");
                        vehiclelist.add(s);
                        vehicleIdList.add(vehicleId);
                    }
                    vehicleAdapter = new ArrayAdapter<>(AddTransaction.this,R.layout.spinner_item,vehiclelist);
                    vehicleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    vehicleSP.setAdapter(vehicleAdapter);
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
        MySingleton.getInstance(AddTransaction.this).addToRequestQueue(stringRequest1);

    }


    private void submit(){

        if(!validate()){
            onSubmitFailed();
        }
        else{
            submitBTN.setEnabled(false);
            progressDialog = new ProgressDialog(AddTransaction.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Submitting..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            sendData();

            Intent intent = new Intent("finish_activity");
            sendBroadcast(intent);
        }

    }

    private boolean validate(){
        addedBy = addedbyET.getText().toString();
        amount = amountET.getText().toString();

        if(fuelStation.equals("Select Fuel Station")){
            Toast.makeText(AddTransaction.this,"Please select fuel station",Toast.LENGTH_LONG).show();
            return false;
        }
        if(vehicle.equals("Select Vehicle")){
            Toast.makeText(AddTransaction.this,"Please select vehicle",Toast.LENGTH_LONG).show();
            return false;
        }
        if(fuelType.equals("Select Fuel Type")){
            Toast.makeText(AddTransaction.this,"Please select fuel type",Toast.LENGTH_LONG).show();
            return false;
        }
        if(addedBy.isEmpty()){
            addedbyET.setError("Required!");
            return false;
        }
        if(amount.isEmpty()){
            amountET.setError("Required!");
            return false;
        }

        return true;
    }

    private void onSubmitFailed(){
        submitBTN.setEnabled(true);
    }

    private void onSubmitSuccess(){
        Toast.makeText(AddTransaction.this,"Purchase order has been sent!",Toast.LENGTH_LONG).show();
        submitBTN.setEnabled(true);
        Intent intent = new Intent(AddTransaction.this, Transactions.class);
        startActivity(intent);
        finish();
    }

    private void sendData(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendDataUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("true")) {
                        progressDialog.dismiss();
                        onSubmitSuccess();
                    }
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
                params.put("fuelStationId",fuelStationId);
                params.put("vehicleId",vehicleId);
                params.put("fuelType",fuelType);
                params.put("enteredBy",addedBy);
                params.put("price",amount);
                params.put("customerId",customerId);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ token);
                return params;
            }
        };
        MySingleton.getInstance(AddTransaction.this).addToRequestQueue(stringRequest);
    }
}



