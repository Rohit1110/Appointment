package com.rns.mobile.appointments;


import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.widget.Toast;


/**
 * Created by Rohit on 1/9/2018.
 */

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Toast in Alarmreceiver",Toast.LENGTH_LONG).show();
    }
}
