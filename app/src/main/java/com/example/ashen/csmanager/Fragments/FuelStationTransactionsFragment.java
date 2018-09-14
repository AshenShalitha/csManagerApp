package com.example.ashen.csmanager.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ashen.csmanager.Adapters.RvAdapter;
import com.example.ashen.csmanager.Others.MySingleton;
import com.example.ashen.csmanager.Others.SessionManager;
import com.example.ashen.csmanager.R;
import com.example.ashen.csmanager.models.PurchaseOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ashen.csmanager.Others.EndPoints.ROOT_URL;


public class FuelStationTransactionsFragment extends Fragment {

    View rootView;
    private String transactionUrl = ROOT_URL+"purchaseOrders/transactionByFuelStationId";
    private RecyclerView rv;
    private TextView tv;
    private TextView totalTV;
    private String fuelstationId,customerId,token;
    private PurchaseOrder purchaseOrder;
    private List<PurchaseOrder> purchaseOrderList;
    private ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fuel_station_transactions, container, false);

        Bundle extra = getActivity().getIntent().getExtras();
        fuelstationId = extra.getString("fuelStationId");
        Log.i("fuelStationId ",fuelstationId );

        SessionManager sessionManager = new SessionManager(getActivity());
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");

        totalTV = (TextView)rootView.findViewById(R.id.totalTV);
        tv = (TextView)rootView.findViewById(R.id.tv);
        rv = (RecyclerView)rootView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        loadingDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.show();


        fetchTransactionData();
        return rootView;
    }

    private void fetchTransactionData(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST,transactionUrl , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                
                purchaseOrderList = new ArrayList<PurchaseOrder>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if(jsonArray.isNull(0)){
                        tv.setVisibility(View.VISIBLE);
                    }
                    for(int i=jsonArray.length()-1;i>=0;i--){
                        JSONObject outerObject = jsonArray.getJSONObject(i);
                        JSONObject customerObj = outerObject.getJSONObject("customerId");
                        JSONObject fuelStationObj = outerObject.getJSONObject("fuelStationId");
                        JSONObject vehicleObj = outerObject.getJSONObject("vehicleId");
                        purchaseOrder = new PurchaseOrder();
                        purchaseOrder.setFuelType("FUEL TYPE : "+outerObject.getString("fuelType"));
                        purchaseOrder.setPrice("PRICE : "+outerObject.getString("price"));
                        purchaseOrder.setEnteredBy("ENTERED BY : "+outerObject.getString("enteredBy"));
                        purchaseOrder.setCreatedAt(formatDateAndTime(outerObject.getString("createdAt")));
                        purchaseOrder.setCustomer("CUSTOMER : "+customerObj.getString("name"));
                        purchaseOrder.setFuelStation("FUEL STATION : "+fuelStationObj.getString("name"));
                        purchaseOrder.setVehicle(vehicleObj.getString("vehicleName"));
                        purchaseOrder.setVehicleNumber(vehicleObj.getString("vehicleNumber"));

                        purchaseOrderList.add(purchaseOrder);
                    }
                    totalTV.setText("Total Number of Transactions : "+purchaseOrderList.size());
                    initializeAdapter();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("fuelStationId",fuelstationId);
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void initializeAdapter(){
        RvAdapter adapter = new RvAdapter(purchaseOrderList);
        rv.setAdapter(adapter);
    }

    private String formatDateAndTime(String s) {

        String dateAndTime = s.substring(0,10)+" at "+s.substring(11,16);
        return dateAndTime;
    }


}
