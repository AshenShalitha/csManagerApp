package com.example.ashen.csmanager.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

public class Transactions extends AppCompatActivity {

    private List<PurchaseOrder> purchaseOrderList;
    private PurchaseOrder purchaseOrder;
    private RecyclerView rv;
    private String getdataUrl = ROOT_URL+"purchaseOrders/purchaseOrderByCustomer";
    private String token,customerId;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SessionManager sessionManager = new SessionManager(Transactions.this);
        token = sessionManager.getUserDetails().get("token");
        customerId = sessionManager.getUserDetails().get("id");

        loadingDialog = new ProgressDialog(Transactions.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Loading..");
        loadingDialog.show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Transactions.this,AddTransaction.class);
                startActivity(intent);
            }
        });

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        initializeData();
    }

    private void initializeData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getdataUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                purchaseOrderList = new ArrayList<PurchaseOrder>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
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
                    initializeAdapter();
                    loadingDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Transactions.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(Transactions.this).addToRequestQueue(stringRequest);

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
