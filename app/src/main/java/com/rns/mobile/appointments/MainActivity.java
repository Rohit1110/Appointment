package com.rns.mobile.appointments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.Calendar;

import model.User;
import utils.FirebaseUtil;
import utils.Utility;

public class MainActivity extends AppCompatActivity {

    private String phoneNumber;
    private User user;
    private ProgressDialog dialog;
    private String TAG = "MainActivity Appointments";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TODO: read from auth
        phoneNumber = FirebaseUtil.getMobile();

        System.out.println("#### LOADING PROFILE FOR USER " + phoneNumber + ".. ###");

        //startCalendar();

        dialog = Utility.showProgress(MainActivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Utility.hideProgress(dialog);
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc == null || !doc.exists()) {
                        System.out.println("No document found for this number!" + phoneNumber);
                        showEditProfile();
                    } else {
                        System.out.println("Document found!");
                        user = doc.toObject(User.class);
                        if (user == null || user.getFirstName() == null || user.getFirstName().trim().length() == 0) {
                            showEditProfile();
                        } else {
                            Utility.saveStringToSharedPreferences(new Gson().toJson(user), Utility.INTENT_VAR_USER, MainActivity.this);
                            Intent i = new Intent(MainActivity.this, AppointmentsActivity.class);
                            i.putExtra(Utility.INTENT_VAR_USER, new Gson().toJson(user));
                            startActivity(i);
                        }
                    }
                } else {
                    System.out.println("No document found for this number!!" + task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.hideProgress(dialog);
                System.out.println("### ERROR IN READING PROFILE ---" + e);
                e.printStackTrace();
            }
        });
    }

    private void showEditProfile() {
        Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
        i.putExtra("user", new Gson().toJson(user));
        startActivity(i);

    }

    private void saveToken(String fcmToken) {
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).update("fcmToken", fcmToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.hideProgress(dialog);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("EDIT", "Error writing document", e);
            }
        });
    }

    private void startCalendar() {
        /*System.out.println("Started calendar ...");
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(2018, 0, 10, 20, 20);
        long startTime = beginCal.getTimeInMillis();

        Calendar endCal = Calendar.getInstance();
        endCal.set(2018, 0, 10, 20, 30);
        long endTime = endCal.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, "Test3");
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Test3");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginCal.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.ALL_DAY, 0);
        intent.putExtra(CalendarContract.Events.STATUS, 1);
        intent.putExtra(CalendarContract.Events.VISIBLE, 1);
        intent.putExtra(CalendarContract.Events.HAS_ALARM, 1);
        intent.putExtra(CalendarContract.ACTION_EVENT_REMINDER, 1);
        intent.putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        intent.putExtra(CalendarContract.Reminders.MINUTES, 1);
        //intent.putExtra(CalendarContract.Events.intent.putExtra(CalendarContract.Events.RE)
        startActivity(intent);
        System.out.println("Started calendar ....");*/
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(2018, 0, 10, 20, 17);
        long startTime = beginCal.getTimeInMillis();

        //addAppointmentsToCalender("Rohit APP", "Rohit DESC", "PUNE", 1, startTime, true, false);

    }


    public long addAppointmentsToCalender(String title,
                                          String desc, String place, int status, long startDate,
                                          boolean needReminder, boolean needMailService) {
/***************** Event: add event *******************/
        long eventID = -1;
        try {
            String eventUriString = "content://com.android.calendar/events";
            ContentValues eventValues = new ContentValues();
            eventValues.put("calendar_id", 1); // id, We need to choose from
            // our mobile for primary its 1
            eventValues.put("title", title);
            eventValues.put("description", desc);
            eventValues.put("eventLocation", place);

            long endDate = startDate + 1000 * 10 * 10; // For next 10min
            eventValues.put("dtstart", startDate);
            eventValues.put("dtend", endDate);

            // values.put("allDay", 1); //If it is bithday alarm or such
            // kind (which should remind me for whole day) 0 for false, 1
            // for true
            eventValues.put("eventStatus", status); // This information is
            // sufficient for most
            // entries tentative (0),
            // confirmed (1) or canceled
            // (2):
            eventValues.put("eventTimezone", "UTC/GMT +5:30");
 /*
  * Comment below visibility and transparency column to avoid
  * java.lang.IllegalArgumentException column visibility is invalid
  * error
  */
            // eventValues.put("allDay", 1);
            // eventValues.put("visibility", 0); // visibility to default (0),
            // confidential (1), private
            // (2), or public (3):
            // eventValues.put("transparency", 0); // You can control whether
            // an event consumes time
            // opaque (0) or transparent (1).

            eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

            Uri eventUri = getApplicationContext()
                    .getContentResolver()
                    .insert(Uri.parse(eventUriString), eventValues);
            eventID = Long.parseLong(eventUri.getLastPathSegment());

            if (needReminder) {
                /***************** Event: Reminder(with alert) Adding reminder to event ***********        ********/

                String reminderUriString = "content://com.android.calendar/reminders";
                ContentValues reminderValues = new ContentValues();
                reminderValues.put("event_id", eventID);
                reminderValues.put("minutes", 5); // Default value of the
                // system. Minutes is a integer
                reminderValues.put("method", 1); // Alert Methods: Default(0),
                // Alert(1), Email(2),SMS(3)

                Uri reminderUri = getApplicationContext()
                        .getContentResolver()
                        .insert(Uri.parse(reminderUriString), reminderValues);
            }

/***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

            if (needMailService) {
                String attendeuesesUriString = "content://com.android.calendar/attendees";
                /********
                 * To add multiple attendees need to insert ContentValues
                 * multiple times
                 ***********/
                ContentValues attendeesValues = new ContentValues();
                attendeesValues.put("event_id", eventID);
                attendeesValues.put("attendeeName", "xxxxx"); // Attendees name
                attendeesValues.put("attendeeEmail", "yyyy@gmail.com");// Attendee Email
                attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
                // Relationship_None(0),
                // Organizer(2),
                // Performer(3),
                // Speaker(4)
                attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
                // Required(2),
                // Resource(3)
                attendeesValues.put("attendeeStatus", 0); // NOne(0),
                // Accepted(1),
                // Decline(2),
                // Invited(3),
                // Tentative(4)

                Uri eventsUri = Uri.parse("content://calendar/events");
                Uri url = getApplicationContext()
                        .getContentResolver()
                        .insert(eventsUri, attendeesValues);

                // Uri attendeuesesUri = curActivity.getApplicationContext()
                // .getContentResolver()
                // .insert(Uri.parse(attendeuesesUriString), attendeesValues);
            }
        } catch (Exception ex) {
            System.out.println("Error in adding event on calendar" + ex.getMessage());
        }

        return eventID;

    }

}
