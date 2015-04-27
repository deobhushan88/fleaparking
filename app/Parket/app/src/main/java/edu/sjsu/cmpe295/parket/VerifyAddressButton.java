package edu.sjsu.cmpe295.parket;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyAddressButton extends Fragment {

    public VerifyAddressButton() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_address_button, container, false);
        Button cancelBtn = (Button) view.findViewById(R.id.cancelButton);
        Button verifyBtn = (Button) view.findViewById(R.id.verifyButton);

        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("cancelButton", "onClick");
                Intent i = new Intent(getActivity().getApplicationContext(), AddParkingSpace.class);
                i.putExtra("FROM_ACTIVITY","VerifyAddressButton");
                startActivity(i);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("VerifyAddressBtn", "onClick");
                Intent i = new Intent(getActivity().getApplicationContext(), RentParkingSpace.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });
        return view;


    }

}
