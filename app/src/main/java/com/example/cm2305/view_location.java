package com.example.cm2305;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class view_location extends Fragment {

    View view;

    EditText address, country;
    Button viewLocationBtn;
    TextView viewLocationOutput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_location, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        address = view.findViewById(R.id.view_location_address_field);
        country = view.findViewById(R.id.view_location_country_field);
        viewLocationBtn = view.findViewById(R.id.view_location_btn);
        viewLocationOutput = view.findViewById(R.id.view_location_output_text);

        viewLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryResults();
            }
        });
    }

    private void queryResults(){
        if (!allEntriesEmpty()){
            // Query Database somehow
            viewLocationOutput.setText("QUERY DATABASE");
        } else {viewLocationOutput.setText("Fill in atleast one field!");}
    }

    // Check if all entries are empty
    private boolean allEntriesEmpty(){
        return trimmedString(address).equals("") && trimmedString(country).equals("");
    }

    // Output a trimmed string
    private String trimmedString(EditText editText){
        return editText.getText().toString().trim();
    }
}