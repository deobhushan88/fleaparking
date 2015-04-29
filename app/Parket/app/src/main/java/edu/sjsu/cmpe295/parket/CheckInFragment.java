package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by bdeo on 4/26/15.
 */
public class CheckInFragment extends Fragment {

    CheckInListener callback;

    public interface CheckInListener {
        public void onCheckIn();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_checkin, container, false);

        Button button = (Button) fragmentView.findViewById(R.id.btn_check_in);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCheckIn();
            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (CheckInListener) activity;
    }
}