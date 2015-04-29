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
public class CheckOutFragment extends Fragment {

    CheckOutListener callback;

    public interface CheckOutListener {
        public void onCheckOut();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_checkout, container, false);

        Button button = (Button) fragmentView.findViewById(R.id.btn_check_out);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCheckOut();
            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (CheckOutListener) activity;
    }
}