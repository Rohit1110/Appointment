package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.util.List;

import model.InviteContact;

/**
 * Created by Rohit on 1/1/2018.
 */

public class InviteContactListAdapter extends RecyclerView.Adapter<InviteContactListAdapter.ViewHolderinvteContact> {
    private Context mContext;
   private List<InviteContact> item,filterList;



    public class ViewHolderinvteContact extends RecyclerView.ViewHolder{
        private TextView phone,name;
        CheckBox contactCheck;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();
        public ViewHolderinvteContact(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            phone = (TextView) itemView.findViewById(R.id.txt_phone);
            name = (TextView) itemView.findViewById(R.id.txt_contactname);
            contactCheck=(CheckBox)itemView.findViewById(R.id.chkcontact);

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

       /* @Override
        public void onClick(View v) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                v.setSelected(false);

            }
            else {
                selectedItems.put(getAdapterPosition(), true);
                v.setSelected(true);
            }
           // Toast.makeText(mContext, "item clicked"+selectedItems.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();

        }*/
    }
    public InviteContactListAdapter(Context mContext, List<InviteContact> item){
        this.mContext = mContext;
        this.item = item;
       // this.filterList = new List<UserContact>();
        // we copy the original list to the filter list and use it for setting row values

        System.out.println("item list:" + item);

    }

    /*// Define listener member variable
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(CharSequence text);
    }

    // Define the method that allows the parent activity or fragment to define the listener.
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }*/


    @Override
    public ViewHolderinvteContact onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_invitecontact_row, parent, false);
        return new ViewHolderinvteContact(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderinvteContact holder, int position) {
         System.out.println("onBindViewHolder");
        InviteContact inviteContact = item.get(position);
        holder.name.setText(inviteContact.getName());
        holder.phone.setText(inviteContact.getPhone());
        holder.contactCheck.setOnCheckedChangeListener(null);
        holder.contactCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if( holder.contactCheck.isChecked()){
                   
               }
            }
        } );
        System.out.println("name in Adapter " + inviteContact.getName());
    }








    @Override
    public int getItemCount() {
        return item.size();
    }





}
