package com.example.nydia.travelsmartapp.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nydia.travelsmartapp.BR;
import com.example.nydia.travelsmartapp.R;
import com.example.nydia.travelsmartapp.databinding.CarparkRowBinding;
import com.example.nydia.travelsmartapp.models.Carpark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nydia on 4/6/18.
 */

public class CarparkAdapter extends RecyclerView.Adapter<CarparkAdapter.ViewHolder>{
    private ArrayList<Carpark> carparks;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Carpark item);
    }

    public CarparkAdapter(@NonNull ArrayList<Carpark> carparks, OnItemClickListener listener) {
        this.carparks = carparks;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CarparkRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.carpark_row, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Carpark carpark = carparks.get(position);
        holder.bind(carpark);
    }

    @Override
    public int getItemCount() {
        return carparks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private CarparkRowBinding mBinding;
        public ViewHolder(CarparkRowBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(@NonNull final Carpark carpark) {
            mBinding.setVariable(BR.carpark, carpark);
            mBinding.executePendingBindings();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(carpark);
                }
        });
    }
}
}
