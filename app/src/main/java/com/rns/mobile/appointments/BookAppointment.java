package com.rns.mobile.appointments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookAppointment extends AppCompatActivity {
    Button book;
    Spinner from,to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        book=(Button)findViewById(R.id.btnbook);
        from=(Spinner)findViewById(R.id.fromspinner);
        to=(Spinner)findViewById(R.id.tospinner);
        List<String> list = new ArrayList<String>();
        list.add("10");
        list.add("11");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<String>();
        list2.add("11");
        list2.add("12");
        list2.add("1");
        list2.add("2");
        list2.add("3");
        list2.add("4");
        list2.add("5");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to.setAdapter(dataAdapter2);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookAppointment.this,"Appoiontment your book from "+ from.getSelectedItem().toString()+" to "+ to.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
