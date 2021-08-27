package com.example.paindiaryapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiaryapplication.databinding.PiechartFragmentBinding;
import com.example.paindiaryapplication.entity.PainRecord;
import com.example.paindiaryapplication.viewmodel.PainViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PieChartFragment extends Fragment {
    private PiechartFragmentBinding binding;
    private PainViewModel painViewModel;
    private PieChart pieChart;

    public PieChartFragment(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PiechartFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        painViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(PainViewModel.class);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        painViewModel.getLocationCount(email).observe(this.getActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if (!strings.isEmpty())
                {
                    pieChart = (PieChart) binding.pieChart;
                    List<PieEntry> entry = new ArrayList<>();
                    for (String string: strings){
                        String[] temp;
                        temp = string.split(" ");
                        entry.add(new PieEntry(Float.parseFloat(temp[1]),temp[0]));
                    }
                    PieDataSet dataSet = new PieDataSet(entry, "Pain Location");
                    ArrayList<Integer> colors = new ArrayList<Integer>();
                    colors.add(Color.parseColor("#6785f2"));
                    colors.add(Color.parseColor("#675cf2"));
                    colors.add(Color.parseColor("#496cef"));
                    colors.add(Color.parseColor("#aa63fa"));
                    colors.add(Color.parseColor("#f5a658"));
                    dataSet.setColors(colors);
                    PieData pieData = new PieData(dataSet);
                    pieData.setDrawValues(true);
                    pieData.setValueTextSize(20f);

                    pieChart.setData(pieData);
                    Description d = new Description();
                    d.setText("Pain Location Count");
                    pieChart.setDescription(d);
                    pieChart.setHoleRadius(0f);
                    pieChart.setTransparentCircleRadius(0f);
                    pieChart.invalidate();
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
