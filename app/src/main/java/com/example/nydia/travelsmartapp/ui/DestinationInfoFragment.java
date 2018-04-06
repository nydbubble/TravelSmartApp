package com.example.nydia.travelsmartapp.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nydia.travelsmartapp.R;
import com.example.nydia.travelsmartapp.models.Carpark;
import com.example.nydia.travelsmartapp.models.Forecast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DestinationInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DestinationInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationInfoFragment extends Fragment {
    private Forecast forecast;
    private ViewDataBinding binding;
    private RecyclerView carparkRecyclerView;
    private ScrollView mainScrollView;
    private ArrayList<Carpark> availableCarparks = new ArrayList<>();
    private TextView psiTextView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    

    //private OnFragmentInteractionListener mListener;

    public DestinationInfoFragment() {
        // Required empty public constructor
    }
    
    public static DestinationInfoFragment newInstance(Forecast forecast, List<Carpark> carparks) {
        DestinationInfoFragment fragment = new DestinationInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("Weather Forecast", forecast);
        args.putParcelableArrayList("Carpark", (ArrayList<? extends Parcelable>)carparks);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forecast = getArguments().getParcelable("Weather Forecast");
            //availableCarparks = getArguments().getParcelable("Carpark");
            //availableCarparks.add(new Carpark());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_destination_info, container, false);
        View rootView = binding.getRoot();
        psiTextView = (TextView)rootView.findViewById(R.id.psi);
        initCarpark(rootView);

        mainScrollView = (ScrollView)rootView.findViewById(R.id.destinationinfo);
        return rootView;
    }

    private void initCarpark(View rootView){
        carparkRecyclerView = (RecyclerView) rootView.findViewById(R.id.carparks);
        carparkRecyclerView.setNestedScrollingEnabled(false);
        CarparkAdapter mAdapter = new CarparkAdapter(availableCarparks, new CarparkAdapter.OnItemClickListener() {
            @Override public void onItemClick(Carpark item) {
                Toast.makeText(getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        carparkRecyclerView.setLayoutManager(mLayoutManager);
        carparkRecyclerView.setAdapter(mAdapter);
        carparkRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setCarparks(ArrayList<Carpark> carparks){
        availableCarparks.clear();
        availableCarparks.addAll(carparks);
        carparkRecyclerView.getAdapter().notifyDataSetChanged();

        mainScrollView.smoothScrollTo(0,0);
    }

    public void setForecast(Forecast forecast){
        binding.setVariable(com.example.nydia.travelsmartapp.BR.forecast, forecast);
    }

    public void setPsi(String psi) {
        psiTextView.setText(psi);
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
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
