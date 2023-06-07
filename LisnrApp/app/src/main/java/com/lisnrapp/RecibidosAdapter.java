package com.lisnrapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

    public class RecibidosAdapter extends RecyclerView.Adapter<RecibidosAdapter.ViewHolderRecibidos> {


    ArrayList<Recibidos> recibidos;
    private ItemClickListener mItemListener;

        public RecibidosAdapter(ArrayList<Recibidos> recibidos, Context context, ItemClickListener itemClickListener) {
        this.recibidos = recibidos;
        this.context = context;
        this.mItemListener = itemClickListener;
    }
    private Context context;


        @NonNull
    @Override
    public RecibidosAdapter.ViewHolderRecibidos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_recibidos,parent,false);
        return new ViewHolderRecibidos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecibidosAdapter.ViewHolderRecibidos holder, int position) {
        holder.tvId.setText(recibidos.get(position).getId());
        holder.tvTitle.setText(recibidos.get(position).getTitulo());
        holder.tvHour.setText(recibidos.get(position).getHour());
        holder.itemView.setOnClickListener(v -> {
            mItemListener.onItemClick(recibidos.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return recibidos.size();
    }

    public interface ItemClickListener {
        void onItemClick(Recibidos recibidos);
    }
    public class ViewHolderRecibidos extends RecyclerView.ViewHolder {
        private TextView tvId;
        private TextView tvTitle;
        private TextView tvHour;

        public ViewHolderRecibidos(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvHour = itemView.findViewById(R.id.tvHour);

        }
    }
}
