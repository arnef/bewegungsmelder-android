package de.arnefeil.bewegungsmelder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;

import de.arnefeil.bewegungsmelder.models.Band;
import de.arnefeil.bewegungsmelder.models.Filter;
import de.arnefeil.bewegungsmelder.models.Location;

/**
 * Created by arne on 10/3/13.
 */
public class FilterAdapter extends ArrayAdapter<Object> {

    private ArrayList<Object> objects;
    private Filter filter;

    public FilterAdapter(Context context, int textViewResourceId,
                         ArrayList<Object> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    public FilterAdapter(Context context, int textViewResourceId,
                         ArrayList<Object> objects, Filter filter) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.filter = filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(android.R.layout.select_dialog_multichoice, null);
        }
        Object object = this.objects.get(position);
        CheckedTextView checkedTextView = (CheckedTextView)
                v.findViewById(android.R.id.text1);

        if (checkedTextView != null) {
            if (object instanceof Filter) {
                Filter filter = (Filter) object;
                checkedTextView.setText(filter.getName());
                checkedTextView.setChecked(!filter.getWhitelist().isEmpty());
            }
            if (object instanceof Band) {
                Band band = (Band) object;
                checkedTextView.setChecked(this.filter.getWhitelist().contains(band));
                checkedTextView.setText(band.getTitle());
            }
            if (object instanceof Location) {
                Location location = (Location) object;
                checkedTextView.setChecked(this.filter.getWhitelist().contains(location));
                checkedTextView.setText(location.getTitle());
            }
            if (object instanceof String) {
                String type = (String) object;
                checkedTextView.setChecked(this.filter.getWhitelist().contains(type));
                checkedTextView.setText(type);
            }
        }

        return v;
    }

}
