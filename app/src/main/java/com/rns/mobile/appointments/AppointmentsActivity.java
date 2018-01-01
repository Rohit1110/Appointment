package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.AppointmentsAdapter;
import model.Appointment;
import model.User;
import utils.FirebaseUtil;

public class AppointmentsActivity extends AppCompatActivity {
    private RecyclerView appointmentlist;
    private List<Appointment> list;
    private AppointmentsAdapter adapter;
    private User user;
    private String phoneNumber;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        appointmentlist = (RecyclerView) findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("## LOADING APPOINTMENTS .. ##");

        list = new ArrayList<>();
        phoneNumber = FirebaseUtil.getMobile();
        adapter = new AppointmentsAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        appointmentlist.setLayoutManager(mLayoutManager);

        prepareAppointmentsList();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentsActivity.this, SearchAppointmentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void prepareAppointmentsList() {

        //Utility.showProgress(dialog, AppointmentsActivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).collection(FirebaseUtil.DOC_APPOINTMENTS).orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                //Utility.hideProgress(dialog);
                System.out.println("Appointments snapshot completed!" + documentSnapshots.size());
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentSnapshot doc : documentSnapshots) {
                        Appointment appointment = doc.toObject(Appointment.class);
                        if (appointment == null) {
                            continue;
                        }
                        list.add(appointment);
                    }
                    System.out.println("Appointments list size => " + list.size());
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }


}
