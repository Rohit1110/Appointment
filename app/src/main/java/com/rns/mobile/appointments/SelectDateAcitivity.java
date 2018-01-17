package com.rns.mobile.appointments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import model.Appointment;
import model.User;
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
    private User otherUser;
    private ListView availableSlotsListView;
    private ArrayAdapter<String> adapter;
    private Calendar myCalendar = Calendar.getInstance();
    private List<String> slotsList;
    private Set<String> blockedSlots;
    private String userPhone;
    private Set<String> selectedSlots;
    private List<String> filteredSlots;
    private EditText reason;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);
        // book = (Button) findViewById(R.id.btnbook);
        setdate = (TextView) findViewById(R.id.selecteddate);
        reason = (EditText) findViewById(R.id.edit_reason);
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
                today.setTextColor(getResources().getColor(R.color.colorAccent));
                tomorrow.setTextColor(getResources().getColor(R.color.album_title));
                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat(Utility.DATE_FORMAT_DISPLAY);
                String dateString = sdf.format(date);
                setdate.setText(dateString);

                appointment.setDate(Utility.formatToUsedDate(dateString));
                updateUserAppointments();
            }
        });

        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                appointment.setDate(Utility.formatToUsedDate(formattedDate));
                updateUserAppointments();
            }
        });

        appointmentPhone = (TextView) findViewById(R.id.txt_appointment_phone);
        //selectedDate = (DatePicker) findViewById(R.id.datepicker);
        availableSlotsListView = (ListView) findViewById(R.id.list_availableslot);
        slotsSelected = (TextView) findViewById(R.id.txt_slot_selected);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, slotsList);
        availableSlotsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        availableSlotsListView.setAdapter(adapter);
        availableSlotsListView.setOnItemClickListener(new SlotsSelected());

        appointment = Utility.extractAppointment(SelectDateAcitivity.this);

        blockedSlots = new HashSet<String>();
        slotsList = Arrays.asList(Utility.TIME_SLOTS);
        userPhone = FirebaseUtil.getMobile();
        selectedSlots = new HashSet<>();
        System.out.println("Slot list initialized = " + slotsList);

        if (appointment != null) {
            if (appointment.getName() != null) {
                appointmentPhone.setText(appointment.getName());
            } else {
                appointmentPhone.setText(appointment.getPhone());
            }
        }


        appointment.setDate(Utility.formatToUsedDate(dateString));
        updateUserAppointments();
    }

    /*public void startCalendar() {*//*
        System.out.println("Started calendar ...");
        //Date time = Utility.convertToDate(appointment.getStartTime(), appointment.getDate());
        Calendar beginCal = Calendar.getInstance();
        int year=2018,mnth=0,day=10,hrs=19,min=15;
        beginCal.set(year, mnth, day, hrs, min);
        beginCal.add(Calendar.MINUTE, REMINDER_BEFORE);
        long startTime = beginCal.getTimeInMillis();

        Calendar endCal = Calendar.getInstance();
        endCal.set(year, mnth, day, 19,30 );
        long endTime = endCal.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

            intent.putExtra(CalendarContract.Events.TITLE, "title rohit");

        intent.putExtra(CalendarContract.Events.DESCRIPTION, "title");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "pune");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginCal.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.ALL_DAY, 0);
        intent.putExtra(CalendarContract.Events.STATUS, 1);
        intent.putExtra(CalendarContract.Events.VISIBLE, 1);
        intent.putExtra(CalendarContract.Events.HAS_ALARM, 1);
        intent.putExtra(CalendarContract.ACTION_EVENT_REMINDER, 1);
        intent.putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        intent.putExtra(CalendarContract.Reminders.MINUTES, -15);
        //intent.putExtra(CalendarContract.Events.intent.putExtra(CalendarContract.Events.RE)
        startActivity(intent);*//*

        // Date time = Utility.convertToDate(appointment.getStartTime(), appointment.getDate());
        Calendar beginCal = Calendar.getInstance();
        int year = 2018, mnth = 0, day = 10, hrs = 20, min = 15;
        beginCal.set(year, mnth, day, hrs, min);
        beginCal.add(Calendar.MINUTE, REMINDER_BEFORE);
        long startTime = beginCal.getTimeInMillis();

        Calendar endCal = Calendar.getInstance();
        endCal.set(year, mnth, day, 20, 30);
        long endTime = endCal.getTimeInMillis();


        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.TITLE, "Rohit New");
        values.put(CalendarContract.Events.DESCRIPTION, "Calender Add");

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

// Default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1);

        values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL="
                + endTime);
// Set Period for 1 Hour
        values.put(CalendarContract.Events.DURATION, "+P1H");

        values.put(CalendarContract.Events.HAS_ALARM, 1);

// Insert event to calendar
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            System.out.println("Call Calender");
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        System.out.println("Call Calender2222");

    }*/


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
        prepareAppointmentSlots();
        if (!reason.getText().toString().equals("")) {
            appointment.setDescription(reason.getText().toString());
            System.out.println("Reason =>" + reason.getText().toString());
        }


        if (selectedSlots != null && selectedSlots.size() > 0) {
            appointment.setAppointmentStatus(Utility.APP_STATUS_ACTIVE);
            dialog = Utility.showProgress(SelectDateAcitivity.this);
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(userPhone).
                    collection(FirebaseUtil.DOC_APPOINTMENTS).document(appointment.toString()).set(appointment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Completed Booking for user!!!" + userPhone);

                    System.out.println("Appointment added successfully!!" + appointment);


                    User currentUser = Utility.getUserFromSharedPrefs(SelectDateAcitivity.this);

                    appointment.setId(appointment.toString());
                    final Appointment otherUserAppointment = appointment.duplicate(userPhone);
                    if (currentUser != null) {
                        otherUserAppointment.setName(currentUser.getFullName());
                    }
                    FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).
                            collection(FirebaseUtil.DOC_APPOINTMENTS).
                            document(appointment.toString()).set(otherUserAppointment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Completed booking for other user!!!" + appointment.getPhone());
                            Utility.hideProgress(dialog);
                            System.out.println("Appointment added successfully!!" + otherUserAppointment);
                            Appointment duplicate = appointment.duplicate(appointment.getPhone());
                            duplicate.setName(otherUserAppointment.getName());
                            new NotificationTask(duplicate, Utility.NOTIFICATION_TYPE_NEW).sendNotification();
                            goToHome();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Appointment failed to add => " + e);
                            e.printStackTrace();
                        }
                    });


                }
            }) /*{
            @Override public void onComplete (@NonNull Task < DocumentReference > task) {

            }*//*
        })*/.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Appointment failed to add => " + e);
                    e.printStackTrace();
                }
            });
        } else {
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
        startActivity(i);
        finish();
    }


    private void updateUserSlots(User user) {
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
                Utility.hideProgress(dialog);
                System.out.println("Done fetching users apps for " + userPhone + " == " + task.getResult().size() + " Success:" + task.isSuccessful());
                blockSlots(task);
                blockOtherUserSlots();
            }
        });
    }

    private void blockSlots(@NonNull Task<QuerySnapshot> task) {
        if (task.getResult() != null && task.getResult().size() > 0) {
            System.out.println("Blocking slots for user");
            for (DocumentSnapshot doc : task.getResult()) {
                if (!doc.exists()) {
                    continue;
                }
                Appointment appointment = doc.toObject(Appointment.class);
                if (appointment != null) {
                    System.out.println(appointment);
                    if (appointment.getStartTime() != null && slotsList.contains(appointment.getStartTime())) {
                        //dataAdapter.remove(appointment.getStartTime());
                        System.out.println("Blocking slots for .." + appointment);
                        Integer index = slotsList.indexOf(appointment.getStartTime());
                        index++;
                        while (index < slotsList.size()) {
                            String blockedSlot = slotsList.get(index - 1) + Utility.SLOT_APPENDER + slotsList.get(index);
                            blockedSlots.add(blockedSlot);
                            if (slotsList.get(index).compareTo(appointment.getEndTime()) >= 0) {
                                break;
                            }
                            index++;
                        }

                    }

                }
            }
            System.out.println("Blocked slots after user =>" + blockedSlots);
        } else {
            System.out.println("ERROR in fetching users apps =>" + task.getException());
        }
        updateAvailableSlots();
        System.out.println("Blocked slots after user =>" + blockedSlots);
    }

    private void blockOtherUserSlots() {
        System.out.println("Blocking other user slots .." + appointment);
        if (appointment != null && appointment.getPhone() != null) {
            if (!appointment.getPhone().trim().contains("+")) {
                appointment.setPhone(Utility.COUNTRY_CODE + appointment.getPhone());
            }
            System.out.println("## Loading profile for .. " + appointment.getPhone() + " dialog=" + dialog);
            dialog = Utility.showProgress(SelectDateAcitivity.this);
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Utility.hideProgress(dialog);
                    System.out.println("## Done profile .." + task.getResult().exists());
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        otherUser = task.getResult().toObject(User.class);
                        otherUser.setPhone(appointment.getPhone());
                        if (appointment.getName() == null) {
                            appointment.setName(otherUser.getFirstName() + " " + otherUser.getLastName());
                            appointmentPhone.setText(appointment.getName());
                        }
                        System.out.println("Appointment for =>" + appointment.getName());
                        //Update slotsList based on this other users available slotsList
                        if (isWeeklyOff(otherUser)) {
                            filteredSlots.clear();
                            setSlotsAdapter();
                            return;
                        }

                        updateUserSlots(otherUser);
                        updateOtherUserAppointments();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("## Error in blocking slots .." + e);
                    e.printStackTrace();
                }
            });


        } else {
            updateAvailableSlots();
        }
    }

    private boolean isWeeklyOff(User otherUser) {
        if (otherUser == null || otherUser.getSelectedDays() == null || otherUser.getSelectedDays().trim().length() == 0) {
            return false;
        }

        String[] days = otherUser.getSelectedDays().split(",");
        if (days.length == 0) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        String[] mTestArray = getResources().getStringArray(R.array.off_days);
        String today = mTestArray[cal.get(Calendar.DAY_OF_WEEK) - 1];

        for (String day : days) {
            if (day.equalsIgnoreCase(today)) {
                return true;
            }
        }
        return false;
    }

    private void updateOtherUserAppointments() {
        dialog = Utility.showProgress(SelectDateAcitivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).collection(FirebaseUtil.DOC_APPOINTMENTS).whereEqualTo("date", appointment.getDate()).whereEqualTo("appointmentStatus", Utility.APP_STATUS_ACTIVE).
                orderBy("startTime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Utility.hideProgress(dialog);
                System.out.println("Done fetching users apps!" + task.getResult().size());
                blockSlots(task);
                updateAvailableSlots();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to fetch apps for other user =>" + e.getMessage());
                e.printStackTrace();
            }
        })

        ;
    }

    private void updateAvailableSlots() {
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
                System.out.println(" -- Added slot ---" + availableSlots);
            }
        }
        filteredSlots = new ArrayList<>(availableSlots);
        Collections.sort(filteredSlots);
        setSlotsAdapter();
        //availableSlotsListView.setOnItemSelectedListener(new SlotsListener);

    }

    private void setSlotsAdapter() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, filteredSlots);
        availableSlotsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        availableSlotsListView.setAdapter(adapter);

        availableSlotsListView.setOnItemClickListener(new SlotsSelected());
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
            CheckedTextView ctv = (CheckedTextView) arg1;
            String selectedSlot = ctv.getText().toString();
            if (ctv.isChecked()) {
                if (validateSlots(selectedSlot)) {
                    selectedSlots.add(selectedSlot);
                    System.out.println(selectedSlots);
                    // Toast.makeText(SelectDateAcitivity.this, "now it is unchecked", Toast.LENGTH_SHORT).show();
                } else {
                    ctv.setChecked(false);
                    ctv.setSelected(false);
                }

            } else {
                selectedSlots.remove(selectedSlot);
                System.out.println(selectedSlots);
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
