package com.lisnrapp.ui.home.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lisnrapp.adapters.GenericAdapter;
import com.lisnrapp.databinding.FragmentHomeBinding;
import com.lisnrapp.model.CuponesModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView textView = binding.textViewHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        initDataCupones();
    }

    private void initDataCupones(){

        List<CuponesModel> dataCuponesList = new ArrayList<>();

        dataCuponesList.add(new CuponesModel(0,"Android Oreo", 0));
        dataCuponesList.add(new CuponesModel(1,"Android Pie", 1));
        dataCuponesList.add(new CuponesModel(2, "Android Nougat", 2));
        dataCuponesList.add(new CuponesModel(3,"Android Marshmallow", 3));

        GenericAdapter adapterCupones = new GenericAdapter(dataCuponesList, getContext());
        binding.recyclerCupones.setAdapter(adapterCupones);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}