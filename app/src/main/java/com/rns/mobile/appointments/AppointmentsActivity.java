package com.rns.mobile.appointments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import adapter.AppointmentsDateAdapter;
import decorator.SimpleDividerItemDecoration;
import model.ActiveContact;
import model.Appointment;
import model.User;
import recyclerAdapter.EventItem;
import recyclerAdapter.HeaderItem;
import recyclerAdapter.ListItem;
import recyclerAdapter.SimpleAdapter;
import utils.FirebaseUtil;
import utils.Utility;

public class AppointmentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Appointment> list;

    private User user;
    private String phoneNumber;
    private ProgressDialog dialog;
    private String TAG = "Appointments Activity";
    String id;
    private Appointment currentAppointment, cappointnent,appointmentid;
    private SimpleAdapter mAdapter;
    private View button;
    AlertDialog alertDialog1;
    TextView noAppointment;
    private boolean showcacel = false;
    int selectedElement = 1; //global variable to store state
    private String from = "all";
    String elementstatus;
    String appointmentstatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        //isReadContactPermissionGranted();
        // isReadCalenderPermissionGranted();
        noAppointment = (TextView) findViewById(R.id.nodata);


        user = Utility.getUserFromSharedPrefs(AppointmentsActivity.this);
        String userJson = getIntent().getStringExtra("user");
        elementstatus = getIntent().getStringExtra("showcancel");
        appointmentstatus = getIntent().getStringExtra("states");
        System.out.println("status " + elementstatus + " appointment " + appointmentstatus);
        if (elementstatus.equals("true")) {
            showcacel = true;
        } else {
            showcacel = false;
        }
        if (appointmentstatus.equals("1")) {
            selectedElement = 1;
        } else {
            selectedElement = 2;
        }
      /*  Intent intent = getIntent();

           showcacel=intent.getBooleanExtra("showcanceled",false);
           selectedElement=intent.getIntExtra("state",1);*/

        System.out.println("Show cancel " + showcacel + " states " + selectedElement);

        if (userJson != null) {
            user = new Gson().fromJson(userJson, User.class);

        }


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
      /* String[] array_list={"one","two"};

        mAdapter = new SimpleAdapter(this,array_list);

        //This is the code to provide a sectioned list
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        //Sections
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Section 1"));
       // sections.add(new SimpleSectionedRecyclerViewAdapter.Section(2,"Section 2"));
       *//* sections.add(new SimpleSectionedRecyclerViewAdapter.Section(12,"Section 3"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(14,"Section 4"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(20,"Section 5"));*//*

        //Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[1];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(this,R.layout.section,R.id.section_text,adapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        recyclerView.setAdapter(mSectionedAdapter);*/
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {


            @Override
            public void onClick(View view, int position) {
                AppointmentsDateAdapter adapter = (AppointmentsDateAdapter) recyclerView.getAdapter();
                appointmentid=adapter.getAppointment(position);
               // Toast.makeText(AppointmentsActivity.this, "Press and Hold to Cancel this Appointment", Toast.LENGTH_LONG).show();
                Intent i = new Intent(AppointmentsActivity.this,AppointmentDetails.class);
                i.putExtra("appointmentid",appointmentid.getId());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

                AppointmentsDateAdapter adapter = (AppointmentsDateAdapter) recyclerView.getAdapter();
                System.out.println("current positions..." + adapter.getItemCount());


//                list.remove(position);
                //currentAppointment = list.get(position);

                currentAppointment = adapter.getAppointment(position);
                //id = currentAppointment.getId();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppointmentsActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to cancel this Appointment");
                final int pos = position;

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!Utility.isInternetOn(AppointmentsActivity.this)) {
                            Utility.createAlert(AppointmentsActivity.this, Utility.ERROR_CONNECTION);
                            return;
                        }
                        System.out.println("Deleteing appointment =>" + currentAppointment.getId());
                        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).collection(FirebaseUtil.DOC_APPOINTMENTS).document(currentAppointment.getId()).update("appointmentStatus", Utility.APP_STATUS_CANCELLED).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Utility.deleteAppointmentFromCalendar(AppointmentsActivity.this, currentAppointment);
                                //Delete other users appointment
                                System.out.println("Size of contactlis " + currentAppointment.getContactList().size());
                                if (currentAppointment.getContactList().size() == 1) {
                                    FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(currentAppointment.getPhone()).collection(FirebaseUtil.DOC_APPOINTMENTS).document(currentAppointment.getId()).update("appointmentStatus", Utility.APP_STATUS_CANCELLED).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Appointment app = null;
                                            for (ActiveContact userContact : currentAppointment.getContactList()) {
                                                System.out.println("Deleted for other user appointment =>" + currentAppointment.getId());
                                                app = currentAppointment.duplicate(userContact.getNumber());
                                                app.setName(user.prepareFullName());
                                            }
                                            new NotificationTask(app, Utility.NOTIFICATION_TYPE_CANCEL).sendNotification();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("EDIT", "Error deleting other user App", e);
                                        }
                                    });
                                } else {

                                    Appointment app = null;
                                    for (ActiveContact userContact : currentAppointment.getContactList()) {
                                        System.out.println("Deleted for other user appointment =>" + currentAppointment.getId());
                                        app = currentAppointment.duplicate(userContact.getNumber());
                                        app.setName(user.prepareFullName());


                                    }
                                    new NotificationTask(currentAppointment, Utility.NOTIFICATION_TYPE_CANCEL).sendNotification();
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("EDIT", "Error deleting app", e);
                            }
                        });

                    }
                });


                cappointnent = adapter.getAppointment(position);

                Date dt = Utility.formatDate(cappointnent.getDate(), Utility.DATE_FORMAT_USED);
                String chkf = Utility.CompareDate(dt, new Date());
                String chkp = Utility.getcurrentAppointment(cappointnent.getStartTime(), cappointnent.getEndTime(), dt, new Date());
                if (!chkf.contains("past") || chkf.contains("future") || chkp.contains("future")) {
                    if (cappointnent.getAppointmentStatus().equals(Utility.APP_STATUS_ACTIVE)) {
                        if (!chkp.contains("present") || chkp.contains("future")) {
                            AlertforDoubleclick(position, cappointnent);
                        } else {

                            if (cappointnent.getAppointmentStatus().equals(Utility.APP_STATUS_CANCELLED)) {
                                Toast.makeText(AppointmentsActivity.this, "This appointment is already cancelled", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AppointmentsActivity.this, "ongoing or past appointments cannot be cancelled", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(AppointmentsActivity.this, "This appointment is already cancelled", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (cappointnent.getAppointmentStatus().equals(Utility.APP_STATUS_CANCELLED)) {
                        Toast.makeText(AppointmentsActivity.this, "This appointment is already cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AppointmentsActivity.this, "ongoing or past appointments cannot be cancelled", Toast.LENGTH_LONG).show();
                    }
                }


            }
        }));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("## LOADING APPOINTMENTS .. ##");

        list = new ArrayList<>();
        phoneNumber = FirebaseUtil.getMobile();
        //adapter = new AppointmentsAdapter(this, list);
       /* RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);*/
        prepareAppointmentsList(null);


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

    private void AlertforDoubleclick(int position, final Appointment appointment) {
        //Toast.makeText(AppointmentsActivity.this,""+position,Toast.LENGTH_LONG).show();
        currentAppointment = appointment;
        //id = currentAppointment.getId();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppointmentsActivity.this);
        alertDialogBuilder.setMessage("Are you sure to cancel this Appointment");
        final int pos = position;

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                System.out.println("Deleteing appointment =>" + currentAppointment.getId());
                FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).collection(FirebaseUtil.DOC_APPOINTMENTS).document(currentAppointment.getId()).update("appointmentStatus", Utility.APP_STATUS_CANCELLED).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utility.deleteAppointmentFromCalendar(AppointmentsActivity.this, currentAppointment);
                        //Delete other users appointment


//                        System.out.println("Size of contactlis " + currentAppointment.getContactList().size());

                     /*   if (currentAppointment.getContactList().size() == 0) {
                            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(currentAppointment.getPhone()).collection(FirebaseUtil.DOC_APPOINTMENTS).document(currentAppointment.getId()).update("appointmentStatus", Utility.APP_STATUS_CANCELLED).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("Deleted for other user appointment =>" + currentAppointment.getId());


                                    Map<String, String> contact = (Map<String, String>) appointment.getContactList().get(0);
                                    System.out.println("Hash MAP value =>" + contact);
                                    Appointment app = appointment.duplicate(contact.get("number"));

                                    //Appointment app = currentAppointment.duplicate(currentAppointment.getContactList().get(0).getNumber());
                                    app.setName(user.prepareFullName());
                                    new NotificationTask(app, Utility.NOTIFICATION_TYPE_CANCEL).sendNotification();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("EDIT", "Error deleting other user App", e);
                                }
                            });
                        } else {*/
                        if (appointment.getContactList()!=null && appointment.getContactList().size() > 0) {
                            for (int i = 0; i < appointment.getContactList().size(); i++) {
                                final Map<String, String> contact = (Map<String, String>) appointment.getContactList().get(i);
                                System.out.println("Hash MAP value =>" + contact);

                                final Appointment app = appointment.duplicate(contact.get("number"));
                                app.setName(user.prepareFullName());
                                final int finalI = i;
                                FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(contact.get("number")).collection(FirebaseUtil.DOC_APPOINTMENTS).document(currentAppointment.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult() != null && task.getResult().exists()) {

                                            Appointment currentUser = new Appointment();
                                            if (task.getResult().getString("name") != null) {
                                                currentUser.setName(task.getResult().getString("name"));
                                            }
                                            if (task.getResult().getString("startTime") != null) {
                                                currentUser.setStartTime(task.getResult().getString("startTime"));
                                            }
                                            if (task.getResult().getString("endTime") != null) {
                                                currentUser.setEndTime(task.getResult().getString("endTime"));
                                            }
                                          /*  if (doc.getString("phone") != null) {
                                                appointments.setPhone(doc.getString("phone"));
                                            }*/
                                            if (task.getResult().getString("description") != null) {
                                                currentUser.setDescription(task.getResult().getString("description"));
                                            }
                                            if (task.getResult().getString("date") != null) {
                                                currentUser.setDate(task.getResult().getString("date"));
                                            }
                                            if (task.getResult().getString("appointmentStatus") != null) {
                                                currentUser.setAppointmentStatus(task.getResult().getString("appointmentStatus"));
                                            }
                                            if (task.getResult().get("contactList") != null) {
                                                currentUser.setContactList((List<ActiveContact>) task.getResult().getData().get("contactList"));
                                            }

                                            final List<Map<String, String>> updatedList = new ArrayList<>();

                                            for (int i = 0; i < currentUser.getContactList().size(); i++) {
                                                Map<String, String> contact = (Map<String, String>) currentUser.getContactList().get(i);
                                                String phone = null;

                                                phone = contact.get("number");

                                                System.out.println("Phone " + phone + " number " + phoneNumber);
                                                if (phone.equals(phoneNumber)) {
                                                    contact.put("status", Utility.APP_STATUS_CANCELLED);
                                                } else {
                                                   /* if(contact.get("status").equals(null)||!contact.get("status").equals(Utility.APP_STATUS_CANCELLED))
                                                    {
                                                        totalmemberlist.add(contact);
                                                    }*/

                                                }
                                                updatedList.add(contact);

                                                new NotificationTask(app, Utility.NOTIFICATION_TYPE_CANCEL).sendNotification();
                                            }

                                            final List<Map<String, String>> totalmemberlist = new ArrayList<>();

                                            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(contact.get("number")).
                                                    collection(FirebaseUtil.DOC_APPOINTMENTS).document(currentUser.toString()).update("contactList", updatedList).addOnSuccessListener(new OnSuccessListener<Void>() {

                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(contact.get("number")).collection(FirebaseUtil.DOC_APPOINTMENTS).document(currentAppointment.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.getResult() != null && task.getResult().exists()) {

                                                                Appointment currentUser = new Appointment();
                                                                if (task.getResult().getString("name") != null) {
                                                                    currentUser.setName(task.getResult().getString("name"));
                                                                }
                                                                if (task.getResult().getString("startTime") != null) {
                                                                    currentUser.setStartTime(task.getResult().getString("startTime"));
                                                                }
                                                                if (task.getResult().getString("endTime") != null) {
                                                                    currentUser.setEndTime(task.getResult().getString("endTime"));
                                                                }

                                                                if (task.getResult().getString("description") != null) {
                                                                    currentUser.setDescription(task.getResult().getString("description"));
                                                                }
                                                                if (task.getResult().getString("date") != null) {
                                                                    currentUser.setDate(task.getResult().getString("date"));
                                                                }
                                                                if (task.getResult().getString("appointmentStatus") != null) {
                                                                    currentUser.setAppointmentStatus(task.getResult().getString("appointmentStatus"));
                                                                }
                                                                if (task.getResult().get("contactList") != null) {
                                                                    currentUser.setContactList((List<ActiveContact>) task.getResult().getData().get("contactList"));
                                                                }


                                                                for (int i = 0; i < currentUser.getContactList().size(); i++) {
                                                                    Map<String, String> contacts = (Map<String, String>) currentUser.getContactList().get(i);
                                                                    System.out.println("Appointment status " + contacts.get("status"));
                                                                    if (contacts.get("status").equals(Utility.APP_STATUS_ACTIVE)) {
                                                                        System.out.println("Other memeber " + contacts.get("number"));
                                                                        totalmemberlist.add(contacts);

                                                                    }
                                                                }
                                                                CancelAppoitment(totalmemberlist, contact, appointment);


                                                            }
                                                        }
                                                    });


                                                }
                                            });


                                        }

                                    }
                                });
                            }


                            // new NotificationTask(app, Utility.NOTIFICATION_TYPE_CANCEL).sendNotification();

                        }//end for
                    }//end else


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EDIT", "Error deleting app", e);
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

    private void CancelAppoitment(List<Map<String, String>> totalmemberlist, Map<String, String> contact, final Appointment appointment) {
        if (totalmemberlist.size() == 0) {
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(contact.get("number")).collection(FirebaseUtil.DOC_APPOINTMENTS).document(currentAppointment.getId()).update("appointmentStatus", Utility.APP_STATUS_CANCELLED).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Deleted for other user appointment =>" + currentAppointment.getId());


                    Map<String, String> contact = (Map<String, String>) appointment.getContactList().get(0);
                    System.out.println("Hash MAP value =>" + contact);
                    Appointment app = appointment.duplicate(contact.get("number"));

                    //Appointment app = currentAppointment.duplicate(currentAppointment.getContactList().get(0).getNumber());
                    app.setName(user.prepareFullName());
                    new NotificationTask(app, Utility.NOTIFICATION_TYPE_CANCEL).sendNotification();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("EDIT", "Error deleting other user App", e);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionprofile:

                Intent intent = new Intent(AppointmentsActivity.this, EditProfileActivity.class);
                intent.putExtra("user", new Gson().toJson(user));

                startActivity(intent);
                return true;

            case R.id.actionfilter:
                //CreateAlertDialogWithRadioButtonGroup();
                showDialog();
                return true;

            case R.id.actionshare:
                //CreateAlertDialogWithRadioButtonGroup();
                Utility.sendLink(AppointmentsActivity.this);
                return true;

        }
        return false;
    }

    private void prepareAppointmentsList(final String date) {
        System.out.println("coming date " + date);
        //if (date == null) {
        //Utility.showProgress(dialog, AppointmentsActivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).collection(FirebaseUtil.DOC_APPOINTMENTS).
                orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                //Utility.hideProgress(dialog);
                prepareAppointmentsAdapter(documentSnapshots, date);
            }
        });
         /*}else {

            //Utility.showProgress(dialog, AppointmentsActivity.this);
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).collection(FirebaseUtil.DOC_APPOINTMENTS).whereEqualTo("date", date).
                    orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    //Utility.hideProgress(dialog)
                    System.out.println("Snapshot =>" + documentSnapshots + " -- " + e);
                    prepareAppointmentsAdapter(documentSnapshots);
                }
            });

        }*/

    }

    private void prepareAppointmentsAdapter(QuerySnapshot documentSnapshots, String filterDate) {
        System.out.println("call one");
        list.clear();
        if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
            System.out.println("Appointments snapshot completed!" + documentSnapshots.toString());
            showNoAppointments(false);
            Appointment appointments = null;
            for (DocumentSnapshot doc : documentSnapshots) {
                System.out.println("In loop " + doc.getData().get("contactList"));
                //appointment=Utility.setData(doc,appointment)
                appointments = new Appointment();
                if (doc.getString("name") != null) {
                    appointments.setName(doc.getString("name"));
                }
                if (doc.getString("startTime") != null) {
                    appointments.setStartTime(doc.getString("startTime"));
                }
                if (doc.getString("endTime") != null) {
                    appointments.setEndTime(doc.getString("endTime"));
                }
                if (doc.getString("phone") != null) {
                    appointments.setPhone(doc.getString("phone"));
                }
                if (doc.getString("date") != null) {
                    appointments.setDate(doc.getString("date"));
                }
                if (doc.getString("description") != null) {
                    appointments.setDescription(doc.getString("description"));
                }

                if (doc.getString("appointmentStatus") != null) {
                    appointments.setAppointmentStatus(doc.getString("appointmentStatus"));
                }
                if (doc.get("contactList") != null) {
                    appointments.setContactList((List<ActiveContact>) doc.getData().get("contactList"));
                }


                //Appointment appointment = doc.toObject(Appointment.class);
                if (appointments == null) {
                    continue;
                }
                if (Utility.APP_STATUS_CANCELLED.equals(appointments.getAppointmentStatus()) && !showcacel) {
                    continue;

                }
                if (!Utility.APP_STATUS_CANCELLED.equals(appointments.getAppointmentStatus()) && showcacel) {
                    continue;

                }
                if (filterDate != null && appointments.getDate() != null && !appointments.getDate().equals(filterDate)) {
                    continue;
                }
                appointments.setId(doc.getId());
                /*HeaderItem header = new HeaderItem(doc.getDate("date"));
                list.add(header);*/
                list.add(appointments);
                Utility.addAppointmentsToCalender(AppointmentsActivity.this, appointments);

            }

            System.out.println("Appointments list size => " + list.size());


            Map<String, List<Appointment>> events = toMap(list);
            List<ListItem> items = new ArrayList<>();

            for (String date : events.keySet()) {
                HeaderItem header = new HeaderItem(date);
                items.add(header);

                for (Appointment appointment : events.get(date)) {
                    EventItem item = new EventItem(appointment);
                    items.add(item);
                }
            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(AppointmentsActivity.this));

            //Collections.sort(items);

            recyclerView.setAdapter(new AppointmentsDateAdapter(items));
            if (list.size() == 0) {
                showNoAppointments(true);
            }

        } else {

            showNoAppointments(true);
        }
    }

    private void showNoAppointments(boolean show) {
        if (!show) {
            noAppointment.setText("");
            return;
        }
        if (from.equals("all")) {
            noAppointment.setVisibility(View.VISIBLE);
            noAppointment.setText("No appointments to show");
        } else if (from.equals("today")) {
            noAppointment.setVisibility(View.VISIBLE);
            noAppointment.setText("No appointments to show today");
        } else if (from.equals("cancel")) {
            noAppointment.setVisibility(View.VISIBLE);

            noAppointment.setText("No cancelled appointments to show");
        }
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

    public boolean isReadCalenderPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 1);
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


    //Calendar Read permission

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AppointmentsActivity.this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) AppointmentsActivity.this, android.Manifest.permission.WRITE_CALENDAR)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AppointmentsActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write calendar permission is necessary to write event!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) AppointmentsActivity.this, new String[]{android.Manifest.permission.WRITE_CALENDAR}, Utility.MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) AppointmentsActivity.this, new String[]{android.Manifest.permission.WRITE_CALENDAR}, Utility.MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_WRITE_CALENDAR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //code for deny
                }
                break;
        }
    }

    @NonNull
    private Map<String, List<Appointment>> toMap(@NonNull List<Appointment> events) {
        Map<String, List<Appointment>> map = new TreeMap<>(Collections.<String>reverseOrder());
        for (Appointment event : events) {
            List<Appointment> value = map.get(event.getDate());
            if (value == null) {
                value = new ArrayList<>();
                map.put(event.getDate(), value);
            }
            value.add(event);
        }
        return map;
    }


    AlertDialog alert;

    private void SingleChoiceWithRadioButton() {
        final String[] selectFruit = new String[]{"Todays Appointments", "Show All Appointments", "Cancelled Appointments"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Choice");
        builder.setSingleChoiceItems(selectFruit, selectedElement, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedElement = which;
                //Toast.makeText(AppointmentsActivity.this, selectFruit[which]+":"+ which + " Selected", Toast.LENGTH_LONG).show();
                if (selectFruit[which] == "Todays Appointments") {
                    from = "today";
                    showcacel = false;
                    prepareAppointmentsList(Utility.formatDate(new Date(), Utility.DATE_FORMAT_USED));
                } else if (selectFruit[which] == "Show All Appointments") {
                    showcacel = false;
                    from = "all";
                    prepareAppointmentsList(null);

                } else if (selectFruit[which] == "Cancelled Appointments") {
                    showcacel = true;
                    from = "cancel";
                    prepareAppointmentsList(null);

                }
                //  dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert = builder.create();
        alert.show();
    }

//Call this method always

    private void showDialog() {
        if (alert == null) SingleChoiceWithRadioButton();
        else alert.show();
    }


}
