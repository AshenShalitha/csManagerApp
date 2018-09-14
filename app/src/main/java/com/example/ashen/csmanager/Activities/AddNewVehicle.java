package com.example.ashen.csmanager.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ashen.csmanager.Others.MySingleton;
import com.example.ashen.csmanager.Others.SessionManager;
import com.example.ashen.csmanager.R;
import com.example.ashen.csmanager.models.Vehicle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.ashen.csmanager.Others.EndPoints.ROOT_URL;

public class AddNewVehicle extends AppCompatActivity {

    @BindView(R.id.vehicle_name) EditText vehicleNameET;
    @BindView(R.id.vehicle_number) EditText vehicleNumberET;
    @BindView(R.id.limit) EditText limitET;
    @BindView(R.id.btn_submit) Button submitBTN;

    private String vehicleName;
    private String vehicleNumber;
    private String limit;
    private String addedBy;
    private String ownersId;
    private String token;
    private String postUrl = ROOT_URL+"vehicles";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_vehicle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add New Vehicle");
        ButterKnife.bind(this);

        SessionManager sessionManager = new SessionManager(AddNewVehicle.this);
        token = sessionManager.getUserDetails().get("token");
        addedBy = sessionManager.getUserDetails().get("name");
        ownersId = sessionManager.getUserDetails().get("id");

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    public void submit()
    {
        if(!validate())
        {
            onSubmitFailed();
            return;
        }
        else
        {
            submitBTN.setEnabled(false);
            progressDialog = new ProgressDialog(AddNewVehicle.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Submitting..");
            progressDialog.show();
            sendData();

            Intent intent = new Intent("finish_activity");
            sendBroadcast(intent);

        }
    }

    public boolean validate()
    {
        vehicleName = vehicleNameET.getText().toString();
        vehicleNumber = vehicleNumberET.getText().toString().toUpperCase();
        limit = limitET.getText().toString();

        if(vehicleName.isEmpty())
        {
            vehicleNameET.setError("Required!");
            return false;
        }
        else if(vehicleNumber.isEmpty())
        {
            vehicleNumberET.setError("Required!");
            return false;
        }
        if(limit.isEmpty())
        {
            limitET.setError("Required!");
            return false;
        }
        return true;
    }

    public void onSubmitFailed()
    {
        submitBTN.setEnabled(true);
    }

    public void onSubmitSuccess()
    {
        submitBTN.setEnabled(true);
        Intent intent = new Intent(AddNewVehicle.this, Vehicles.class);
        startActivity(intent);
        finish();
    }

    public void sendData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, postUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean success;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getBoolean("success");
                    if(success){
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
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(),"Vehicle already added!", Toast.LENGTH_LONG).show();
                onSubmitFailed();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("vehicleName",vehicleName);
                params.put("vehicleNumber",vehicleNumber);
                params.put("allocatedLimit",limit);
                params.put("addedBy",addedBy);
                params.put("ownersId",ownersId);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ token);
                return params;
            }
        };
        MySingleton.getInstance(AddNewVehicle.this).addToRequestQueue(stringRequest);
    }

}
