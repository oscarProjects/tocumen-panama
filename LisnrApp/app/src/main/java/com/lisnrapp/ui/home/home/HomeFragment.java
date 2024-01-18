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

import com.lisnrapp.R;
import com.lisnrapp.adapters.GenericAdapter;
import com.lisnrapp.databinding.FragmentHomeBinding;
import com.lisnrapp.data.cupones.CuponesModel;

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
        initDataShops();
        initDataRestaurants();
        initDataInfo();
    }

    private void initDataInfo() {
        List<CuponesModel> dataInfoList = new ArrayList<>();

        dataInfoList.add(new CuponesModel(0,"Registro/Check In", 0, false, false, 0, false));
        dataInfoList.add(new CuponesModel(1,"Internet y Teléfonos", 0, false, false, 0, false));
        dataInfoList.add(new CuponesModel(2, "Área de Fumadores", 0, false, false, 0, false));
        dataInfoList.add(new CuponesModel(3,"Android Marshmallow", 0, false, false, 0, false));

        GenericAdapter adapterInfo = new GenericAdapter(dataInfoList, getContext());
        binding.recyclerInfo.setAdapter(adapterInfo);
    }

    private void initDataRestaurants() {
        List<CuponesModel> dataRestaurantsList = new ArrayList<>();

        dataRestaurantsList.add(new CuponesModel(0,"Subway", R.drawable.subway, true, false, R.drawable.subway_logo, true));
        dataRestaurantsList.add(new CuponesModel(1,"Burger King", R.drawable.donas, true, false, R.drawable.dunkindonuts_logo, true));
        dataRestaurantsList.add(new CuponesModel(2, "Mc Donalds", R.drawable.subway, true, false, R.drawable.subway_logo, true));
        dataRestaurantsList.add(new CuponesModel(3,"Android Marshmallow", R.drawable.donas, true, false, R.drawable.dunkindonuts_logo, true));

        GenericAdapter adapterRestaurants = new GenericAdapter(dataRestaurantsList, getContext());
        binding.recyclerRestaurants.setAdapter(adapterRestaurants);
    }

    private void initDataShops() {
        List<CuponesModel> dataCuponesList = new ArrayList<>();

        dataCuponesList.add(new CuponesModel(0,"Licores y Cigarrillos", 0, false, false, 0, false));
        dataCuponesList.add(new CuponesModel(1,"Cosméticos y Perfumes", 1, false, false, 0, false));
        dataCuponesList.add(new CuponesModel(2, "Modas", 2, false, false, 0, false));
        dataCuponesList.add(new CuponesModel(3,"Android Marshmallow", 3, false, false, 0, false));

        GenericAdapter adapterShops = new GenericAdapter(dataCuponesList, getContext());
        binding.recyclerShops.setAdapter(adapterShops);
    }

    private void initDataCupones(){

        List<CuponesModel> dataCuponesList = new ArrayList<>();

        dataCuponesList.add(new CuponesModel(0,"2x1 Pizzas Grandes", R.drawable.pizza, true, true, 0, false));
        dataCuponesList.add(new CuponesModel(1,"3x2 Pizzas Medianas", R.drawable.pizza, true, true, 0, false));
        dataCuponesList.add(new CuponesModel(2, "3x2 Pizzas Individuales", R.drawable.pizza, true, true, 0, false));
        dataCuponesList.add(new CuponesModel(3,"2x1 Pizzas Familiares", R.drawable.pizza, true, true, 0, false));

        GenericAdapter adapterCupones = new GenericAdapter(dataCuponesList, getContext());
        binding.recyclerCupones.setAdapter(adapterCupones);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}