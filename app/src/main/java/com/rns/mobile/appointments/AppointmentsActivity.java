package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private RecyclerView recyclerView;
    private List<Appointment> list;
    private AppointmentsAdapter adapter;
    private User user;
    private String phoneNumber;
    private ProgressDialog dialog;
    private String TAG="Appointments Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        isReadContactPermissionGranted();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("## LOADING APPOINTMENTS .. ##");

        list = new ArrayList<>();
        phoneNumber = FirebaseUtil.getMobile();
        adapter = new AppointmentsAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
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


    public boolean isReadContactPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


}
