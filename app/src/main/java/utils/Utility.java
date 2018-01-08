package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

    public static void saveStringToSharedPreferences(String input, String label, Activity context) {
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefEditor.putString(label, input);
        prefEditor.apply();

    }

    public static User getUserFromSharedPrefs(Activity context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String myStrValue = prefs.getString(INTENT_VAR_USER, null);
        if (myStrValue != null) {
            return new Gson().fromJson(myStrValue, User.class);
        }
        return null;
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
}
