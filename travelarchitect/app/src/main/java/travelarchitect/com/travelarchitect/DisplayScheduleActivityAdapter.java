package travelarchitect.com.travelarchitect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Russell on 6/2/2018.
 */

public class DisplayScheduleActivityAdapter extends ArrayAdapter<DisplayScheduleActivityGetSet> {
    private Context mContext;
    private List<DisplayScheduleActivityGetSet> moviesList = new ArrayList<>();

    public DisplayScheduleActivityAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<DisplayScheduleActivityGetSet> list) {
        super(context, 0 , list);
        mContext = context;
        moviesList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.schedule_item,parent,false);

        DisplayScheduleActivityGetSet currentMovie = moviesList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.scheduleItemTitle);
        name.setText(currentMovie.getmName());

        TextView duration = (TextView) listItem.findViewById(R.id.scheduleItemDuration);
        duration.setText(currentMovie.getmRelease());

        TextView type = (TextView) listItem.findViewById(R.id.scheduleItemType);
        type.setText(currentMovie.getmType());

        TextView time = (TextView) listItem.findViewById(R.id.scheduleItemTime);
        time.setText(currentMovie.getmTime());

        //Underline this
        TextView tags = (TextView) listItem.findViewById(R.id.scheduleTitleTags);
        SpannableString content = new SpannableString("Tags");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tags.setText(content);

        return listItem;
    }
}
