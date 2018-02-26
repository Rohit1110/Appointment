package com.rns.mobile.appointments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import model.ActiveContact;
import model.Appointment;
import model.SmsField;
import model.User;
import model.UserContact;
import utils.FirebaseUtil;
import utils.Utility;

public class SelectDateAcitivity extends AppCompatActivity {

    private Button book;
    private Spinner from, to;
    private TextView appointmentPhone, today, tomorrow, slotsSelected;
    private TextView setdate;
    private Appointment appointment;
    private DatePicker selectedDate;
    private ProgressDialog dialog;
    //private User otherUser;
    private ListView availableSlotsListView;
    private ArrayAdapter<String> adapter;
    private Calendar myCalendar = Calendar.getInstance();
    private List<String> slotsList;
    private Set<String> blockedSlots;
    private String userPhone;
    private Set<String> selectedSlots;
    private List<String> filteredSlots;
    private EditText reason;
    TextView noSlots;
    public SmsField smsField;
    boolean isoffday = false;
    Appointment appointments = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // book = (Button) findViewById(R.id.btnbook);
        setdate = (TextView) findViewById(R.id.selecteddate);
        reason = (EditText) findViewById(R.id.edit_reason);

        reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        /*reason.setFocusable(true);
        reason.setFocusableInTouchMode(true);*/
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });
        noSlots = (TextView) findViewById(R.id.notmeslots);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(Utility.DATE_FORMAT_DISPLAY);
        String dateString = sdf.format(date);
        setdate.setText(dateString);

        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");*/

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(SelectDateAcitivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat(Utility.DATE_FORMAT_USED);
                        Date dates = null;
                        try {
                            dates = sdf.parse(selectedDateString);
                            if (dates != null) {
                                isoffday = false;
                                setdate.setText(new SimpleDateFormat(Utility.DATE_FORMAT_DISPLAY).format(dates));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //
                        if (selectedDateString != null && userPhone != null) {
                            appointment.setDate(selectedDateString);
                            updateUserAppointments();
                        }
                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });
        today = (TextView) findViewById(R.id.txttoday);
        today.setTextColor(getResources().getColor(R.color.colorAccent));
        tomorrow = (TextView) findViewById(R.id.txttommarow);

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isoffday = false;
                today.setTextColor(getResources().getColor(R.color.colorAccent));
                tomorrow.setTextColor(getResources().getColor(R.color.album_title));
                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat(Utility.DATE_FORMAT_DISPLAY);
                String dateString = sdf.format(date);
                setdate.setText(dateString);

                appointment.setDate(Utility.extractFromDisplayDate(dateString));
                updateUserAppointments();
            }
        });

        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isoffday = false;
                tomorrow.setTextColor(getResources().getColor(R.color.colorAccent));
                today.setTextColor(getResources().getColor(R.color.album_title));

                SimpleDateFormat sdf = new SimpleDateFormat(Utility.DATE_FORMAT_DISPLAY);
                GregorianCalendar calendar = new GregorianCalendar();

//Display the date now:
                Date now = calendar.getTime();
                DateFormat fmt = sdf.getDateInstance(DateFormat.FULL, Locale.US);
                String formattedDate = fmt.format(now);
                System.out.println(formattedDate);

//Advance the calendar one day:
                calendar.add(calendar.DAY_OF_MONTH, 1);
                Date tomorrow = calendar.getTime();
                formattedDate = sdf.format(tomorrow);
                System.out.println(formattedDate);
                setdate.setText(formattedDate);

                appointment.setDate(Utility.extractFromDisplayDate(formattedDate));
                updateUserAppointments();
            }
        });

        //updateUserAppointments();

        appointmentPhone = (TextView) findViewById(R.id.txt_appointment_phone);
        //selectedDate = (DatePicker) findViewById(R.id.datepicker);
        availableSlotsListView = (ListView) findViewById(R.id.list_availableslot);
        slotsSelected = (TextView) findViewById(R.id.txt_slot_selected);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, slotsList);
        availableSlotsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        availableSlotsListView.setAdapter(adapter);
        availableSlotsListView.setItemsCanFocus(false);

        availableSlotsListView.setOnItemClickListener(new SlotsSelected());


        appointment = Utility.extractAppointment(SelectDateAcitivity.this);

        blockedSlots = new HashSet<String>();
        slotsList = Arrays.asList(Utility.TIME_SLOTS);
        userPhone = FirebaseUtil.getMobile();
        selectedSlots = new HashSet<>();
        System.out.println("Slot list initialized = " + slotsList);

        if (appointment != null) {
            String names = "";
            if (appointment.getContactList() != null) {

                //appointmentPhone.setText(appointment.getContactList().toString());

                for (int i = 0; i < appointment.getContactList().size(); i++) {
                    System.out.println("SSSSSSSSSSSSS" + appointment.getContactList().get(i).getContact());
                    names = names + "," + appointment.getContactList().get(i).getContact();

                }
                appointmentPhone.setText(names.substring(1, names.length()));
                System.out.println("SSSS" + names);
            }
           /* if (appointment.getName() != null) {
                appointmentPhone.setText(appointment.getName());
            } else {
                appointmentPhone.setText(appointment.getPhone());
            }*/
        }


        appointment.setDate(Utility.extractFromDisplayDate(dateString));
        updateUserAppointments();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                //Book appointment
/*Activity curActivity, String title,
                String desc, String place, int status, long startDate,
                boolean needReminder, boolean needMailService*/
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse("10/01/2018 16:56:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long startDate = date.getTime();
                //addAppointmentsToCalender(SelectDateAcitivity.this,"title1","test desc1","pune",1,startDate,true,false);
                bookAppointment();
                /*Intent intent = new Intent(SelectDateAcitivity.this, MainActivity.class);
                startActivity(intent);
                finish();*/
                return (true);
        }
        return false;

    }

    private void bookAppointment() {
        System.out.println("Book appointment clicked!");
        if (!Utility.isInternetOn(SelectDateAcitivity.this)) {
            Utility.createAlert(SelectDateAcitivity.this, Utility.ERROR_CONNECTION);
            return;
        }

        prepareAppointmentSlots();
        if (!reason.getText().toString().equals("")) {
            appointment.setDescription(reason.getText().toString());
            System.out.println("Reason =>" + reason.getText().toString());
        } else {
            reason.setError("Please write reason");
            return;
        }

        final User currentUser = Utility.getUserFromSharedPrefs(SelectDateAcitivity.this);
        if (selectedSlots != null && selectedSlots.size() > 0) {
            appointment.setAppointmentStatus(Utility.APP_STATUS_ACTIVE);
            //appointment.setName(currentUser.prepareFullName());
            dialog = Utility.showProgress(SelectDateAcitivity.this);
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(userPhone).
                    collection(FirebaseUtil.DOC_APPOINTMENTS).document(appointment.toString()).set(appointment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Completed Booking for user!!!" + userPhone);

                    System.out.println("Appointment added successfully!!" + appointment);


                    appointment.setId(appointment.toString());

                    for (final ActiveContact contact : appointment.getContactList()) {

                        if (contact.getNumber() == null) {
                            continue;
                        }
                        final Appointment otherUserAppointment = appointment.duplicate(userPhone);
                        if (currentUser != null) {
                            //otherUserAppointment.setName(currentUser.prepareFullName());
                            //otherUserAppointment.setContactList(appointments.getContactList());
                            List<ActiveContact> contactList = new ArrayList<>();
                            for (ActiveContact userContact : appointment.getContactList()) {
                                if (userContact.getNumber() != null && !userContact.getNumber().equals(contact.getNumber())) {
                                    System.out.println("Current User Contact " + userContact.getContact() + " : " + userContact.getNumber());
                                    contactList.add(userContact);
                                }
                            }
                            ActiveContact currentContact = new ActiveContact();
                            System.out.println("Curr user phone=>" + userPhone);
                            currentContact.setNumber(userPhone);
                            currentContact.setContact(currentUser.prepareFullName());
                            currentContact.setStatus(Utility.APP_STATUS_ACTIVE);
                            contactList.add(currentContact);
                            otherUserAppointment.setContactList(contactList);
                            System.out.println("New contactlist " + contactList);
                        }

                        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(contact.getNumber()).
                                collection(FirebaseUtil.DOC_APPOINTMENTS).
                                document(appointment.toString()).set(otherUserAppointment).addOnSuccessListener(new OnSuccessListener<Void>() {


                            @Override
                            public void onSuccess(Void aVoid) {
                                System.out.println("Completed booking for other user!!!" + contact.getNumber());
                                Utility.hideProgress(dialog);
                                System.out.println("Appointment added successfully!!" + otherUserAppointment);
                                Appointment duplicate = null;
                                /*for (Appointment duplicate: appointment.getContactList()) {
                                    duplicate = appointment.duplicate(duplicate.getNumber());
                                }*/

                                for (ActiveContact ap : appointment.getContactList()) {
                                    duplicate = appointment.duplicate(ap.getNumber());


                                    //duplicate.setNumber(otherUserAppointment.getContactList().get(finalI).getContact());
                                    duplicate.setContactList(appointment.getContactList());
                                    duplicate.setName(currentUser.prepareFullName());
                                    duplicate.setDescription(appointment.getDescription());
                                    System.out.println("duplicate " + duplicate.getContactList());
                                    new NotificationTask(duplicate, Utility.NOTIFICATION_TYPE_NEW).sendNotification();
                                    final Appointment finalDuplicate = duplicate;
                                    System.out.println("Final duplicate " + finalDuplicate);

                                    FirebaseUtil.db.collection(FirebaseUtil.DOC_CONFIG).document("ehJdPUDH9lAz50qPK2Hw").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                DocumentSnapshot document = task.getResult();
                                                if (document != null) {
                                                    smsField = document.toObject(SmsField.class);
                                                    new SMSTask(Utility.NOTIFICATION_TYPE_NEW, finalDuplicate, smsField).execute();
                                                    Log.d("TAG", "DocumentSnapshot data: " + task.getResult().getData());
                                                } else {
                                                    Log.d("TAG", "No such document");
                                                }
                                            } else {
                                                Log.d("TAG", "get failed with ", task.getException());
                                            }
                                        }

                                    });
                                }

                                Utility.hideProgress(dialog);
                                goToHome();
                            }
                        }).

                                addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Utility.hideProgress(dialog);
                                        Utility.createAlert(SelectDateAcitivity.this, Utility.ERROR_CONNECTION);
                                        System.out.println("Appointment failed to add => " + e);
                                        e.printStackTrace();
                                    }
                                });
                    }


                }
            }) /*{
            @Override public void onComplete (@NonNull Task < DocumentReference > task) {

            }*//*
        })*/.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Progress(dialog);
                    Utility.createAlert(SelectDateAcitivity.this, Utility.ERROR_CONNECTION);
                    System.out.println("Appointment failed to add => " + e);
                    e.printStackTrace();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("Completed!!" + task.getException());
                }
            });
        } else

        {
            Toast.makeText(SelectDateAcitivity.this, "Please select at least one time slot", Toast.LENGTH_LONG).show();
        }
    }

    private void goToHome() {

        if (Utility.checkPermission(SelectDateAcitivity.this)) {
            //Utility.addAppointmentsToCalender(SelectDateAcitivity.this, appointment);
            Utility.addAppointmentsToCalender(SelectDateAcitivity.this, appointment);
            System.out.println("Added to Calendar!!");
        }

        Intent i = new Intent(SelectDateAcitivity.this, AppointmentsActivity.class);
        i.putExtra("showcancel", "false");
        i.putExtra("states", "1");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }


    private void updateUserSlots(User user) {
        System.out.println("user name " + user.getStartTime());
        if (user != null && user.getStartTime() != null && user.getEndTime() != null) {
            Set<String> validSlots = new HashSet<>();
            for (String slot : Utility.TIME_SLOTS) {
                if (slot.compareTo(user.getStartTime()) >= 0 && slot.compareTo(user.getEndTime()) <= 0) {
                    validSlots.add(slot);
                }
            }

            slotsList = new ArrayList<>(validSlots);
            //slotsToList = new ArrayList<>(validSlots);
            Collections.sort(slotsList);
            //Collections.sort(slotsToList);
            System.out.println("Slots after filtering by other user = >" + validSlots);
            //setSlotAdapters(slotsFromList);
        }

    }

    private void updateUserAppointments() {
        dialog = Utility.showProgress(SelectDateAcitivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(userPhone).collection(FirebaseUtil.DOC_APPOINTMENTS).whereEqualTo("date", appointment.getDate()).whereEqualTo("appointmentStatus", Utility.APP_STATUS_ACTIVE).
                orderBy("startTime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //Utility.hideProgress(dialog);
                System.out.println("On complete" + FirebaseUtil.resultExists(task));

                if (FirebaseUtil.resultExists(task)) {
                    System.out.println("Done fetching users apps for " + userPhone + " == " + task.getResult().size() + " Success:" + task.isSuccessful());
                    blockSlots(task);
                }
                blockOtherUserSlots();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Utility.hideProgress(dialog);
                Utility.createAlert(SelectDateAcitivity.this, Utility.ERROR_CONNECTION);
                System.out.println("Failed to load user appointments =>" + e);
                e.printStackTrace();
            }
        });
    }


    private void blockSlots(@NonNull Task<QuerySnapshot> task) {

        if (task.getResult() != null && task.getResult().size() > 0) {
            System.out.println("Blocking slots for user" + task.getResult());

            for (DocumentSnapshot doc : task.getResult()) {
                if (!doc.exists()) {

                    continue;
                }
//                Appointment appointment = doc.toObject(Appointment.class);
                System.out.println("Doc print " + doc.getData());
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
               /* if (doc.getString("phone") != null) {
                    appointments.setPhone(doc.getString("phone"));
                }*/
                if (doc.getString("description") != null) {
                    appointments.setDescription(doc.getString("description"));
                }
                if (doc.getString("date") != null) {
                    appointments.setDate(doc.getString("date"));
                }
                if (doc.getString("appointmentStatus") != null) {
                    appointments.setAppointmentStatus(doc.getString("appointmentStatus"));
                }
                if (doc.get("contactList") != null) {
                    appointments.setContactList((List<ActiveContact>) doc.getData().get("contactList"));
                }
                if (appointments != null) {
                    System.out.println(appointment);
                    if (appointments.getStartTime() != null && slotsList.contains(appointments.getStartTime())) {
                        //dataAdapter.remove(appointment.getStartTime());
                        System.out.println("Blocking slots for .." + appointments.getStartTime());
                        Integer index = slotsList.indexOf(appointments.getStartTime());
                        index++;
                        while (index < slotsList.size()) {
                            String blockedSlot = slotsList.get(index - 1) + Utility.SLOT_APPENDER + slotsList.get(index);
                            blockedSlots.add(blockedSlot);
                            if (slotsList.get(index).compareTo(appointments.getEndTime()) >= 0) {
                                break;
                            }
                            index++;
                        }

                    }

                } else {
                    System.out.println("No data for Slots");
                }
            }
            //Utility.hideProgress(dialog);
            System.out.println("Blocked slots after user =>" + blockedSlots);
        } else {

            System.out.println("ERROR in fetching users apps =>" + task.getException());
        }
        updateAvailableSlots();
        System.out.println("Blocked slots after user =>" + blockedSlots);
    }

    private void blockOtherUserSlots() {

        System.out.println("ASsssss" + appointment.getContactList().size());
        //System.out.println("Blocking other user slots ..1" + appointments.getName());
        if (appointment != null && appointment.getContactList() != null) {
            for (final ActiveContact otherContact : appointment.getContactList()) {
           /* if (!appointment.getPhone().trim().contains("+")) {
                appointment.setPhone(Utility.COUNTRY_CODE + appointment.getPhone());
            }*/
                /*if (!otherContact.getNumber().trim().contains("+")) {*/
                appointment.setPhone(otherContact.getNumber());
               /*}*/
                System.out.println("## Loading profile for .. " + appointment.getPhone() + " dialog=" + dialog + " number = " + appointment.getPhone());
                //dialog = Utility.showProgress(SelectDateAcitivity.this);


                // final ActiveContact con = c;


                FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(otherContact.getNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        //Utility.hideProgress(dialog);
                        if (FirebaseUtil.taskExists(task)) {
                            System.out.println("## Done profile .." + task.getResult().getString("firstName"));
                            User otherUser = task.getResult().toObject(User.class);

                            otherUser.setPhone(otherContact.getNumber());

                            if (otherContact.getNumber() == null) {
                                appointment.setName(otherUser.getFirstName() + " " + otherUser.getLastName());
                                appointmentPhone.setText(otherContact.getNumber());
                            }
                            System.out.println("Appointment for =>" + otherContact.getNumber());

                            //Update slotsList based on this other users available slotsList
                            if (isWeeklyOff(otherUser)) {

                                if (filteredSlots != null) {
                                    filteredSlots.clear();
                                    System.out.println("in else iiiii " + filteredSlots);

                                }
                                System.out.println("Is weekof else" + isWeeklyOff(otherUser) + " slotes " + otherUser.getFirstName());
                                setSlotsAdapter();


                                return;
                            }
                            System.out.println("Is weekof today" + isWeeklyOff(otherUser) + " slotes " + otherUser.getFirstName());
                            if (!isoffday) {
                                updateUserSlots(otherUser);

                                //updateOtherUserAppointments(appointment.getPhone());
                                updateOtherUserAppointments(otherContact.getNumber());
                            }
                        } else {
                            updateAvailableSlots();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Utility.hideProgress(dialog);
                        Utility.createAlert(SelectDateAcitivity.this, Utility.ERROR_CONNECTION);
                        System.out.println("## Error in blocking slots .." + e);
                        e.printStackTrace();
                    }
                });
            }


        } else {
            System.out.println("Hide progress");
            //dialog.dismiss();
            updateAvailableSlots();
        }

        System.out.println("Hide progress");
//        dialog.dismiss();
    }


    private boolean isWeeklyOff(User otherUser) {
        System.out.println("week off days " + otherUser.getSelectedDays());

        if (otherUser == null || otherUser.getSelectedDays() == null || otherUser.getSelectedDays().trim().length() == 0) {
            return false;
        }

        String[] days = otherUser.getSelectedDays().split(",");
        if (days.length == 0) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        System.out.println("is weekly date " + appointment.getDate());
        cal.setTime(Utility.formatDate(appointment.getDate(), Utility.DATE_FORMAT_USED));
        String[] mTestArray = getResources().getStringArray(R.array.off_days);
        String today = mTestArray[cal.get(Calendar.DAY_OF_WEEK) - 1];

        System.out.println("is weekly date " + today);

        for (String day : days) {
            if (day.equalsIgnoreCase(today)) {
                return true;
            }
        }
        return false;
    }

    private void updateOtherUserAppointments(String number) {
        //dialog = Utility.showProgress(SelectDateAcitivity.this);

        System.out.println("updateOtherUserAppointments " + number);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(number).collection(FirebaseUtil.DOC_APPOINTMENTS).whereEqualTo("date", appointment.getDate()).whereEqualTo("appointmentStatus", Utility.APP_STATUS_ACTIVE).
                orderBy("startTime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //Utility.hideProgress(dialog);
                // Utility.hideProgress(dialog);
                System.out.println("Done fetching users apps!" + task.getResult().size());
                blockSlots(task);
                updateAvailableSlots();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Utility.hideProgress(dialog);
                Utility.createAlert(SelectDateAcitivity.this, Utility.ERROR_CONNECTION);
                System.out.println("Failed to fetch apps for other user =>" + e.getMessage());
                e.printStackTrace();
            }
        })

        ;
    }

    private void updateAvailableSlots() {
        System.out.println("SSSSSSSSSSS Rohit");

        noSlots.setVisibility(View.INVISIBLE);
        Set<String> availableSlots = new HashSet<>();
        for (String slot : slotsList) {
            if (slotBeforeCurrentTime(slot)) {
                continue;
            }
            int index = slotsList.indexOf(slot);
            if ((index + 1) < slotsList.size()) {
                String slotString = slot + Utility.SLOT_APPENDER + slotsList.get(index + 1);
                if (blockedSlots.contains(slotString)) {
                    continue;
                }
                availableSlots.add(slotString);
                Utility.hideProgress(dialog);
                System.out.println(" -- Added slot ---" + availableSlots);
            }
        }
        filteredSlots = new ArrayList<>(availableSlots);
        Collections.sort(filteredSlots);
        //Utility.hideProgress(dialog);

        setSlotsAdapter();

        //availableSlotsListView.setOnItemSelectedListener(new SlotsListener);

    }

    private void setSlotsAdapter() {
        isoffday = true;

        System.out.println("Size of filterslots " + filteredSlots);

        if (filteredSlots == null || filteredSlots.size() == 0) {
            Utility.hideProgress(dialog);
            noSlots.setVisibility(View.VISIBLE);


            noSlots.setText("No slots are available on this date");

        } else {

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, filteredSlots);
            availableSlotsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            availableSlotsListView.setAdapter(adapter);
            availableSlotsListView.setItemsCanFocus(false);
            availableSlotsListView.setOnItemClickListener(new SlotsSelected());
        }
    }

    private boolean slotBeforeCurrentTime(String slot) {
        if (appointment.getDate() == null) {
            return true;
        }
        if (Utility.isSameDay(appointment.getDate())) {
            System.out.println("Same day for " + slot);
            Date date = Utility.convertToDate(slot, appointment.getDate());
            System.out.println("Date slot =>" + date);
            if (date != null && date.getTime() < new Date().getTime()) {
                return true;
            }
        }
        return false;
    }


    public class SlotsSelected implements AdapterView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            //CheckBox chk = (CheckBox) v
            CheckedTextView ctv = (CheckedTextView) arg1;
            String selectedSlot = ctv.getText().toString();

            System.out.println("wwwwwwww " + arg1);
            System.out.println("click text " + ctv.getText() + " " + ctv.isChecked());

            if (ctv.isChecked()) {

                if (validateSlots(selectedSlot)) {

                    selectedSlots.add(selectedSlot);

                    System.out.println("click slots" + selectedSlots);

                    // Toast.makeText(SelectDateAcitivity.this, "now it is unchecked", Toast.LENGTH_SHORT).show();
                } else {

                    System.out.println("Else " + selectedSlot + ctv.isChecked());
                    ctv.setChecked(false);
                    ctv.setSelected(false);
                    availableSlotsListView.setItemChecked(arg2, false);


                    //                System.out.println("Else after"+selectedSlot+ctv.isChecked());


                }
            } else {

                selectedSlots.remove(selectedSlot);
                //ctv.setChecked(false);
                System.out.println("click slots else" + selectedSlots);
                //Toast.makeText(SelectDateAcitivity.this, "now it is checked", Toast.LENGTH_SHORT).show();
            }
            setSlotsSelected();

            //removeInvalidSlots();
        }

    }

    private boolean validateSlots(String selectedSlot) {
        if (selectedSlots == null || selectedSlots.size() == 0) {
            return true;
        }
        //Validate that the current slot is only one unit ahead or behind the max and min selected Slots
        Integer selectedStart = slotsList.indexOf(splitSlot(selectedSlot, 0));
        Integer selectedEnd = slotsList.indexOf(splitSlot(selectedSlot, 1));
        List<String> list = new ArrayList<>(selectedSlots);
        Collections.sort(list);
        String startSlot = splitSlot(list.get(0), 0);
        Integer startIndex = slotsList.indexOf(startSlot);
        String endSlot = splitSlot(list.get(list.size() - 1), 1);
        Integer endIndex = slotsList.indexOf(endSlot);
        //Check if current slot is just before the selected first
        Integer diff = (startIndex - selectedEnd);
        if (Math.abs(diff) <= 0) {
            return true;
        }
        diff = (selectedStart - endIndex);
        if (Math.abs(diff) <= 0) {
            return true;
        }

        Utility.createAlert(SelectDateAcitivity.this, "Incorrect slot selection! Please select continuous slots.");
        return false;

    }

    private void setSlotsSelected() {
        if (selectedSlots == null || selectedSlots.size() == 0) {
            slotsSelected.setText("No slots selected");
            return;
        }
        List<String> list = new ArrayList<>(selectedSlots);
        Collections.sort(list);
        String startSlot = splitSlot(list.get(0), 0);
        String endSlot = splitSlot(list.get(list.size() - 1), 1);
        slotsSelected.setText(startSlot + Utility.SLOT_APPENDER + endSlot);


    }

    /*private void removeInvalidSlots() {
        if(filteredSlots == null || selectedSlots == null) {
            return;
        }

        for(String slot: filteredSlots) {
            if(!selectedSlots.contains(slot)) {
                int position = adapter.getPosition(slot);
                if(position > 0) {
                    CheckedTextView ctv = (CheckedTextView) availableSlotsListView.getitem
                    if(ctv != null) {
                        ctv.setChecked(false);
                    }
                }

            }
        }
    }*/

    public void prepareAppointmentSlots() {
        if (selectedSlots == null || selectedSlots.size() == 0) {
            return;
        }
        List<String> list = new ArrayList<>(selectedSlots);
        Collections.sort(list);
        appointment.setStartTime(list.get(0).split(Utility.SLOT_APPENDER)[0]);
        appointment.setEndTime(list.get(list.size() - 1).split(Utility.SLOT_APPENDER)[1]);

        System.out.println("Appointment timings set as ==> " + appointment);

    }

    private String splitSlot(String slotString, int index) {
        if (slotString == null) {
            return "";
        }
        return slotString.split(Utility.SLOT_APPENDER)[index];
    }


}
