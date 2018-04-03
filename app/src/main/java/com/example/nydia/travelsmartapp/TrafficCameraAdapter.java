package com.example.nydia.travelsmartapp;

import android.databinding.BindingAdapter;
import com.example.nydia.travelsmartapp.databinding.*;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nydia.travelsmartapp.models.TrafficCamera;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nydia on 4/2/18.
 */

public class TrafficCameraAdapter extends RecyclerView.Adapter<TrafficCameraAdapter.ViewHolder>{
    private List<TrafficCamera> trafficCameras = new ArrayList<>();
    private ViewDataBinding binding;

    public TrafficCameraAdapter(@NonNull List<TrafficCamera> trafficCameras) {
        this.trafficCameras = trafficCameras;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MovieListRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.movie_list_row, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrafficCamera trafficCamera = trafficCameras.get(position);
        holder.bind(trafficCamera);
    }

    @Override
    public int getItemCount() {
        return trafficCameras.size();
    }

    public void updateList(List<TrafficCamera> trafficCameras) {
        this.trafficCameras = trafficCameras;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MovieListRowBinding mBinding;
        public ViewHolder(MovieListRowBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(@NonNull TrafficCamera cameras) {
            mBinding.setVariable(BR.traffic_camera, cameras);
            mBinding.executePendingBindings();
        }
    }
}
