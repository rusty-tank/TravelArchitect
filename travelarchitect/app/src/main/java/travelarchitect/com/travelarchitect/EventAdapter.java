package travelarchitect.com.travelarchitect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kok Siang Tee on 4/20/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private ArrayList<Event> events;
    private Context context;

    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public TextView eventDate;
        public TextView eventCategory;
        public TextView eventDescription;
        public ImageView eventImage;

        public ViewHolder(View v) {
            super(v);

            eventName = (TextView) v.findViewById(R.id.name);
            eventDate = (TextView) v.findViewById(R.id.date);
            eventCategory = (TextView) v.findViewById(R.id.category);
            eventDescription = (TextView) v.findViewById(R.id.description);
            eventImage = (ImageView) v.findViewById(R.id.image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.eventName.setText(events.get(position).getName());
        holder.eventDate.setText(events.get(position).getDate());
        holder.eventCategory.setText(events.get(position).getCategory());
        holder.eventDescription.setText(events.get(position).getDescription());
        Picasso.with(context).load(events.get(position).getImageName()).into(holder.eventImage);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
