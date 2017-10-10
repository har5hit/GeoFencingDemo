package com.justadeveloper96.mapsfencing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ConfigurationDialog extends BottomSheetDialogFragment {

    @BindView(R.id.tv_meters)
    TextView tvMeters;
    @BindView(R.id.btn_toggle)
    Button btnToggle;
    Unbinder unbinder;
    @BindView(R.id.sb_meters)
    AppCompatSeekBar sbMeters;
    @BindView(R.id.btn_close)
    Button btnClose;
    private OnFragmentInteractionListener mListener;
    private static final String KEY_STARTED = "STARTED";
    private static final String KEY_METERS = "METERS";
    Boolean active;
    float current_meters,seeker_meters;

    public ConfigurationDialog() {
        // Required empty public constructor
    }

    public static ConfigurationDialog newInstance(boolean already_started, float meters) {

        Bundle args = new Bundle();
        args.putBoolean(KEY_STARTED, already_started);
        args.putFloat(KEY_METERS, meters);
        ConfigurationDialog fragment = new ConfigurationDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configuration_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String meters = "Meters: ";
        active = getArguments().getBoolean(KEY_STARTED);
        current_meters = getArguments().getFloat(KEY_METERS,0);
        btnToggle.setText(active?"Stop":"Start");
        sbMeters.setEnabled(!active);

        if (current_meters<1)
        {
            sbMeters.setProgress((int) current_meters);
            current_meters=current_meters+50;
        }else {
            sbMeters.setProgress((int) (current_meters-50));
        }
        tvMeters.setText(meters+(int)(current_meters));


        sbMeters.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                current_meters = i+50;
                tvMeters.setText(meters+(int)current_meters);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = !active;
                toggleState(active);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigurationDialog.this.dismiss();
            }
        });
    }

    public void toggleState(boolean start) {
        if (start) {
            btnToggle.setText("Stop");
            sbMeters.setEnabled(false);
            mListener.onFragmentInteraction(start,current_meters);
        } else {
            btnToggle.setText("Start");
            sbMeters.setEnabled(true);
            mListener.onFragmentInteraction(start,current_meters);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(boolean start, float meters);
    }
}
