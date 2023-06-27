package com.lisnrapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.lisnrapp.databinding.ItemGenericBinding;
import com.lisnrapp.data.cupones.CuponesModel;

import java.util.List;

public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.MyViewHolder> {

    private final List<CuponesModel> cuponesModelList;

    private final Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ItemGenericBinding binding;

        public MyViewHolder(ItemGenericBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }
    }


    public GenericAdapter(List<CuponesModel> cuponesModelList, Context context) {
        this.cuponesModelList = cuponesModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemGenericBinding binding = ItemGenericBinding.inflate(inflater,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CuponesModel cupon = cuponesModelList.get(position);

        holder.binding.description.setText(cupon.getDescriptionCupon());

        if(cupon.isShowImageCupon() && cupon.getImageCupon() != 0){
            holder.binding.imageView.setVisibility(View.VISIBLE);
            holder.binding.imageView.setImageDrawable(AppCompatResources.getDrawable(context, cupon.getImageCupon()));
        }

        if(cupon.isShowButton()){
            holder.binding.imageButton.setVisibility(View.VISIBLE);
        }

        if(cupon.isShowImageLogo() && cupon.getImageLogo() != 0){
            holder.binding.description.setVisibility(View.GONE);
            holder.binding.imageViewLogo.setVisibility(View.VISIBLE);
            holder.binding.imageViewLogo.setImageDrawable(AppCompatResources.getDrawable(context, cupon.getImageLogo()));
        }
    }

    @Override
    public int getItemCount() {
        return cuponesModelList.size();
    }

}
