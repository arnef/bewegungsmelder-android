package de.arnefeil.bewegungsmelder.adapter;

import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.arnefeil.bewegungsmelder.MainActivity;
import de.arnefeil.bewegungsmelder.R;
import de.arnefeil.bewegungsmelder.models.Band;
import de.arnefeil.bewegungsmelder.models.Event;
import de.arnefeil.bewegungsmelder.models.Link;

/**
 * Created by arne on 10/3/13.
 */
public class EventAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private MainActivity mainActivity;

    public EventAdapter(MainActivity mainActivity, int textViewResourceId,
                        ArrayList<Event> objects) {
        super(mainActivity, textViewResourceId, objects);
        this.mainActivity = mainActivity;
        this.events = objects;
    }

    private View eventsView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.listview_event, parent, false);
        }
        ViewHolder viewHolder = getViewHolder(v);

        final Event event = this.events.get(position);
        if (event != null) {
            if (event.isCancelled()) {
                viewHolder.tvTitle.setText("Cancelled " + event.getTitle());
                viewHolder.eventBackground.setBackgroundColor(Color.parseColor("#593737"));
            } else {
                viewHolder.tvTitle.setText(event.getTitle());
                viewHolder.eventBackground.setBackgroundColor(Color.parseColor("#3c3c3c"));
            }

            viewHolder.tvLocation.setText(event.getLocation() != null
                    ? event.getLocation().getTitle()
                    : null);
            viewHolder.tvPrice.setText(event.getPrice());
            viewHolder.tvTimeStart.setText(event.getTimeStart() != null
                    ? event.getTimeStart().toString()
                    : null);
            viewHolder.tvTimeEntry.setText(event.getTimeEntry() != null
                    ? event.getTimeEntry().toString()
                    : null);
            viewHolder.tvDescription.setText(event.getDescription());
            viewHolder.tvDescriptionExtra.setText(event.getDescriiptionExtras());
            viewHolder.tvLinks.setMovementMethod(LinkMovementMethod.getInstance());
            if (event.getLinks() != null) {
                String links = "";
                for (Link link : event.getLinks()) {
                    links = "<a href=\"" + link.getUrl() + "\">" + link.getTitle() + "</a><br>";
                }
                viewHolder.tvLinks.setText(Html.fromHtml(links));
                viewHolder.tvLinks.setLinkTextColor(Color.parseColor("#C7C649"));
            } else {
                viewHolder.tvLinks.setText(null);
            }
            viewHolder.tvBands.removeAllViews();
            if (event.getBands() != null) {
                for (Band band : event.getBands()) {
                    String bandDescription = band.getTitle();
                    TextView bandTitle = new TextView(v.getContext());
                    bandTitle.setTextColor(Color.parseColor("#ffffee"));
                    bandTitle.setLinkTextColor(Color.parseColor("#C7C649"));
                    if (band.getDescription() != null) {
                        bandDescription += "&emsp;<small>" + band.getDescription() + "</small>";
                    }
                    if (band.getLinks() != null) {
                        String links = "<br>";
                        for (Link link : band.getLinks()) {
                            links += "<a href=\"" + link.getUrl() + "\">" + link.getTitle() + "</a>&emsp;";
                        }
                        bandDescription += links;
                    }
                    bandTitle.setText(Html.fromHtml(bandDescription));
                    bandTitle.setPadding(0, 0, 0, 5);
                    viewHolder.tvBands.addView(bandTitle);
                }
            }
            viewHolder.ivFavorite.setImageDrawable(v.getResources().getDrawable(event.isFavorite()
                    ? R.drawable.rating_favorited
                    : R.drawable.rating_favorite));
            viewHolder.ivFavorite.setVisibility(event.getIsABlank() ? View.GONE : View.VISIBLE);
            if (event.getIsABlank()) {
                viewHolder.eventBackground.setBackgroundResource(R.drawable.nice_back);
            }
            viewHolder.ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    event.setFavorite(!event.isFavorite());
                    mainActivity.updateList();
                }
            });

            viewHolder.lvCategories.removeAllViews();
            if (event.getType() != null) {
                for (String type : event.getType()) {
                    TextView cat = new TextView(v.getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 6, 0);
                    cat.setLayoutParams(params);
                    cat.setPadding(3, 3, 3, 3);
                    cat.setBackgroundColor(Color.parseColor("#ffffee"));
                    cat.setText(type);
                    viewHolder.lvCategories.addView(cat);
                }
            }
        }

        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.eventsView(position, convertView, parent);
    }

    private ViewHolder getViewHolder(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        if (viewHolder != null) {
            return viewHolder;
        }
        viewHolder = new ViewHolder();
        viewHolder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        viewHolder.tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        viewHolder.tvPrice = (TextView) v.findViewById(R.id.tvPrice);
        viewHolder.tvTimeStart = (TextView) v.findViewById(R.id.tvTimeStart);
        viewHolder.tvTimeEntry = (TextView) v.findViewById(R.id.tvTimeEntry);
        viewHolder.tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        viewHolder.tvDescriptionExtra = (TextView) v.findViewById(R.id.tvDescriptionExtra);
        viewHolder.tvLinks = (TextView) v.findViewById(R.id.tvLinks);
        viewHolder.tvBands = (LinearLayout) v.findViewById(R.id.tableBands);
        viewHolder.ivFavorite = (ImageView) v.findViewById(R.id.iv_favorite);
        viewHolder.lvCategories = (LinearLayout) v.findViewById(R.id.ll_categories);
        viewHolder.eventBackground = (RelativeLayout) v.findViewById(R.id.eventback);
        v.setTag(viewHolder);
        return viewHolder;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvLocation;
        TextView tvPrice;
        TextView tvTimeStart;
        TextView tvTimeEntry;
        TextView tvDescription;
        TextView tvDescriptionExtra;
        TextView tvLinks;
        LinearLayout tvBands;
        ImageView ivFavorite;
        LinearLayout lvCategories;
        RelativeLayout eventBackground;
    }
}
