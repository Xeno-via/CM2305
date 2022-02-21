package com.example.cm2305;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class view_account extends Fragment {

    View view;
    EditText firstName, lastName;
    Button viewAccountBtn;
    TextView viewAccountOutput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_account, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        firstName = view.findViewById(R.id.view_account_first_name_field);
        lastName = view.findViewById(R.id.view_account_last_name_field);
        viewAccountBtn = view.findViewById(R.id.view_account_btn);
        viewAccountOutput = view.findViewById(R.id.view_account_output_text);

        viewAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryResults();
            }
        });
    }

    // Query Database
    private void queryResults(){
        if (!allEntriesEmpty()){
            // Query Database somehow
            viewAccountOutput.setText("QUERY DATABASE");
        } else {viewAccountOutput.setText("Fill in atleast one field!");}
    }

    // Check if all entries are empty
    private boolean allEntriesEmpty(){
        return trimmedString(firstName).equals("") && trimmedString(lastName).equals("");
    }

    // Output a trimmed string
    private String trimmedString(EditText editText){
        return editText.getText().toString().trim();
    }
}