package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 01/01/2018.
 */

public class SlotsAdapter extends ArrayAdapter<String> {

    private List<String> slots;

    public SlotsAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.slots = objects;
    }

    @Override
    public void clear() {
        slots = new ArrayList<>();
        //super.clear();
    }

    @Override
    public void addAll(String... items) {
        slots.addAll(Arrays.asList(items));
    }

    /*@Nullable
    @Override
    public String getItem(int position) {
        if(position >= 0 && position < slots.size()) {
            return slots.get(position);
        }
        System.out.println("Returning NULL for position=>" + position + " when size is " + slots.size());
        return "";
    }*/


    @Override
    public int getPosition(@Nullable String item) {
        return slots.indexOf(item);
    }

    public boolean isPresent(String item) {
        return slots.contains(item);
    }

    @Override
    public void remove(@Nullable String object) {
        slots.remove(object);
    }

    public void addItems(List<String> items) {
        slots = items;
        //super.addAll(slots);
    }
}
