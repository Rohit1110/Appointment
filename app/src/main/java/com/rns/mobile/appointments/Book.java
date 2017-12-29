package com.rns.mobile.appointments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Book extends AppCompatActivity {
    Spinner from,to;
    private static final String[] fromtime = new String[] {
            "Select From",  "10", "11", "12", "1", "2","3","4","5","6"
    };
    private static final String[] totime = new String[] {
            "Select to",  "10.30", "11.30", "12.30", "1.30", "2.30","3.30","4.30","5.30","6.30"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        from=(Spinner)findViewById(R.id.fromtime);
        to=(Spinner)findViewById(R.id.totime);

      ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, fromtime);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from.setAdapter(dataAdapter);



        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, fromtime);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to.setAdapter(dataAdapter2);
    }
}
