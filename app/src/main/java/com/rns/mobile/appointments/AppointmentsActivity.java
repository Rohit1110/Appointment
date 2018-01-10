package com.rns.mobile.appointments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import adapter.AppointmentsAdapter;
import decorator.SimpleDividerItemDecoration;
import model.Appointment;
import model.User;
import utils.FirebaseUtil;
import utils.Utility;

public class AppointmentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Appointment> list;
    private AppointmentsAdapter adapter;
    private User user;
    private String phoneNumber;
    private ProgressDialog dialog;
    private String TAG = "Appointments Activity";
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        isReadContactPermissionGranted();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {


            @Override
            public void onClick(View view, int position) {
                Toast.makeText(AppointmentsActivity.this, "Long press to cancel appointment", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, final int position) {
                //Toast.makeText(AppointmentsActivity.this,""+position,Toast.LENGTH_LONG).show();
                //list.remove(position);
                Appointment a = list.get(position);
                id = a.getId();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppointmentsActivity.this);
                alertDialogBuilder.setMessage("Are you sure to cancel this Appointment");
                final int pos = position;

                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).collection("appointments").document(id).update("appointmentStatus", Utility.APP_STATUS_CANCELLED).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Appointment a = list.remove(pos);
                                        adapter.notifyItemRemoved(pos);
                                        Appointment appointment=list.get(position);
                                    /*    FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).collection("appointments").whereEqualTo("date",appointment.getDate() ).whereEqualTo("startTime",appointment.getStartTime()).whereEqualTo("endTime",appointment.getEndTime()).("appointmentStatus", Utility.APP_STATUS_CANCELLED).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("EDIT", "Error writing document", e);
                                            }
                                        });*/

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("EDIT", "Error writing document", e);
                                    }
                                });
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        }));
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


        /*AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);*/


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
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).collection(FirebaseUtil.DOC_APPOINTMENTS).
                orderBy("date", Query.Direction.DESCENDING).orderBy("startTime", Query.Direction.DESCENDING).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                //Utility.hideProgress(dialog);
                System.out.println("Appointments snapshot completed!" + documentSnapshots.size());


                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                    for (DocumentSnapshot doc : documentSnapshots) {
                        Appointment appointment = doc.toObject(Appointment.class);


                        if (appointment == null) {
                            continue;
                        }
                        if(Utility.APP_STATUS_CANCELLED.equals(appointment.getAppointmentStatus())) {
                            continue;

                        }
                        appointment.setId(doc.getId());
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


    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}
