package com.example.ashen.csmanager.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashen.csmanager.R;
import com.example.ashen.csmanager.models.PurchaseOrder;

import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.PurchaseOrderViewHolder>{

    public static class PurchaseOrderViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        TextView vehicleNameTV;
        TextView vehicleNumberTV;
        TextView customerNameTV;
        TextView addedByTV;
        TextView litresTV;
        TextView fuelTypeTV;
        TextView priceTV;
        TextView dateAndTimeTV;
        TextView fuelStationTV;

        PurchaseOrderViewHolder(View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            vehicleNameTV = (TextView)itemView.findViewById(R.id.vehicle);
            vehicleNumberTV = (TextView)itemView.findViewById(R.id.vehicleNo);
            customerNameTV = (TextView)itemView.findViewById(R.id.customer);
            addedByTV = (TextView)itemView.findViewById(R.id.addedBy);
            litresTV = (TextView)itemView.findViewById(R.id.litres);
            fuelTypeTV = (TextView)itemView.findViewById(R.id.fuelType);
            priceTV = (TextView)itemView.findViewById(R.id.price);
            dateAndTimeTV = (TextView)itemView.findViewById(R.id.dateAndTime);
            fuelStationTV = (TextView)itemView.findViewById(R.id.fuelStation);
        }

    }

    List<PurchaseOrder>  purchaseOrderList;

    public RvAdapter(List<PurchaseOrder> purchaseOrderList){
        this.purchaseOrderList = purchaseOrderList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PurchaseOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item2, parent, false);
        PurchaseOrderViewHolder purchaseOrderViewHolder = new PurchaseOrderViewHolder(v);
        return purchaseOrderViewHolder;
    }

    @Override
    public void onBindViewHolder(PurchaseOrderViewHolder holder, int position) {
        holder.vehicleNameTV.setText(purchaseOrderList.get(position).getVehicle());
        holder.vehicleNumberTV.setText(purchaseOrderList.get(position).getVehicleNumber());
        holder.customerNameTV.setText(purchaseOrderList.get(position).getCustomer());
        holder.addedByTV.setText(purchaseOrderList.get(position).getEnteredBy());
        holder.litresTV.setText(purchaseOrderList.get(position).getLitres());
        holder.fuelTypeTV.setText(purchaseOrderList.get(position).getFuelType());
        holder.priceTV.setText(purchaseOrderList.get(position).getPrice());
        holder.dateAndTimeTV.setText(purchaseOrderList.get(position).getCreatedAt());
        holder.fuelStationTV.setText(purchaseOrderList.get(position).getFuelStation());
    }

    @Override
    public int getItemCount() {
        return purchaseOrderList.size();
    }

}
