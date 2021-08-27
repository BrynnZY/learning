package com.example.paindiaryapplication;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiaryapplication.databinding.CombinedLineFragmentBinding;
import com.example.paindiaryapplication.entity.PainRecord;
import com.example.paindiaryapplication.viewmodel.PainViewModel;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class CombinedLineFragment extends Fragment {
    private CombinedLineFragmentBinding binding;
    private PainViewModel painViewModel;
    private String startDate = "";
    private String endDate = "";
    private LineChart mLineChart;
    private CombinedChart dataChart;
    private List<Float> chartWeather = new ArrayList<>();
    private List<Integer> chartPainLevel = new ArrayList<>();

    public CombinedLineFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CombinedLineFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        //spinner
        String[] spinnerItems = {"temperature", "humidity", "pressure"};
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(spinnerItems));
        final ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list);
        binding.spinner.setAdapter(spinnerAdapter);

        painViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(PainViewModel.class);
        binding.startEntering.setInputType(InputType.TYPE_NULL);
        binding.endEntering.setInputType(InputType.TYPE_NULL);
        binding.startEntering.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    showStartDatePickerDialog();
                }
            }
        });
        binding.startEntering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePickerDialog();
            }
        });

        binding.endEntering.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    showEndDatePickerDialog();
                }
            }
        });

        binding.endEntering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDatePickerDialog();
            }
        });
        binding.dateConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Float> weather = new ArrayList<>();
                List<Integer> painLevel = new ArrayList<>();
                if (startDate.equals("") || endDate.equals("")){
                    Toast.makeText(CombinedLineFragment.this.getActivity(),
                            "Start date or end date missing, please choose start date and end date", Toast.LENGTH_SHORT).show();
                }else {
                    if (startDate.compareTo(endDate) > 0){
                        Toast.makeText(CombinedLineFragment.this.getActivity(),
                                "End date invalid, it must be after the start date, please enter end date again", Toast.LENGTH_SHORT).show();
                    }else {
                        painViewModel.getPainRecords(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                .observe(CombinedLineFragment.this.getActivity(), new Observer<List<PainRecord>>() {
                                    @Override
                                    public void onChanged(List<PainRecord> painRecords) {
                                        if (!painRecords.isEmpty()){
                                            if (painRecords.get(0).entryDate.compareTo(endDate) < 0 ||
                                                    painRecords.get(painRecords.size()-1).entryDate.compareTo(startDate) > 0)
                                            {
                                                Toast.makeText(CombinedLineFragment.this.getActivity(),
                                                        "Date invalid, please select dates " +
                                                                "between " + painRecords.get(0).entryDate + " and " + painRecords.get(painRecords.size()-1).entryDate,
                                                        Toast.LENGTH_SHORT).show();
                                            }else {
                                                String choice = binding.spinner.getSelectedItem().toString();
                                                dataChart = (CombinedChart) binding.chart;
                                                CombinedData combinedData= new CombinedData();
                                                List<Entry> entries = new ArrayList<>();
                                                List<Entry> entries2 = new ArrayList<>();
                                                List<String> dates = new ArrayList<>();
                                                //List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                                                int j = 0;
                                                for (int i = painRecords.size() - 1; i >= 0; i--){
                                                    if (painRecords.get(i).entryDate.compareTo(startDate) >= 0 &&
                                                            painRecords.get(i).entryDate.compareTo(endDate) <= 0){
                                                        entries.add(new Entry(j, painRecords.get(i).painIntensityLevel));
                                                        painLevel.add(painRecords.get(i).painIntensityLevel);
                                                        float weatherData = 0;
                                                        switch (choice){
                                                            case "temperature":
                                                                weatherData = (float) painRecords.get(i).temperature;
                                                                break;
                                                            case "humidity":
                                                                weatherData = (float) painRecords.get(i).humidity;
                                                                break;
                                                            case "pressure":
                                                                weatherData = (float) painRecords.get(i).pressure;
                                                                break;
                                                        }
                                                        entries2.add(new Entry(j, weatherData));
                                                        weather.add(weatherData);
                                                        dates.add(painRecords.get(i).entryDate.substring(5));
                                                        j++;
                                                    }
                                                }
                                                LineDataSet dataSet = new LineDataSet(entries, "intensity level");
                                                dataSet.setColor(Color.BLACK);
                                                dataSet.setCircleColor(Color.BLACK);
                                                dataSet.setValueTextSize(9f);
                                                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                                LineDataSet dataSet2 = new LineDataSet(entries2, "weather");
                                                dataSet2.setColor(Color.GRAY);
                                                dataSet2.setCircleColor(Color.GRAY);
                                                dataSet2.setValueTextSize(9f);
                                                dataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);

                                                LineData lineData = new LineData();
                                                lineData.addDataSet(dataSet);
                                                LineData lineData2 = new LineData();
                                                lineData2.addDataSet(dataSet2);
                                                //combinedData.setData(lineData);
                                                lineData.addDataSet(lineData2.getDataSetByIndex(0));
                                                combinedData.setData(lineData);

                                                YAxis leftAxis = dataChart.getAxisLeft();
                                                leftAxis.setTextColor(Color.BLACK);
                                                YAxis rightAxis = dataChart.getAxisRight();
                                                rightAxis.setTextColor(Color.GRAY);

                                                Description description = new Description();
                                                description.setText("Weather and Pain Level in different dates");
                                                dataChart.setDescription(description);
                                                XAxis xAxis = dataChart.getXAxis();
                                                xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

                                                dataChart.setData(combinedData);
                                                dataChart.invalidate();
                                                chartWeather = weather;
                                                chartPainLevel  = painLevel;
                                                binding.correlationButton.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });

        binding.correlationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double[][] data = new double[chartPainLevel.size() + 3][2];
                for (int i = 0; i < chartPainLevel.size() && i < chartWeather.size(); i++){
                    data[i][0] = (double)chartPainLevel.get(i);
                    data[i][1] = (double)chartWeather.get(i);
                }
            double p = 0.0;
            double correlation = 1.0;
                RealMatrix m = MatrixUtils.createRealMatrix(data);
                PearsonsCorrelation pc = new PearsonsCorrelation(m);
                RealMatrix corM = pc.getCorrelationMatrix();
                RealMatrix pM = pc.getCorrelationPValues();
                p = pM.getEntry(0,1);
                correlation = corM.getEntry(0,1);
                binding.correlationMessage.setText("p value: " + p+ "\n" + " correlation: " +
                        correlation);
            }
        });
        return view;
    }

    private void showStartDatePickerDialog(){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(CombinedLineFragment.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate = year + "-" + dateStandard(month,dayOfMonth);
                binding.startEntering.setText(startDate);
            }
        }, c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void showEndDatePickerDialog(){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(CombinedLineFragment.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate = year + "-" + dateStandard(month,dayOfMonth);
                binding.endEntering.setText(endDate);
            }
        }, c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
    private String dateStandard(int month, int dayOfMonth){
        String sMonth;
        String sDay;
        if (month < 10){
            sMonth = "0" + (month + 1);
        }else{
            sMonth = Integer.toString(month + 1);
        }
        if (dayOfMonth < 10){
            sDay = "0" + (dayOfMonth);
        }else{
            sDay = Integer.toString(dayOfMonth);
        }
        return sMonth + "-" + sDay;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

