package com.example.ashen.csmanager.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


public class VehicleDetailsFragment extends Fragment {

    private View rootView;
    private String token,customerId;
    private String vehicleId;
    private String url = ROOT_URL+"vehicles";
    private TextView vehicleNameTV;
    private TextView vehicleNumberTV;
    private TextView allocatedLimitTV;
    private TextView addedByTV;
    private TextView createdAtTV;
    private TextView lastUpdateAtTV;
    private ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_vehicle_details, container, false);

        SessionManager sessionManager = new SessionManager(getActivity());
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");

        Bundle extra = getActivity().getIntent().getExtras();
        vehicleId = extra.getString("vehicleId");
        Log.i("VehicleId ",vehicleId );

        vehicleNameTV = (TextView)rootView.findViewById(R.id.vehicleNameTV);
        vehicleNumberTV = (TextView)rootView.findViewById(R.id.vehicleNumberTV);
        allocatedLimitTV = (TextView)rootView.findViewById(R.id.allocatedLimitTV);
        addedByTV = (TextView)rootView.findViewById(R.id.addedByTV);
        createdAtTV = (TextView)rootView.findViewById(R.id.createdAtTV);
        lastUpdateAtTV = (TextView)rootView.findViewById(R.id.lastUpdateAtTV);

        loadingDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.show();

        fetchVehicleData();

        return rootView;
    }

    private void fetchVehicleData(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/"+vehicleId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    vehicleNameTV.setText(jsonObject.getString("vehicleName"));
                    vehicleNumberTV.setText("Number : "+jsonObject.getString("vehicleNumber"));
                    allocatedLimitTV.setText("Allocated Limit : Rs:"+jsonObject.getString("allocatedLimit"));
                    addedByTV.setText("Added By : "+jsonObject.getString("addedBy"));
                    createdAtTV.setText("Added at : "+formatDateAndTime(jsonObject.getString("createdAt")));
                    lastUpdateAtTV.setText("Last Update at : "+formatDateAndTime(jsonObject.getString("updatedAt")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(loadingDialog != null)
                    loadingDialog.dismiss();
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

    private String formatDateAndTime(String s) {

        String dateAndTime = s.substring(0,10)+" at "+s.substring(11,16);
        return dateAndTime;
    }

}
