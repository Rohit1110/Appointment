package utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.DatePicker;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.Appointment;
import model.User;

/**
 * Created by Rohit on 11/27/2017.
 */

public class Utility {

    public static final String[] TIME_SLOTS = new String[]{"00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30", "24:00"};
    //public static final String[] TIME_SLOTS_TO = new String[]{"Select To", "0:00", "0:30", "1:00", "1:30", "2:00", "2:30", "3:00", "3:30", "4:00", "4:30", "5:00", "5:30", "6:00", "6:30", "7:00", "7:30", "8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30", "24:00"};
    public static final String INTENT_VAR_OTHER_USER = "otherUser";
    public static final String INTENT_VAR_APPOINTMENT = "appointment";
    public static final String INTENT_VAR_USER = "user";
    public static final String COUNTRY_CODE = "+91";
    public static final String SLOT_APPENDER = " - ";
    public static final int PHONE_MAX_LENGTH = 10;
    public static final String DATE_FORMAT_DISPLAY = "dd MMM yyyy";
    public static final String DATE_FORMAT_USED = "yyyy-MM-dd";
    public static final String APP_STATUS_ACTIVE = "Active";
    public static final String APP_STATUS_CANCELLED = "Cancelled";
    public static final int REMINDER_BEFORE = 15;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 123;
    public static final String CALENDAR_CONTENT_URI = "content://com.android.calendar/events";
    public static final String NOTIFICATION_TYPE_NEW = "NEW_APP";
    public static final String NOTIFICATION_TYPE_CANCEL = "CANCEL_APP";
    public static final String ERROR_CONNECTION = "Error connecting server ..";


    public static void createAlert(Context context, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void saveFile(Context context, Bitmap b, String picName) throws IOException {
        FileOutputStream fos;

        fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
        b.compress(Bitmap.CompressFormat.PNG, 100, fos);

        fos.close();

    }
//Internet Connection Check

    public static boolean isInternetOn(Context ctx) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {

            // if connected with internet


            return true;

        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED || connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }

    public static ProgressDialog showProgress(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Loading ..");
        //dialog.setMessage("");
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static void hideProgress(ProgressDialog dialog) {
        System.out.println("Hiding dialog .." + dialog);
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    public static String getDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        return createDate(day, month, year);
    }

    @NonNull
    public static String createDate(int day, int month, int year) {
        month++;
        String dayString = "" + day;
        if (day < 10) {
            dayString = "0" + day;
        }

        String monthString = "" + month;
        if (month < 10) {
            monthString = "0" + month;
        }

        return year + "-" + monthString + "-" + dayString;
    }

    public static Appointment extractAppointment(Activity context) {
        String appJson = context.getIntent().getStringExtra(INTENT_VAR_APPOINTMENT);
        if (appJson != null) {
            System.out.println("" + appJson);
            return new Gson().fromJson(appJson, Appointment.class);
        }
        return null;
    }

    public static User extractUser(Activity context) {
        String userJson = context.getIntent().getStringExtra(INTENT_VAR_OTHER_USER);
        if (userJson != null) {
            System.out.println("" + userJson);
            return new Gson().fromJson(userJson, User.class);
        }
        return null;
    }

    public static void saveStringToSharedPreferences(String input, String label, Context context) {
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefEditor.putString(label, input);
        prefEditor.apply();

    }

    public static User getUserFromSharedPrefs(Activity context) {
        String myStrValue = getSharedString(context, INTENT_VAR_USER);
        if (myStrValue != null) {
            return new Gson().fromJson(myStrValue, User.class);
        }
        return null;
    }

    private static String getSharedString(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, null);
    }


    public static String getStringValue(String value) {
        if (value == null || value.trim().length() == 0) {
            return "";
        }
        return value.trim();
    }

    public static String removeAllSpaces(String phone) {
        if (phone == null) {
            return "";
        }
        return phone.trim().replaceAll("\\s+", "");
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        String formattedDate = df.format(date.getTime());
        return formattedDate;
    }

    public static Date formatDate(String date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date formattedDate = null;
        try {
            formattedDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }


    //For Date comparison
    public static String CompareDate(Date startDate, Date today) {
        String past = null, feature = null, present = null;
        long st_dateTime_millis = startDate.getTime();
        long today_datetime_millis = today.getTime();
        System.out.println("start dates " + st_dateTime_millis + " today " + today_datetime_millis);
        if (today_datetime_millis >= st_dateTime_millis) {
            return "past";
        } else if (today_datetime_millis <= st_dateTime_millis) {
            return "future";
        } else if (today_datetime_millis == st_dateTime_millis) {
            return "present";
        }
        return "not";
    }


    //end comparisions

    public static String formatToUsedDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            Date date = new SimpleDateFormat(DATE_FORMAT_DISPLAY).parse(dateString);
            if (date != null) {
                return new SimpleDateFormat(DATE_FORMAT_USED).format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSameDay(String selectedDate) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        try {
            cal1.setTime(new SimpleDateFormat(DATE_FORMAT_USED).parse(selectedDate));
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static Date convertToDate(String slot, String date) {
        if (slot == null || date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat(DATE_FORMAT_USED).parse(date));
            String[] split = slot.split(":");
            cal.set(Calendar.HOUR_OF_DAY, new Integer(split[0]));
            cal.set(Calendar.MINUTE, new Integer(split[1]));
            return cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static long addAppointmentsToCalender(Context activity, Appointment appointment) {


/***************** Event: add event *******************/
        if (!checkPermission(activity)) {
            return -1;
        }

        if (caledarEventExists(activity, appointment)) {
            return -1;
        }

        System.out.println("Started calendar ...");
        Date startDate = convertToDate(appointment.getStartTime(), appointment.getDate());
        Date endDate = convertToDate(appointment.getStartTime(), appointment.getDate());
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(startDate);
        //beginCal.add(Calendar.MINUTE, );
        long startTime = beginCal.getTimeInMillis();
        long eventID = -1;
        try {
            String eventUriString = CALENDAR_CONTENT_URI;
            ContentValues eventValues = new ContentValues();
            eventValues.put("calendar_id", 1); // id, We need to choose from
            // our mobile for primary its 1
            eventValues.put("title", appointment.getName());
            eventValues.put("description", appointment.getDescription());
            eventValues.put("eventLocation", "");

            //long endDate = startTime + 1000 * 10 * 10; // For next 10min

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            long endTime = endCal.getTimeInMillis();

            eventValues.put("dtstart", startTime);
            eventValues.put("dtend", endTime);

            // values.put("allDay", 1); //If it is bithday alarm or such
            // kind (which should remind me for whole day) 0 for false, 1
            // for true
            eventValues.put("eventStatus", 1); // This information is
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
            // an event consumes startDate
            // opaque (0) or transparent (1).

            eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

            Uri eventUri = activity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
            eventID = Long.parseLong(eventUri.getLastPathSegment());

            System.out.println("Writing event ID =>" + eventID);

            //

            //eventValues.put("_id", appointment.getId());

            //if (needReminder) {
            /***************** Event: Reminder(with alert) Adding reminder to event ***********        ********/

            String reminderUriString = "content://com.android.calendar/reminders";
            ContentValues reminderValues = new ContentValues();
            reminderValues.put("event_id", eventID);
            reminderValues.put("minutes", REMINDER_BEFORE); // Default value of the
            // system. Minutes is a integer
            reminderValues.put("method", 1); // Alert Methods: Default(0),
            // Alert(1), Email(2),SMS(3)

            Uri reminderUri = activity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
            //}

            /***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

            saveStringToSharedPreferences(String.valueOf(eventID), appointment.getId(), activity);

            System.out.println("Added appointment -- " + appointment + " to the calendar - " + eventID);

        } catch (Exception ex) {
            System.out.println("Error in adding event on calendar - " + ex.getMessage());
        }

        return eventID;


    }

    public static boolean caledarEventExists(Context activity, Appointment appointment) {

        String event = getSharedString(activity, appointment.getId());
        if (event != null && event.trim().length() > 0) {
            return true;
        }

        if (!checkPermission(activity)) {
            return true;
        }

        try {
            Date startDate = convertToDate(appointment.getStartTime(), appointment.getDate());
            Date endDate = convertToDate(appointment.getEndTime(), appointment.getDate());

            if (startDate == null || endDate == null) {
                return false;
            }

            // +- 5 minutes to the slot
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(startDate);
            cal1.add(Calendar.MINUTE, -5);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(endDate);
            cal2.add(Calendar.MINUTE, 5);

            long begin = cal1.getTimeInMillis();
            long end = cal2.getTimeInMillis();
            String[] proj = new String[]{CalendarContract.Instances._ID, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.EVENT_ID};
            Cursor cursor = CalendarContract.Instances.query(activity.getContentResolver(), proj, begin, end, appointment.getName());
            if (cursor.getCount() > 0) {
                // deal with conflict
                while (cursor.moveToNext()) {
                    String eventId = cursor.getString(3);
                    Log.v("ID : ", cursor.getString(0));
                    Log.v("eventID : ", eventId);
                    if (eventId.equals(event)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void deleteAppointmentFromCalendar(Context activity, Appointment appointment) {
        if (!checkPermission(activity)) {
            return;
        }
        try {
            Uri deleteUri = getUri(activity, appointment);
            int rows = activity.getContentResolver().delete(deleteUri, null, null);
            System.out.println("Removed " + appointment + " from Calendar =>" + deleteUri);
        } catch (Exception e) {
            System.out.println("Error deleting from Calendar =>" + e);
        }

    }

    private static Uri getUri(Context activity, Appointment appointment) {
        Uri eventUri = Uri.parse(CALENDAR_CONTENT_URI);  // or
        Uri deleteUri = null;

        deleteUri = ContentUris.withAppendedId(eventUri, new Long(getSharedString(activity, appointment.getId())));
        return deleteUri;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(Context activity) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkcontactPermission(Activity activity) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getcurrentAppointment(String startTime, String endTime, Date dates, Date today) {
        System.out.println("Start: " + startTime + " End :" + endTime + " Dates : " + dates);
        String newDate = Utility.formatDate(dates, Utility.DATE_FORMAT_USED);
        Date start_time = convertToDate(startTime, newDate);
        Date end_time = convertToDate(endTime, newDate);
        long stime = start_time.getTime();
        long etime = end_time.getTime();
        long t_days = today.getTime();
        System.out.println("Start: " + stime + " End :" + etime + " t_days : " + t_days);
        if (stime <= t_days && etime >= t_days) {
            return "present";
        } else if (etime >= t_days) {

            return "future";
        }

        return "not";

    }

}
