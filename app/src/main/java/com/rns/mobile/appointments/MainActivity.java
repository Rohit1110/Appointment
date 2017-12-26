package com.rns.mobile.appointments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;

import adapter.Appointment_Adapter;
import model.Appointment;

public class MainActivity extends AppCompatActivity {
    ListView appointmentlist;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appointmentlist=(ListView)findViewById(R.id.listappoint);
        add=(Button)findViewById(R.id.btnadd);
        System.out.println("Rohit123");
        ArrayList list=new ArrayList();

       Appointment appointment=new Appointment();

        appointment.setName("Rohit");
        appointment.setTime("11-12 am");
        appointment.setPhone("123456789");
        list.add(appointment);

        System.out.println("Length"+list.size());
      Appointment_Adapter adapter=new Appointment_Adapter(MainActivity.this,list);
        appointmentlist.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,SearchAppointment.class);
                startActivity(i);
            }
        });



    }
}
