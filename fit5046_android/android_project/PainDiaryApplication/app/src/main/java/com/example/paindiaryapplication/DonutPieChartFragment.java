package com.example.paindiaryapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiaryapplication.databinding.DonutpiechartFragmentBinding;
import com.example.paindiaryapplication.entity.PainRecord;
import com.example.paindiaryapplication.viewmodel.PainViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DonutPieChartFragment  extends Fragment {
    private DonutpiechartFragmentBinding binding;
    private PainViewModel painViewModel;
    private PieChart pieChart;

    public DonutPieChartFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DonutpiechartFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        painViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(PainViewModel.class);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //current date
        Date date= new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String entryDate = df.format(date);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<PainRecord> painRecordCompletableFuture =
                    painViewModel.findByDateEmailFuture(email, entryDate);
            painRecordCompletableFuture.thenApply(painRecord -> {
                //already has record vs not has record
                if (painRecord != null) {
                    pieChart = (PieChart) binding.pieChart;
                    List<PieEntry> entry = new ArrayList<>();
                    entry.add(new PieEntry(painRecord.stepsTaken,"Steps Taken"));
                    entry.add(new PieEntry(
                            painRecord.goalSetting > painRecord.stepsTaken ?
                                    (painRecord.goalSetting - painRecord.stepsTaken) : 0,
                            "Steps Remaining"));
                    PieDataSet dataSet = new PieDataSet(entry, "Stemps Taken/Remaining");
                    ArrayList<Integer> colors = new ArrayList<Integer>();
                    colors.add(Color.parseColor("#f5a658"));
                    colors.add(Color.parseColor("#6785f2"));
                    dataSet.setColors(colors);
                    PieData pieData = new PieData(dataSet);
                    pieData.setDrawValues(true);
                    pieData.setValueTextSize(20f);

                    pieChart.setData(pieData);
                    Description d = new Description();
                    d.setText("Steps taken today");
                    pieChart.setDescription(d);
                    pieChart.invalidate();
                }
                else{
                    Toast.makeText(DonutPieChartFragment.this.getActivity(),
                            "No data in the database", Toast.LENGTH_SHORT).show();
                }
                return painRecord;
            });
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
