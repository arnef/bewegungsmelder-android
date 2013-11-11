package de.arnefeil.bewegungsmelder.tools;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import de.arnefeil.bewegungsmelder.MainActivity;
import de.arnefeil.bewegungsmelder.R;
import de.arnefeil.bewegungsmelder.adapter.FilterAdapter;
import de.arnefeil.bewegungsmelder.models.Band;
import de.arnefeil.bewegungsmelder.models.Event;
import de.arnefeil.bewegungsmelder.models.Filter;
import de.arnefeil.bewegungsmelder.models.Location;

/**
 * Created by arne on 10/3/13.
 */
public class FilterLoader {

    private MainActivity mainActivity;
    private ArrayList<Object> filter;
    private ArrayList<Object> availableBands;
    private ArrayList<Object> availableLocations;
    private ArrayList<Object> availableTypes;
    private ArrayList<Event> allEvents;
    private HashSet<Event> filteredEvents;

    public FilterLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.filter = new ArrayList<Object>();
        this.filter.add(new Filter("Band"));
        this.filter.add(new Filter("Location"));
        this.filter.add(new Filter("Type"));
        this.filteredEvents = new HashSet<Event>();
        this.allEvents = this.mainActivity.getEventLoader().getEventList();
        this.setAvailableBands();
        this.setAvailableLocations();
        this.setAvailableTypes();
        Log.v("FilterLoader", "Bands: " + this.availableBands.size()
                + "\nLocations: " + this.availableLocations.size()
                + "\nTypes: " + this.availableTypes.size());
    }

    public void execute() {
        final Dialog dialog = new Dialog(this.mainActivity);
        dialog.setContentView(R.layout.dialog_filter_select);
        dialog.setTitle(this.mainActivity.getString(R.string.action_filter));
        dialog.setCancelable(true);

        ListView lvFilterName = (ListView) dialog.findViewById(R.id.listView);
        FilterAdapter adapter = new FilterAdapter(this.mainActivity,
                android.R.layout.select_dialog_multichoice, this.filter);
        lvFilterName.setAdapter(adapter);
        dialog.getWindow().setLayout(this.mainActivity.getDialogSize()[0],
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lvFilterName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Filter filter = (Filter) parent.getItemAtPosition(position);
                dialog.dismiss();
                if (filter.getName().equals("Band"))
                    FilterLoader.this.createDialog(
                            FilterLoader.this.availableBands,
                            filter
                    ).show();
                if (filter.getName().equals("Location"))
                    FilterLoader.this.createDialog(
                            FilterLoader.this.availableLocations,
                            filter).show();
                if (filter.getName().equals("Type"))
                    FilterLoader.this.createDialog(
                            FilterLoader.this.availableTypes,
                            filter
                    ).show();

            }
        });
        Button btnClear = (Button) dialog.findViewById(R.id.button_save);
        btnClear.setText("Filter löschen");
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Object o: FilterLoader.this.filter) {
                    Filter f = (Filter) o;
                    f.clearWhitelist();
                }
                FilterLoader.this.mainActivity.getEventLoader().setFiltered(false);
                FilterLoader.this.mainActivity.updateView();
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button) dialog.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setAvailableBands() {
        Set<Band> bands = new TreeSet<Band>();
        if (this.allEvents != null) {
        for (Event e: this.allEvents) {
            if (e.getBands() != null) {
                for (Band b: e.getBands()) {
                    bands.add(b);
                }
            }
        }}
        this.availableBands = new ArrayList<Object>(bands);
    }

    private void setAvailableLocations() {
        Set<Location> locations = new TreeSet<Location>();
        if (this.allEvents != null) {
        for (Event e: this.allEvents) {
            if (e.getLocation() != null) {
                locations.add(e.getLocation());
            }
        }}
        this.availableLocations = new ArrayList<Object>(locations);
    }

    private void setAvailableTypes() {
        Set<String> types = new TreeSet<String>();
        if (this.allEvents != null) {
        for (Event e: this.allEvents) {
            if (e.getType() != null) {
                for (String t: e.getType()) {
                    types.add(t);
                }
            }
        }}
        this.availableTypes = new ArrayList<Object>(types);
    }

    private Dialog createDialog(ArrayList<Object> list, final Filter filter) {
        final Dialog dialog = new Dialog(this.mainActivity);
        dialog.setContentView(R.layout.dialog_filter_select);
        dialog.setTitle(filter.getName() + "s");
        dialog.setCancelable(true);
        if (list.size() < 5) {
            dialog.getWindow().setLayout(this.mainActivity.getDialogSize()[0],
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            dialog.getWindow().setLayout(this.mainActivity.getDialogSize()[0],
                    this.mainActivity.getDialogSize()[1]);
        }
        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        FilterAdapter adapter = new FilterAdapter(this.mainActivity,
                android.R.layout.select_dialog_multichoice, list, filter);
        listView.setAdapter(adapter);

        final ArrayList<Object> whitelist = new ArrayList<Object>(filter.getWhitelist());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(
                        android.R.id.text1
                );
                Object object = parent.getItemAtPosition(position);
                if (checkedTextView.isChecked()) {
                    checkedTextView.setChecked(false);
                    whitelist.remove(object);
                } else {
                    checkedTextView.setChecked(true);
                    whitelist.add(object);
                }

            }
        });
        Button btnCancel = (Button) dialog.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btnSave = (Button) dialog.findViewById(R.id.button_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter.setWhitelist(whitelist);
                if (filter.getWhitelist().size() == 0) {
                    mainActivity.getEventLoader().setFiltered(false);
                    mainActivity.updateView();
                } else {
                    FilterLoader.this.filter();
                }
                dialog.dismiss();

            }
        });

        return dialog;
    }

    private void filter() {
        this.filteredEvents.clear();
        for (Object o: this.filter) {
            Filter filter = (Filter) o;
            if (filter.getWhitelist().size() > 0) {
                if (filter.getName().equals("Band"))
                    this.filterBands(filter);
                if (filter.getName().equals("Location"))
                    this.filterLocations(filter);
                if (filter.getName().equals("Type"))
                    this.filterTypes(filter);
            }
        }
        this.mainActivity.getEventLoader().setEventListFiltered(new ArrayList<Event>(this.filteredEvents));
        this.mainActivity.getEventLoader().setFiltered(true);
        this.mainActivity.updateView();
    }

    private void filterBands(Filter filter) {
        ArrayList<Event> events = this.mainActivity.getEventLoader().getEventList();
        for (Event e: events) {
            if (e.getBands() != null) {
                for(Object o: filter.getWhitelist()) {
                    if (e.getBands().contains(o))
                        this.filteredEvents.add(e);
                }
            }
        }
    }

    private void filterLocations(Filter filter) {
        ArrayList<Event> events = this.mainActivity.getEventLoader().getEventList();
        for (Event e: events) {
            if (e.getLocation() != null) {
                for (Object o: filter.getWhitelist()) {
                    Location l = (Location) o;
                    if (e.getLocation().equals(l))
                        this.filteredEvents.add(e);
                }
            }
        }
    }

    private void filterTypes(Filter filter) {
        ArrayList<Event> events = this.mainActivity.getEventLoader().getEventList();
        for (Event e: events) {
            if (e.getType() != null) {
                for (Object o: filter.getWhitelist()) {
                    if (e.getType().contains(o))
                        this.filteredEvents.add(e);
                }
            }
        }
    }
}

