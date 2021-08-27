package com.example.navigationjava.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.navigationjava.databinding.HomeFragmentBinding;
import com.example.navigationjava.viewmodel.SharedViewModel;

public class HomeFragment extends Fragment {
    private SharedViewModel model;
    private HomeFragmentBinding addBinding;

    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addBinding = HomeFragmentBinding.inflate(inflater,container,false);
        View view = addBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
    }
}
