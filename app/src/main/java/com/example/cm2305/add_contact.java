package com.example.cm2305;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class add_contact extends Fragment {

    View view;
    EditText firstName, lastName, phoneNumber;
    RadioButton textRadio, callRadio, videoRadio;
    Button addContactBtn;
    TextView addContactOutput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        // Get all Layout Objects
        firstName = view.findViewById(R.id.add_contact_first_name_field);
        lastName = view.findViewById(R.id.add_contact_last_name_field);
        phoneNumber = view.findViewById(R.id.add_contact_phone_field);
        textRadio = view.findViewById(R.id.text_method_radio);
        callRadio = view.findViewById(R.id.call_method_radio);
        videoRadio = view.findViewById(R.id.video_method_radio);
        addContactBtn = view.findViewById(R.id.add_contact_btn);
        addContactOutput = view.findViewById(R.id.add_contact_output_text);

        addContactBtn.setOnClickListener(new View.OnClickListener() {
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
            output.put("firstName", trimmedString(firstName));
            output.put("lastName", trimmedString(lastName));
            output.put("phone", trimmedString(phoneNumber));
            output.put("method", methodOfChoice());
            addContactOutput.setText("Added Contact: "
                    +trimmedString(firstName)+" "+trimmedString(lastName)+ "!");
            return output;
        }
        addContactOutput.setText("Fill all necessary fields!");
        return null;
    }

    // Check if all required fields are full
    private boolean allFieldsFull(){
        boolean namesDone = !trimmedString(firstName).equals("") && !trimmedString(lastName).equals("");
        boolean phoneDone = !trimmedString(phoneNumber).equals("");
        return namesDone && phoneDone;
    }

    // Output a trimmed string to remove unnecessary whitespace
    private String trimmedString(EditText editText){
        return editText.getText().toString().trim();
    }

    private String methodOfChoice(){
        if (textRadio.isChecked()){
            return "text";
        } else if (callRadio.isChecked()) {
            return "call";
        } else if (videoRadio.isChecked()){
            return "video";
        }
        return null;
    }
}