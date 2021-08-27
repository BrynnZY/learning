package com.example.fragmentjava;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.fragmentjava.databinding.ViewFragmentBinding;

public class ViewFragment extends Fragment {
    private ViewFragmentBinding binding;

    public ViewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ViewFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

//        SharedPreferences sharedPref = requireActivity().getSharedPreferences("Message", Context.MODE_PRIVATE);
//        String message = sharedPref.getString("message",null);
//        binding.textMessage.setText("Message from AddFragment: "+ message);

        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.textMessage.setText(s);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
