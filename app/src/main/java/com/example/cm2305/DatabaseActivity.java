package com.example.cm2305;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DatabaseActivity extends AppCompatActivity {

    Button accountFragmentBtn, contactFragmentBtn, locationFragmentBtn;
    RadioButton addFragmentRadio, viewFragmentRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        // Get view buttons
        accountFragmentBtn = findViewById(R.id.account_fragment_btn);
        contactFragmentBtn = findViewById(R.id.contact_fragment_btn);
        locationFragmentBtn = findViewById(R.id.location_fragment_btn);
        addFragmentRadio = findViewById(R.id.add_fragment_radio);
        viewFragmentRadio = findViewById(R.id.view_fragment_radio);

        // Pressing the Account Button
        accountFragmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFragmentRadio.isChecked()) {
                    replaceFragment(new add_account());
                } else {replaceFragment(new view_account());}
            }
        });

        // Pressing the Contact Button
        contactFragmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFragmentRadio.isChecked()) {
                    replaceFragment(new add_contact());
                } else {replaceFragment(new view_contact());}
            }
        });

        // Pressing the Location Button
        locationFragmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFragmentRadio.isChecked()) {
                    replaceFragment(new add_location());
                } else {replaceFragment(new view_location());}
            }
        });

    }

    // Replace fragment when one of the buttons is pressed.
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}
