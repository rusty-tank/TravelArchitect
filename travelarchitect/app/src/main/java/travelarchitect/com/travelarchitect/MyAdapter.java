package travelarchitect.com.travelarchitect;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Kok Siang Tee on 3/29/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<String> destinations;
    private ArrayList<String> destinationsTag;
    private ArrayList<String> destinationsCoor;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView destinationName;
        public ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);

            destinationName = (TextView) itemView.findViewById(R.id.destination_name);
            deleteButton = (ImageView) itemView.findViewById(R.id.delete_button);

            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.delete_button:
                    removeAt(getAdapterPosition());
                    break;
            }
        }
    }

    public void removeAt(int position) {
        destinations.remove(position);
        destinationsTag.remove(position);
        destinationsCoor.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, destinations.size());
    }

    public MyAdapter(ArrayList<String> destinations, ArrayList<String> destinationsTag, ArrayList<String> destinationsCoor) {
        this.destinations = destinations;
        this.destinationsTag = destinationsTag;
        this.destinationsCoor = destinationsCoor;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.destinations_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        holder.destinationName.setText(destinations.get(position));
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }
}
