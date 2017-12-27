package com.rns.mobile.appointments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class SearchAppointment extends AppCompatActivity {
Button next;
AutoCompleteTextView search;
    private static final String[] COUNTRIES = new String[] {
            "Dentist", "Aurtho", "homeopathi", "entc", "Aurvedik"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appointment);
        next=(Button)findViewById(R.id.btnnxt);
        search=(AutoCompleteTextView)findViewById(R.id.editsearch);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        search.setAdapter(adapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SearchAppointment.this,BookAppointment.class);
                startActivity(intent);
            }
        });

    }
}
