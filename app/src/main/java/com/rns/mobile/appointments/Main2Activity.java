package com.rns.mobile.appointments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

import adapter.Appointment_Adapter;
import model.Appointment;

public class Main2Activity extends AppCompatActivity {
    RecyclerView appointmentlist;
    ArrayList<Appointment> list;
    Appointment_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        appointmentlist=(RecyclerView)findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list =new ArrayList<>();





        System.out.println("Length"+list.size());

         adapter=new Appointment_Adapter(this,list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        appointmentlist.setLayoutManager(mLayoutManager);

        appointmentlist.setAdapter(adapter);

        prepareUserlist();




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(Main2Activity.this,SearchAppointment.class);
               startActivity(intent);
            }
        });
    }
    private void prepareUserlist() {


        Appointment a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        a = new Appointment("Rohit Jadhav", "10.30-11.30");
        list.add(a);

        adapter.notifyDataSetChanged();
    }




}
