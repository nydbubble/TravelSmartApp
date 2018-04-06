package com.example.nydia.travelsmartapp.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.nydia.travelsmartapp.R;
import com.example.nydia.travelsmartapp.databinding.FragmentJourneyInfoBinding;
import com.example.nydia.travelsmartapp.databinding.TrafficIncidentsRowBinding;
import com.example.nydia.travelsmartapp.models.Leg;
import com.example.nydia.travelsmartapp.models.TrafficCamera;
import com.example.nydia.travelsmartapp.models.TrafficIncident;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JourneyInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JourneyInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Leg leg;

    private ArrayList<TrafficCamera> trafficCamera = new ArrayList<>();
    private RecyclerView trafficCameraRecyclerView;
    private RecyclerView trafficIncidentRecyclerView;

    private FragmentJourneyInfoBinding binding;
    private ArrayList<TrafficIncident> trafficIncident = new ArrayList<>();
    private ScrollView mainScrollView;

    //private OnFragmentInteractionListener mListener;

    public JourneyInfoFragment() {
        // Required empty public constructor
    }

    public static JourneyInfoFragment newInstance(Leg leg, List<TrafficCamera> trafficCamera, List<TrafficIncident> trafficIncident) {
        JourneyInfoFragment fragment = new JourneyInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("Leg", leg); //google maps stuff
        args.putParcelableArrayList("TrafficCamera", (ArrayList<? extends Parcelable>)trafficCamera);
        args.putParcelableArrayList("TrafficIncident", (ArrayList<? extends Parcelable>)trafficIncident);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trafficCamera = getArguments().getParcelableArrayList("TrafficCamera");
            leg = getArguments().getParcelable("Leg");
            trafficIncident = getArguments().getParcelableArrayList("TrafficIncident");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_journey_info, container, false);
        binding.setVariable(com.example.nydia.travelsmartapp.BR.leg, leg);
        View rootView = binding.getRoot();
        initTrafficIncidents(rootView);
        initTrafficCameras(rootView);
        mainScrollView = (ScrollView)rootView.findViewById(R.id.journeyinfo);

        return rootView;
    }

    private void initTrafficCameras(View rootView){
        trafficCameraRecyclerView = (RecyclerView) rootView.findViewById(R.id.traffic_cameras);
        trafficCameraRecyclerView.setNestedScrollingEnabled(false);
        TrafficCameraAdapter mAdapter = new TrafficCameraAdapter(trafficCamera);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        trafficCameraRecyclerView.setLayoutManager(mLayoutManager);
        trafficCameraRecyclerView.setAdapter(mAdapter);
        trafficCameraRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void initTrafficIncidents(View rootView){
        trafficIncidentRecyclerView = (RecyclerView) rootView.findViewById(R.id.traffic_incidents);
        trafficIncidentRecyclerView.setNestedScrollingEnabled(false);
        TrafficIncidentAdapter mAdapter = new TrafficIncidentAdapter(trafficIncident);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        trafficIncidentRecyclerView.setLayoutManager(mLayoutManager);
        trafficIncidentRecyclerView.setAdapter(mAdapter);
        trafficIncidentRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setTrafficCameras(ArrayList<TrafficCamera> trafficCameras){
        this.trafficCamera = trafficCameras;
        trafficCameraRecyclerView.getAdapter().notifyDataSetChanged();


        mainScrollView.smoothScrollTo(0,0);
    }

    public void setTrafficIncidents(ArrayList<TrafficIncident> trafficIncident){
        this.trafficIncident = trafficIncident;
        trafficIncidentRecyclerView.getAdapter().notifyDataSetChanged();


        mainScrollView.smoothScrollTo(0,0);
    }

    public void setLeg(Leg leg){
        binding.setVariable(com.example.nydia.travelsmartapp.BR.leg, leg);
    }


    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
