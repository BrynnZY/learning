package com.example.paindiaryapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindiaryapplication.databinding.RvLayoutBinding;
import com.example.paindiaryapplication.entity.PainRecord;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter
        <RecyclerViewAdapter.ViewHolder> {
   private List<PainRecord> painRecords = new ArrayList<>();

   public void setData(List<PainRecord> painRecords){
       this.painRecords = painRecords;
       notifyDataSetChanged();
   }
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvLayoutBinding binding = RvLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
       PainRecord painRecord = painRecords.get(position);
       holder.binding.date.setText(painRecord.entryDate);
       holder.binding.temp.setText("Temperature: " + String.format("%.2f", painRecord.temperature) + "℃");
       holder.binding.humidity.setText("Humidity: " + painRecord.humidity + "%");
       holder.binding.pressure.setText("Pressure: " + painRecord.pressure + "hPa");
       holder.binding.goal.setText("Daily Goal: " + painRecord.goalSetting);
       holder.binding.steps.setText("Steps Token: " + painRecord.stepsTaken);
       holder.binding.location.setText("Location: " + painRecord.painLocation);
       holder.binding.level.setText("Intensity Level: " + painRecord.painIntensityLevel);
       holder.binding.mode.setText("Mode: " + painRecord.painModeLevel);
    }

    @Override
    public int getItemCount() {
        return painRecords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       private RvLayoutBinding binding;
       public ViewHolder(RvLayoutBinding binding){
           super(binding.getRoot());
           this.binding = binding;
       }
    }
}
