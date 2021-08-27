package com.example.paindiaryapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.paindiaryapplication.databinding.MapFragmentBinding;
import com.example.paindiaryapplication.databinding.ReportFragmentBinding;

public class ReportFragment extends Fragment {
    private ReportFragmentBinding reportBinding;

    public ReportFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        reportBinding = ReportFragmentBinding.inflate(inflater, container, false);
        View view = reportBinding.getRoot();

        reportBinding.locationPieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new PieChartFragment());
            }
        });
        reportBinding.StepsPieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new DonutPieChartFragment());
            }
        });
        reportBinding.painWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new CombinedLineFragment());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment nextFragment){
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container_view, nextFragment);
        transaction.commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportBinding = null;
    }
}
