package adapter;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Usercontact;

/**
 * Created by Rohit on 1/1/2018.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHoldercontact> {
    private Context mContext;
   private List<Usercontact> item,filterList;



    public class MyViewHoldercontact extends RecyclerView.ViewHolder {
        private TextView phone,name;
        public MyViewHoldercontact(View itemView) {
            super(itemView);
            phone = (TextView) itemView.findViewById(R.id.txt_user_phone);
            name = (TextView) itemView.findViewById(R.id.txt_user_contactname);

            this.name.setOnClickListener(new View.OnClickListener() {
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
            });

        }
    }
    public ContactListAdapter(Context mContext, List<Usercontact> item){
        this.mContext = mContext;
        this.item = item;
       // this.filterList = new List<Usercontact>();
        // we copy the original list to the filter list and use it for setting row values

        System.out.println("item list:" + item);

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_row, parent, false);
        return new MyViewHoldercontact(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHoldercontact holder, int position) {

        Usercontact usercontact = item.get(position);
        holder.name.setText(usercontact.getName());
        holder.phone.setText(usercontact.getPhone());
        System.out.println("name in Adapter " + usercontact.getName());
    }








    @Override
    public int getItemCount() {
        return item.size();
    }





}
