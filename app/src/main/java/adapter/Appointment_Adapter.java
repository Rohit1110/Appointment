package adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.util.ArrayList;

import model.Appointment;

/**
 * Created by Rohit on 12/26/2017.
 */

public class Appointment_Adapter extends ArrayAdapter<Appointment> {
private Activity context=null;
TextView time,name,phone;
ArrayList item;

    public Appointment_Adapter(@NonNull Activity context, ArrayList items) {
        super(context, R.layout.layout_appointment_row,items);
        this.context=context;
        this.item=items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.layout_appointment_row, null, true);
        time=(TextView)rowView.findViewById(R.id.txttime);
        name=(TextView)rowView.findViewById(R.id.txtname);
        phone=(TextView)rowView.findViewById(R.id.txtphone);
     Appointment list=getItem(position);
        System.out.println("Length "+ list.getName().length());
        time.setText(list.getTime());
        name.setText(list.getName());
        phone.setText(list.getPhone());


        return rowView;
    }
}
