package com.example.ashen.csmanager.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class FuelStationDetailsActivity extends AppCompatActivity {

    private TextView fuelStationNameTV;
    private TextView addressTV;
    private Button callBTN;
    private Button messageBTN;
    private String url = ROOT_URL+"fuelStations";
    private String fuelStationId,token;
    private String phoneNumber;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_station_details2);

        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getUserDetails().get("token");

        Bundle extra = this.getIntent().getExtras();
        fuelStationId = extra.getString("fuelStationId");

        fuelStationNameTV = (TextView)findViewById(R.id.fuelStationNameTV);
        addressTV = (TextView)findViewById(R.id.addressTV);
        callBTN = (Button)findViewById(R.id.callBTN);
        messageBTN = (Button)findViewById(R.id.messageBTN);

        loadingDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.show();

        fetchData();

        callBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(callIntent);
            }
        });

        messageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:"+phoneNumber);
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(it);
            }
        });

    }

    private void fetchData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/"+fuelStationId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject address = jsonObject.getJSONObject("address");
                    phoneNumber = jsonObject.getString("phone");
                    fuelStationNameTV.setText(jsonObject.getString("name"));
                    addressTV.setText(address.getString("number")+", "+address.getString("street")+", "+address.getString("city")+".");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(loadingDialog != null)
                    loadingDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}
