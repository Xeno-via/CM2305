package com.example.cm2305;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class view_contact extends Fragment {

    View view;
    EditText firstName, lastName, phone;
    Button viewContactBtn;
    TextView viewContactOutput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_contact, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        firstName = view.findViewById(R.id.view_contact_first_name_field);
        lastName = view.findViewById(R.id.view_contact_last_name_field);
        phone = view.findViewById(R.id.view_contact_phone_field);
        viewContactBtn = view.findViewById(R.id.view_account_btn);
        viewContactOutput = view.findViewById(R.id.view_contact_output_text);

        viewContactBtn.setOnClickListener(new View.OnClickListener() {
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
            viewContactOutput.setText("QUERY DATABASE");
        } else {viewContactOutput.setText("Fill in atleast one field!");}
    }

    // Check if all entries are empty
    private boolean allEntriesEmpty(){
        return trimmedString(firstName).equals("") && trimmedString(lastName).equals("")
                && trimmedString(phone).equals("");
    }

    // Output a trimmed string
    private String trimmedString(EditText editText){
        return editText.getText().toString().trim();
    }
}