package com.example.ashen.csmanager.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashen.csmanager.R;
import com.example.ashen.csmanager.models.FuelStation;

import java.util.List;

public class FuelStationsAdapter extends RecyclerView.Adapter<FuelStationsAdapter.MyViewHolder>{

    private List<FuelStation> fuelStationList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name,city;

        public MyViewHolder(View view){
            super(view);
            name = (TextView)view.findViewById(R.id.name);
            city = (TextView)view.findViewById(R.id.city);
        }
    }

    public FuelStationsAdapter(List<FuelStation> fuelStationList){
        this.fuelStationList = fuelStationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fs_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FuelStation fuelStation = fuelStationList.get(position);
        holder.name.setText(fuelStation.getName());
        holder.city.setText(fuelStation.address.city);
    }

    @Override
    public int getItemCount() {
        return fuelStationList.size();
    }
}
