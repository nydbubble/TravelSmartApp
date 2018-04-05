package com.example.nydia.travelsmartapp.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.nydia.travelsmartapp.BR;
import com.example.nydia.travelsmartapp.R;
import com.example.nydia.travelsmartapp.databinding.TrafficIncidentsRowBinding;
import com.example.nydia.travelsmartapp.models.TrafficIncident;

import java.util.List;

/**
 * Created by nydia on 4/5/18.
 */

public class TrafficIncidentAdapter extends RecyclerView.Adapter<TrafficIncidentAdapter.ViewHolder> {
    private List<TrafficIncident> trafficIncidents;

    public TrafficIncidentAdapter(@NonNull List<TrafficIncident> trafficIncidents) {
        this.trafficIncidents = trafficIncidents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TrafficIncidentsRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.traffic_incidents_row, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrafficIncident trafficIncident = trafficIncidents.get(position);
        holder.bind(trafficIncident);
    }

    @Override
    public int getItemCount() {
        return trafficIncidents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TrafficIncidentsRowBinding mBinding;
        public ViewHolder(TrafficIncidentsRowBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(@NonNull TrafficIncident incidents) {
            mBinding.setVariable(BR.traffic_incident, incidents);
            mBinding.executePendingBindings();
        }
    }
}
