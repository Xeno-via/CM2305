package com.example.cm2305;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class add_location extends Fragment {

    View view;

    EditText address;
    EditText country;
    Button addLocationBtn;
    TextView addLocationOutput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_location, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get all Layout Objects
        address = view.findViewById(R.id.add_location_address_field);
        country = view.findViewById(R.id.add_location_country_field);
        addLocationBtn = view.findViewById(R.id.add_location_btn);
        addLocationOutput = view.findViewById(R.id.add_location_output_text);

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formatData();
            }
        });
    }

    // Format data in a map
    private Map<String, String> formatData(){
        if (allFieldsFull()){
            Map<String, String> output = new HashMap<>();
            output.put("address", trimmedString(address));
            output.put("country", trimmedString(country));
            addLocationOutput.setText("Added location located in: "+trimmedString(country).toUpperCase()+"!");
            return output;
        }
        addLocationOutput.setText("Fill all necessary fields!");
        return null;
    }

    // Check if all required fields are full
    private boolean allFieldsFull(){
        return !trimmedString(address).equals("") && !trimmedString(country).equals("");
    }

    // Output a trimmed string to remove unnecessary whitespace
    private String trimmedString(EditText editText){
        return editText.getText().toString().trim();
    }
}