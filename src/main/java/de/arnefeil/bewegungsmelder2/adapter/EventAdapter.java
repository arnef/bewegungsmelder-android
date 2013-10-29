package de.arnefeil.bewegungsmelder2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import de.arnefeil.bewegungsmelder2.MainActivity;
import de.arnefeil.bewegungsmelder2.R;
import de.arnefeil.bewegungsmelder2.models.Band;
import de.arnefeil.bewegungsmelder2.models.Event;
import de.arnefeil.bewegungsmelder2.models.Link;

/**
 * Created by arne on 10/3/13.
 */
public class EventAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private TextView tvTitle;
    private TextView tvLocation;
    private TextView tvPrice;
    private TextView tvTimeStart;
    private TextView tvTimeEntry;
    private TextView tvDescription;
    private TextView tvDescriptionExtra;
    private TextView tvLinks;
    private TableLayout tvBands;
    private ImageView ivFavorite;
    private LinearLayout lvCategories;
    private MainActivity mainActivity;

    public EventAdapter(MainActivity mainActivity, int textViewResourceId,
                        ArrayList<Event> objects) {
        super(mainActivity, textViewResourceId, objects);
        this.mainActivity = mainActivity;
        this.events = objects;
    }

    private View eventsView(int position, View convertView, ViewGroup parant) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_event, null);
        }

        final Event event = this.events.get(position);
        this.setRegsiter(v);
        if (event != null) {
            if (this.tvTitle != null) {
                RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.eventback);
                if  (event.isCancelled()) {
                    this.tvTitle.setText("Cancelled " + event.getTitle());
                    rl.setBackgroundColor(Color.parseColor("#593737"));
                } else {
                    this.tvTitle.setText(event.getTitle());
                    rl.setBackgroundColor(Color.parseColor("#373737"));
                }
            }
            if (this.tvLocation != null) {
                if (event.getLocation() != null)
                    this.tvLocation.setText(event.getLocation().getTitle());
                else this.tvLocation.setHeight(1);
            }
            if (this.tvPrice != null) {
                if (event.getPrice() != null)
                    this.tvPrice.setText(event.getPrice());
                else this.tvPrice.setText("");
            }
            if (this.tvTimeStart != null) {
                if (event.getTimeStart() != null)
                    this.tvTimeStart.setText(event.getTimeStart().toString());
                else this.tvTimeStart.setText("");
            }
            if (this.tvTimeEntry != null) {
                if (event.getTimeEntry() != null)
                    this.tvTimeEntry.setText(event.getTimeEntry().toString());
                else this.tvTimeEntry.setText("");
            }
            if (this.tvDescription != null) {
                if (event.getDescription() != null)
                    this.tvDescription.setText(event.getDescription());
                else this.tvDescription.setHeight(1);
            }
            if (this.tvDescriptionExtra != null) {
                if (event.getDescriiptionExtras() != null)
                    this.tvDescriptionExtra.setText(event.getDescriiptionExtras());
                else this.tvDescriptionExtra.setHeight(1);
            }
            if (this.tvLinks != null) {
                this.tvLinks.setMovementMethod(LinkMovementMethod.getInstance());
                if (event.getLinks() != null) {
                    String links = "";
                    for (Link link: event.getLinks()) {
                        links = "<a href=\"" + link.getUrl() + "\">"  + link.getTitle() + "</a><br>";
                    }
                    this.tvLinks.setText(Html.fromHtml(links));
                } else this.tvLinks.setHeight(1);
            }
            if (this.tvBands != null) {
                this.tvBands.removeAllViews();
                if (event.getBands() != null) {
                    TextView tvHead = new TextView(this.mainActivity);
                    tvHead.setTextAppearance(this.mainActivity, android.R.style.TextAppearance_Medium);
                    tvHead.setTextColor(Color.BLACK);
                    tvHead.setText("Artists");
                    TableRow trHead = new TableRow(this.mainActivity);
                    trHead.addView(tvHead);
                    this.tvBands.addView(trHead);
                    for (Band band: event.getBands()) {
                        String bandDescription = band.getTitle();
                        TableRow bandrow = new TableRow(this.mainActivity);
                        TextView bandTitle = new TextView(this.mainActivity);
                        bandTitle.setTextColor(Color.BLACK);
                        TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                        bandTitle.setLayoutParams(params);
                        TextView bandLinks = new TextView(this.mainActivity);
                        bandLinks.setGravity(Gravity.RIGHT);
                        if (band.getDescription() != null)
                            bandDescription += "\n" + band.getDescription();
                        if (band.getLinks() != null) {
                            String links = "";
                            for (Link link: band.getLinks()) {
                                links += "<a href=\"" + link.getUrl() + "\">" + link.getTitle() + "</a><br>";
                            }
                            bandLinks.setMovementMethod(LinkMovementMethod.getInstance());
                            bandLinks.setText(Html.fromHtml(links));
                        }
                        bandrow.addView(bandTitle);
                        bandrow.addView(bandLinks);
                        bandTitle.setText(bandDescription);
                        this.tvBands.addView(bandrow);
                    }
                }
            }
            if (this.ivFavorite != null) {
                if (event.isFavorite()) {
                    this.ivFavorite.setImageDrawable(
                            this.mainActivity.getResources().getDrawable(R.drawable.rating_favorited)
                    );
                } else {
                    this.ivFavorite.setImageDrawable(
                            this.mainActivity.getResources().getDrawable(R.drawable.rating_favorite)
                    );
                }

                this.ivFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        event.setFavorite(!event.isFavorite());
                        mainActivity.updateList();

                    }
                });
            }
            if (this.lvCategories != null) {
                lvCategories.removeAllViews();
                if (event.getType() != null) {
                    for (String type: event.getType()) {
                        TextView cat = new TextView(this.mainActivity);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,0,6,0);
                        cat.setLayoutParams(params);
                        cat.setPadding(3,3,3,3);
                        cat.setBackgroundColor(Color.parseColor("#ffffee"));

                        cat.setText(type);

                        lvCategories.addView(cat);

                    }
                    //types = types.substring(0, types.length() - 2);
                    //this.tvCategories.setText(Html.fromHtml(types));
                }
            }

        }

        return v;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            return this.eventsView(position, convertView, parent);
    }

    private void setRegsiter(View v) {
        this.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        this.tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        this.tvPrice = (TextView) v.findViewById(R.id.tvPrice);
        this.tvTimeStart = (TextView) v.findViewById(R.id.tvTimeStart);
        this.tvTimeEntry = (TextView) v.findViewById(R.id.tvTimeEntry);
        this.tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        this.tvDescriptionExtra = (TextView) v.findViewById(R.id.tvDescriptionExtra);
        this.tvLinks = (TextView) v.findViewById(R.id.tvLinks);
        this.tvBands = (TableLayout) v.findViewById(R.id.tableBands);
        this.ivFavorite = (ImageView) v.findViewById(R.id.iv_favorite);
        this.lvCategories = (LinearLayout) v.findViewById(R.id.ll_categories);
    }
}
