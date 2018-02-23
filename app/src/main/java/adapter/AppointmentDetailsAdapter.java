package adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.util.List;
import java.util.Map;

import model.ActiveContact;
import model.Appointment;
import model.DetailContact;
import model.UserContact;

/**
 * Created by Rohit on 1/1/2018.
 */

public class AppointmentDetailsAdapter extends RecyclerView.Adapter<AppointmentDetailsAdapter.MyViewHoldercontact> {
    private Context mContext;
    private Appointment appointment;
    private DetailContact detailContact;

    private List<DetailContact> item, filterList;


    public class MyViewHoldercontact extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView phone, name,status;
        private ImageView callimage;
        private CheckBox selectContact;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        public MyViewHoldercontact(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            phone = (TextView) itemView.findViewById(R.id.txt_phone);
            name = (TextView) itemView.findViewById(R.id.txt_user);
            status=(TextView)itemView.findViewById(R.id.status);
            callimage=(ImageView)itemView.findViewById(R.id.call);
            //selectContact = (CheckBox) itemView.findViewById(R.id.chk_contact);


          /*  this.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send the text to the listener, i.e Activity.
                    mListener.onItemClicked(((TextView)v).getText());
                }
            });

            this.phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send the text to the listener, i.e Activity.
                    mListener.onItemClicked(((TextView)v).getText());
                }
            });*/

        }

        @Override
        public void onClick(View v) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                v.setSelected(false);

            } else {
                selectedItems.put(getAdapterPosition(), true);
                v.setSelected(true);
            }
            // Toast.makeText(mContext, "item clicked"+selectedItems.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();

        }
    }

    public AppointmentDetailsAdapter(Context mContext, List<DetailContact> detailContact) {
        this.mContext=mContext;

        this.item=detailContact;




    }

    public AppointmentDetailsAdapter(Context mContext, DetailContact detailContact) {
        this.mContext = mContext;
        //this.item = item;
        this.detailContact=detailContact;
        // this.filterList = new List<UserContact>();
        // we copy the original list to the filter list and use it for setting row values

        //System.out.println("item list:" + item);

    }



    public AppointmentDetailsAdapter(Context mContext, Appointment appointment) {
        this.mContext = mContext;
        //this.item = item;
        this.appointment=appointment;
        // this.filterList = new List<UserContact>();
        // we copy the original list to the filter list and use it for setting row values

        //System.out.println("item list:" + item);

    }

    // Define listener member variable
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(CharSequence text);
    }

    // Define the method that allows the parent activity or fragment to define the listener.
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }


    @Override
    public MyViewHoldercontact onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appoinmentdetails_row, parent, false);
        return new MyViewHoldercontact(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHoldercontact holder, int position) {
      final int pos=position;

        DetailContact detailContact = item.get(position);
        holder.name.setText(item.get(position).getContact());
        holder.phone.setText(item.get(position).getNumber());
        holder.status.setText(item.get(position).getStatus());

        holder.callimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(Manifest.permission.CALL_PHONE)) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + item.get(pos).getNumber()));
                    mContext.startActivity(intent);
                }else{
                    return;
                }
            }
        });




        /*for(int  i = 0; i < appointment.getContactList().size(); i++) {
            Map<String, String> contact = (Map<String, String>) appointment.getContactList().get(i);
            holder.name.setText(contact.get("contact"));
            holder.phone.setText(contact.get("number"));


        }*/







      /*  holder.selectContact.setOnCheckedChangeListener(null);
        holder.selectContact.setChecked(item.get(position).isSelected());

        holder.selectContact.setTag(item.get(position));
        holder.selectContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                InviteContact contact = (InviteContact) cb.getTag();
                contact.setSelected(cb.isChecked());

                item.get(pos).setSelected(cb.isChecked());


            }
        });*/

       // System.out.println("name in Adapter " + activeContact.getContact());
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }


  /*  public void updateList(List<ActiveContact> list) {
        item = list;
        notifyDataSetChanged();
    }*/


    @Override
    public int getItemCount() {
        return item.size();
    }

    public void Delete(int position)
    {
       item.remove(position);
       notifyDataSetChanged();
    }


}
