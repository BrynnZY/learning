package com.example.paindiaryapplication;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiaryapplication.databinding.DailyRecordsFragmentBinding;
import com.example.paindiaryapplication.databinding.MapFragmentBinding;
import com.example.paindiaryapplication.viewmodel.MapSettingViewModel;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {
    private MapFragmentBinding mapBinding;

    public MapFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(MapFragment.this.getActivity(), getString(R.string.access_token));
        mapBinding = MapFragmentBinding.inflate(inflater, container, false);
        View view = mapBinding.getRoot();
        MapSettingViewModel mapSettingViewModel = new ViewModelProvider(requireActivity()).get(MapSettingViewModel.class);
        mapBinding.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        SymbolManager symbolManager = new SymbolManager(mapBinding.mapView, mapboxMap, style);
                        style.addImage("marker-image",
                                BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.mapbox_marker_icon_default)));
                        SymbolOptions symbolOptions = new SymbolOptions().withIconImage("marker-image").withIconOffset(new Float[]{0f,10f});
                        mapSettingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                String latLongs[] = s.split(" ");
                                LatLng latLng = new LatLng(Double.parseDouble(latLongs[0]), Double.parseDouble(latLongs[1]));
                                mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                symbolOptions.withLatLng(latLng);
                                symbolManager.create(symbolOptions);
                            }
                        });
                    }
                });
            }
        });

        mapBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(query, 5);
                    if (addressList != null && addressList.size() >0){
                        mapSettingViewModel.setLatLong(addressList.get(0).getLatitude() + " " + addressList.get(0).getLongitude());
                    }else {
                        Toast.makeText(MapFragment.this.getActivity(), "No matching location", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mapBinding.listView.setVisibility(View.GONE);
                mapBinding.mapView.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(newText, 5);
                    if (addressList != null && addressList.size() >0){
                        List<String> addresses = new ArrayList<>();
                        for(Address address: addressList){
                            addresses.add(address.getAddressLine(0) + " " + address.getAddressLine(1) + " " +
                                    address.getLocality() + " "  + address.getAdminArea() + " "  + address.getCountryName());
                        }
                        mapBinding.listView.setAdapter(new ArrayAdapter<String>(MapFragment.this.getActivity(),
                                android.R.layout.simple_list_item_1,addresses));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mapBinding.mapView.setVisibility(View.GONE);
                mapBinding.listView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mapBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mapBinding.searchView.setQuery(mapBinding.listView.getItemAtPosition(position).toString(), true);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapBinding = null;
    }
}
