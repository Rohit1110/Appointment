package adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.util.ArrayList;
import java.util.List;

import model.Appointment;

/**
 * Created by Rohit on 12/26/2017.
 */

public class Appointment_Adapter extends RecyclerView.Adapter<Appointment_Adapter.MyViewHolder> {
    List<Appointment> item;
    private Context mContext=null;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView time,name,phone;


        public MyViewHolder(View itemView) {
            super(itemView);
            time=(TextView)itemView.findViewById(R.id.txttime);
            name=(TextView)itemView.findViewById(R.id.txtname);
           // phone=(TextView)itemView.findViewById(R.id.txtphone);

        }



    }
    public Appointment_Adapter(Context mContext, List<Appointment> item){
        this.mContext = mContext;
        this.item = item;
        System.out.println("item list:"+item);
    }

        @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_appointment_row, parent, false);
            return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Appointment album = item.get(position);
        holder.name.setText(album.getName());
        holder.time.setText(album.getTime());
        System.out.println("name "+ album.getName());
        System.out.println("time "+ album.getTime());


    }

    @Override
    public int getItemCount() {
        return item.size();
    }
}
