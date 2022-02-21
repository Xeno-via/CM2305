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

public class add_account extends Fragment {

    View view;
    EditText firstName, lastName, password, confirmPassword, mainContact;
    Button addAccountBtn;
    TextView addAccountOutput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_account, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get all Layout Objects
        firstName = view.findViewById(R.id.add_account_first_name_field);
        lastName = view.findViewById(R.id.add_account_last_name_field);
        password = view.findViewById(R.id.add_account_password_field);
        confirmPassword = view.findViewById(R.id.add_account_confirm_password_field);
        mainContact = view.findViewById(R.id.add_account_main_phone_field);
        addAccountBtn = view.findViewById(R.id.add_account_btn);
        addAccountOutput = view.findViewById(R.id.add_account_output_text);

        addAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formatData();
            }
        });
    }

    // Format data in a map
    private Map<String, String> formatData(){
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();
        if (passwordString.equals(confirmPasswordString) && allFieldsFull()) {
            Map<String, String> output = new HashMap<>();
            output.put("firstName", trimmedString(firstName));
            output.put("lastName", trimmedString(lastName));
            output.put("passwordHash", hashPassword(password));
            output.put("mainContact", trimmedString(mainContact));
            addAccountOutput.setText("Added Account: "
                    +trimmedString(firstName)+" "+trimmedString(lastName)+"!");
            return output;
        } else if (!allFieldsFull()) {
            addAccountOutput.setText("Fill all necessary fields!");
        } else {
            addAccountOutput.setText("Passwords do not match!");
        }

        return null;

    }

    private String hashPassword(EditText password){
        // Temporary whilst there is no hash process
        return password.getText().toString();
    }

    // Check if all required fields are full
    private boolean allFieldsFull(){
        boolean namesDone = !trimmedString(firstName).equals("") && !trimmedString(lastName).equals("");
        boolean passwordsDone = !trimmedString(password).equals("") && !trimmedString(confirmPassword).equals("");
        boolean mainContactDone = !trimmedString(mainContact).equals("");
        return namesDone && passwordsDone && mainContactDone;
    }
    // Output a trimmed string to remove unnecessary whitespace
    private String trimmedString(EditText editText){
        return editText.getText().toString().trim();
    }
}