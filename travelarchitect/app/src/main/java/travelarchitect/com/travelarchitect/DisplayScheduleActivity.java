package travelarchitect.com.travelarchitect;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import android.app.ListActivity;
import android.widget.Toast;

/**
 * Created by Russell on 29/1/2018.
 */

public class DisplayScheduleActivity extends ListActivity {

    private ListView listView;
    private DisplayScheduleActivityAdapter mAdapter;
    public int currentTime = 1030;
    public int perDay = 7; //hrs spent per day touring

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        TextView tv = (TextView) findViewById(R.id.scheduleText);

        //Bring destinations and coordinates from previous activity
        final ArrayList<String> destinations = (ArrayList<String>)getIntent().getSerializableExtra("dest");
        final ArrayList<String> destinationsTimeSpent = (ArrayList<String>)getIntent().getSerializableExtra("destTime");
        final ArrayList<String> destinationsTags = (ArrayList<String>)getIntent().getSerializableExtra("destTags");

        Toast.makeText(DisplayScheduleActivity.this, String.valueOf(destinationsTags), Toast.LENGTH_LONG).show();

        //Arrange timings
        ArrayList<String> realTime = new ArrayList<>();
        realTime.add(String.valueOf(currentTime));
        for(int i = 0; i < destinationsTimeSpent.size() - 1; i++){
            String time = String.valueOf(destinationsTimeSpent.get(i));
            int hrs = Integer.valueOf(time.split("\\.")[0]);
            int min = Integer.valueOf(time.split("\\.")[1]);
            currentTime += hrs*100 + min*1;
            realTime.add(String.valueOf(currentTime));
        }

        //Stopped here!

        //Things to erase for final product
        int dupes = destinations.size();
        tv.setText(Integer.toString(dupes));

        //Declare an arraylist to use in output
        ArrayList<String> itemname = destinations;
        ArrayList<String> itemtime = destinationsTimeSpent;
        ArrayList<String> itemtag = destinationsTags;

        //Make sure destinations have less than 31 characters, if not put triple dot and remove comma at end of tags
        for (int i = 0; i < itemname.size(); i++){
            if (itemname.get(i).length() > 35)  itemname.set(i, itemname.get(i).substring(0,35) + "...");
            itemtag.set(i, itemtag.get(i).substring(0,itemtag.get(i).length() - 2));
        }

        //Enter name, time spent and type for each
        listView = (ListView) findViewById(android.R.id.list);
        ArrayList<DisplayScheduleActivityGetSet> moviesList = new ArrayList<>();
        for (int i = 0; i < itemname.size(); i++){
            moviesList.add(new DisplayScheduleActivityGetSet(R.drawable.common_full_open_on_phone, String.valueOf(itemname.get(i)), String.valueOf(itemtime.get(i)),
                    String.valueOf(itemtag.get(i)), String.valueOf(realTime.get(i))));
        }
        mAdapter = new DisplayScheduleActivityAdapter(this, moviesList);
        listView.setAdapter(mAdapter);

//        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.schedule_item,R.id.scheduleItemTitle,itemname));
        //this.setListAdapter(new ArrayAdapter<String>(this, R.layout.schedule_item,R.id.scheduleItemDuration,itemtime));
        //End tutorial
    }

    //Creating functions
//    public static ArrayList<String> arranger(int timeStart, int timeGivenPerDay, ArrayList timeSpent){
//        ArrayList<String> realTime = new ArrayList<>();
//        realTime.add(String.valueOf(timeStart));
//        for (int i = 0; i < timeSpent.size() - 1; i++){
//            int qwer = timeAdder(timeStart, (Integer)timeSpent.get(i));
//            realTime.add(String.valueOf(qwer));
//        }
//        return realTime;
//    }
//
//    public static int timeAdder(int timing, int add){
//        int mins = add%1;
//        int hrs = (add - mins)*100;
//        timing += hrs + mins;
//        return timing;
//    }
    //End functions
}