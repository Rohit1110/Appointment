package com.rns.mobile.appointments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import adapter.Appointment_Adapter;
import model.Appointment;

public class Main2Activity extends AppCompatActivity {
    RecyclerView appointmentlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList list=new ArrayList();

        Appointment appointment=new Appointment();
        for(int i=0; i<5; i++) {
            appointment.setName("Rohit"+i);
            appointment.setTime("11-12 am");
            appointment.setPhone("123456789");
            list.add(appointment);
        }



        System.out.println("Length"+list.size());
        Appointment_Adapter adapter=new Appointment_Adapter(Main2Activity.this,list);
        appointmentlist.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(Main2Activity.this,SearchAppointment.class);
               startActivity(intent);
            }
        });
    }

}
