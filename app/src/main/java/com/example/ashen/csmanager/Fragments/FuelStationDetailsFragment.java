package com.example.ashen.csmanager.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class FuelStationDetailsFragment extends Fragment {

    View rootView;
    private TextView fuelStationNameTV;
    private TextView addressTV;
    private Button callBTN;
    private Button messageBTN;
    private String url = ROOT_URL+"fuelStations";
    private String fuelStationId,token;
    private String phoneNumber;
    private ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fuel_station_details, container, false);

        SessionManager sessionManager = new SessionManager(getActivity());
        token = sessionManager.getUserDetails().get("token");

        Bundle extra = getActivity().getIntent().getExtras();
        fuelStationId = extra.getString("fuelStationId");

        fuelStationNameTV = (TextView) rootView.findViewById(R.id.fuelStationNameTV);
        addressTV = (TextView)rootView.findViewById(R.id.addressTV);
        callBTN = (Button) rootView.findViewById(R.id.callBTN);
        messageBTN = (Button)rootView.findViewById(R.id.messageBTN);

        loadingDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
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

        return rootView;
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
                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

}
