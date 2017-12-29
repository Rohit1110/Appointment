package com.rns.mobile.appointments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;

import adapter.Appointment_Adapter;
import model.Appointment;

public class MainActivity extends AppCompatActivity {
    ListView appointmentlist;
    Button add;
    RecyclerView recyclerView;

    /*{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appointmentlist=(ListView)findViewById(R.id.listappoint);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        add=(Button)findViewById(R.id.btnadd);
        System.out.println("Rohit123");
        ArrayList<Appointment> list=new ArrayList();

       Appointment appointment=new Appointment();
        for(int i=0; i<5; i++) {
            appointment.setName("Rohit"+i);
            appointment.setTime("11-12 am");
            appointment.setPhone("123456789");
            list.add(appointment);
        }



        System.out.println("Length"+list.size());
      Appointment_Adapter adapter=new Appointment_Adapter(this,list);

       *//* RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);*//*
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,SearchAppointment.class);
                startActivity(i);
            }
        });



    }*/
}
