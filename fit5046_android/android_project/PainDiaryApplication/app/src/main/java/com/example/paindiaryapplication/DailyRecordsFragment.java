package com.example.paindiaryapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindiaryapplication.adapter.RecyclerViewAdapter;
import com.example.paindiaryapplication.databinding.DailyRecordsFragmentBinding;
import com.example.paindiaryapplication.databinding.DataEntryFragmentBinding;
import com.example.paindiaryapplication.entity.PainRecord;
import com.example.paindiaryapplication.viewmodel.PainViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DailyRecordsFragment extends Fragment {
    private DailyRecordsFragmentBinding recordBinding;
    private RecyclerViewAdapter adapter;
    private PainViewModel painViewModel;
    private String email;
    private RecyclerView.LayoutManager layoutManager;
    private List<PainRecord> painRecords;

    public DailyRecordsFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recordBinding = DailyRecordsFragmentBinding.inflate(inflater, container, false);
        View view = recordBinding.getRoot();

        //email
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //recyclerview
        adapter = new RecyclerViewAdapter();
        recordBinding.recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(),
                LinearLayoutManager.VERTICAL));
        recordBinding.recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recordBinding.recyclerView.setLayoutManager(layoutManager);

        //viewmodel
        painViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(PainViewModel.class);
        painViewModel.getPainRecords(email).observe(this.getActivity(), new Observer<List<PainRecord>>() {
            @Override
            public void onChanged(List<PainRecord> painRecords) {
                if (!painRecords.isEmpty()){
                    adapter.setData(painRecords);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recordBinding = null;
    }
}
