package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.Appointment;
import utils.Utility;

/**
 * Created by Rohit on 12/26/2017.
 */

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.MyViewHolder> {
    List<Appointment> item;
    private Context mContext = null;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView description;
        private TextView time, name, date;


        public MyViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.txttime);
            name = (TextView) itemView.findViewById(R.id.txtname);
            date = (TextView) itemView.findViewById(R.id.txtdate);
            description = (TextView) itemView.findViewById(R.id.txtdecription);

        }


    }

    public AppointmentsAdapter(Context mContext, List<Appointment> item) {
        this.mContext = mContext;
        this.item = item;
        System.out.println("item list:" + item);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appointment_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Appointment appointment = item.get(position);
        if (appointment.getName() != null && !appointment.getName().trim().equals("")) {
            holder.name.setText(appointment.getName());
        } else {
            holder.name.setText(appointment.getPhone());
        }
        String dateformat= Utility.formatToUsedDate(appointment.getDate());
        System.out.println(dateformat+" new Date Format");
        //holder.date.setText(new SimpleDateFormat(Utility.DATE_FORMAT_DISPLAY).format(appointment.getDate()));
        SimpleDateFormat sdf = new SimpleDateFormat(Utility.DATE_FORMAT_USED);
        Date dates = null;
        try {
            dates = sdf.parse(appointment.getDate());
            if (dates != null) {
                holder.date.setText(new SimpleDateFormat(Utility.DATE_FORMAT_DISPLAY).format(dates));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.time.setText(appointment.getStartTime() + " - " + appointment.getEndTime());
        holder.description.setText(appointment.getDescription());
        System.out.println("name in Adapter " + appointment.getName());
        System.out.println("time in Adapter " + appointment.getStartTime() + " - " + appointment.getEndTime());


    }

    @Override
    public int getItemCount() {
        return item.size();
    }
}
