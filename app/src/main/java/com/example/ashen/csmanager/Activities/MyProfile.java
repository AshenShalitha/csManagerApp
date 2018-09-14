package com.example.ashen.csmanager.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ashen.csmanager.Others.MySingleton;
import com.example.ashen.csmanager.Others.SessionManager;
import com.example.ashen.csmanager.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.ashen.csmanager.Others.EndPoints.ROOT_URL;

public class MyProfile extends AppCompatActivity {

    private TextView nameTV;
    private TextView addressTV;
    private TextView userTypeTV;
    private TextView usernameTV;
    private TextView contactPersonTV;
    private TextView contactNumberTV;
    private TextView createdAtTV;
    private TextView lastUpdateAtTV;
    private Button editBTN;
    private Button changePwBTN;
    private String token,customerId;
    private String getUerDetailsUrl = ROOT_URL+"customers";
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("My Profile");

        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");

        nameTV = (TextView)findViewById(R.id.customerNameTV);
        addressTV = (TextView)findViewById(R.id.addressTV);
        userTypeTV = (TextView)findViewById(R.id.userTypeTV);
        usernameTV = (TextView)findViewById(R.id.usernameTV);
        contactPersonTV = (TextView)findViewById(R.id.contactPersonTV);
        contactNumberTV = (TextView)findViewById(R.id.contactNumberTV);
        createdAtTV = (TextView)findViewById(R.id.createdAtTV);
        lastUpdateAtTV = (TextView)findViewById(R.id.lastUpdateAtTV);
        editBTN = (Button) findViewById(R.id.editBTN);
        changePwBTN = (Button)findViewById(R.id.changePwBTN);

        loadingDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.show();

        fetchData();

        editBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfile.this,EditUserDetailsActivity.class);
                startActivity(intent);
            }
        });

        changePwBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfile.this,ChangePaswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getUerDetailsUrl+"/"+customerId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response : ",response);
                setData(response);
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
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void setData(String response)
    {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONObject address = jsonObject.getJSONObject("address");
            nameTV.setText(jsonObject.getString("name"));
            addressTV.setText(address.getString("number")+", "+address.getString("street")+", "+address.getString("city")+".");
            userTypeTV.setText("User Type : "+jsonObject.getString("type"));
            usernameTV.setText("Username : "+jsonObject.getString("username"));
            contactPersonTV.setText("Contact Person : "+jsonObject.getString("contactPersonName"));
            contactNumberTV.setText("Contact Number : "+jsonObject.getString("phone"));
            createdAtTV.setText("Joined At : "+formatDateAndTime(jsonObject.getString("createdAt")));
            lastUpdateAtTV.setText("Last Update : "+formatDateAndTime(jsonObject.getString("updatedAt")));
            if(loadingDialog != null)
                loadingDialog.dismiss();

        } catch (JSONException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private String formatDateAndTime(String s) {

        String dateAndTime = s.substring(0,10)+" at "+s.substring(11,16);
        return dateAndTime;
    }
}
