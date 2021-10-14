package travelarchitect.com.travelarchitect;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.sql.Time;
import java.text.DecimalFormat;
import android.widget.Toast;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TimelineActivity extends AppCompatActivity {

    public static String combination = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView tv = (TextView) findViewById(R.id.qweroutput);
        final Intent intent = new Intent(TimelineActivity.this,DisplayScheduleActivity.class);



        setSupportActionBar(toolbar);

        //temp is used to output info temporarily without a GUI.
        String temp = "";

        //Bring destinations and coordinates from previous activity
        final ArrayList<String> destinations = (ArrayList<String>)getIntent().getSerializableExtra("dest");
        ArrayList<String> destinationsCoor = (ArrayList<String>)getIntent().getSerializableExtra("destCoor");
        ArrayList<String> destinationsTags = (ArrayList<String>)getIntent().getSerializableExtra("destTags");

        //Bring to schedule page
        Button btnCont = (Button) findViewById(R.id.btnTimelineContinue);
        btnCont.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //onClick is working

                intent.putExtra("dest", destinations);
                startActivity(intent);

//                Intent intent = new Intent(PlaceActivity.this, TimelineActivity.class);
//                intent.putExtra("dest", destinations);
//                intent.putExtra("destCoor", destinationsCoor);
//                intent.putExtra("destTags", destinationsTags);
//                startActivity(intent);
            }
        });

        //Convert imported ArrayList to arrays
        int places = destinations.size();
        double[][] coor = new double[places][2];
        String[][] tags = new String[places][2];
        double[][] distance;
        for (int i = 0; i < coor.length; i++){
            String fullCoor = destinationsCoor.get(i);
            String[] split = fullCoor.split(",");
            coor[i][0] = Double.parseDouble(split[0]);
            coor[i][1] = Double.parseDouble(split[1]);
        }
        for (int i = 0; i < coor.length; i++){
            tags[i][0] = destinationsTags.get(i);
        }

        //Set-up tags dictionary (allocate 30 slots, only 17 are used currently)
        String[][] tagDictionary = new String[30][2];
        //Big category sorting
        tagDictionary[0][0] = "Nature & Parks";
        tagDictionary[0][1] = "1.5";
        tagDictionary[1][0] = "Sights & Landmarks";
        tagDictionary[1][1] = "1.0";
        tagDictionary[2][0] = "Outdoor Activities";
        tagDictionary[2][1] = "2.0";
        tagDictionary[3][0] = "Zoos & Aquariums";
        tagDictionary[3][1] = "3.75";
        tagDictionary[4][0] = "Shopping";
        tagDictionary[4][1] = "1.0";
        tagDictionary[5][0] = "Museums";
        tagDictionary[5][1] = "1.5";
        tagDictionary[6][0] = "Transportation";
        tagDictionary[6][1] = "0.5";
        tagDictionary[7][0] = "Tours";
        tagDictionary[7][1] = "1.0";
        tagDictionary[8][0] = "Nightlife";
        tagDictionary[8][1] = "2.0";
        tagDictionary[9][0] = "Fun & Games";
        tagDictionary[9][1] = "2.0";
        tagDictionary[10][0] = "Food & Drink";
        tagDictionary[10][1] = "0.5";
        tagDictionary[11][0] = "Boat Tours & Water Sports";
        tagDictionary[11][1] = "1.5";
        tagDictionary[12][0] = "Spas & Wellness";
        tagDictionary[12][1] = "1.5";
        tagDictionary[13][0] = "Casinos & Gambling";
        tagDictionary[13][1] = "2.0";
        tagDictionary[14][0] = "Traveller Resources";
        tagDictionary[14][1] = "1.0";
        tagDictionary[15][0] = "Events";
        tagDictionary[15][1] = "2.0";
        tagDictionary[16][0] = "Water & Amusement Parks";
        tagDictionary[16][1] = "2.0";

        //Find tags and their corresponding times
        double tagAssociated = 0;
        double totalTagHrs = 0;
        for (int i = 0; i < coor.length; i++){
            //^loops every destination
//            tags[i][1] = "";
            for (int j = 0; j < 17; j++){
                //^loops entire tag dictionary
                if (tags[i][0].contains(tagDictionary[j][0])){
                    //tags[i][1] += tagDictionary[j][1] + " hrs, ";
                    totalTagHrs += Double.parseDouble(tagDictionary[j][1]);
                    tagAssociated++;
                }
            }
            if (totalTagHrs != 0)
                tags[i][1] = Double.toString(totalTagHrs/tagAssociated);
            else
                tags[i][1] = "NA";
            tagAssociated = 0;
            totalTagHrs = 0;
        }



        //Formatting
        DecimalFormat zeroDP=new DecimalFormat("#");
        DecimalFormat threeDP=new DecimalFormat("#.000");


        //Find distance between every attraction
        int noOfCombinations = (places-1)*(places) / 2;
        distance = new double [noOfCombinations][3];
        int from = 0, to = 1, limit = places - 1, countup = 0;

        while (limit > 0){
            for (int i = 0; i < limit; i++){
                double length;
                Location loc1 = new Location("");
                loc1.setLatitude(coor[from][0]);
                loc1.setLongitude(coor[from][1]);

                Location loc2 = new Location("");
                loc2.setLatitude(coor[to][0]);
                loc2.setLongitude(coor[to][1]);

                length = loc1.distanceTo(loc2)/1000;

                distance[countup][0] = length;
                distance[countup][1] = from + 1;
                distance[countup][2] = to + 1;
                to++;
                countup++;
            }
            limit--;
            from++;
            to = from + 1;
        }

        sort(distance);



//        //This is to give permission to jsoup to access website data
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        //Find location type (hotel, amusement park, shopping mall, etc.)
//        String[][] placeType = new String[destinations.size()][3];
//        for (int i = 0; i < placeType.length; i++){
//            String placeOfFocus = destinations.get(i);
//            try {
//                String url = "https://www.google.com/search?q=" + placeOfFocus;
//                Document doc = Jsoup.connect(url).userAgent("mozilla/17.0").get();
//                Elements ele = doc.select("div._POh");
//                String attractionType = "";
//                for (Element extractSpan:ele){
//                    attractionType += extractSpan.getElementsByTag("span").first().text() + "\n";
//                }
//                if (attractionType != "") {
//                    placeType[i][0] = attractionType;
//                }
//                else {
//                    placeType[i][0] = "undefined";
//                }
//
//            }
//
//            catch (IOException e){
//                e.printStackTrace();
//            }
//
//        }


        //Output part 1 (places, etc.)
        temp += "\nPlaces: \n";

        ArrayList timePerAtt = new ArrayList();
        for (int i = 0; i < destinations.size(); i++){
            if (tags[i][1] != "NA"){
                temp += (i+1) + ". " + destinations.get(i) + "...\n Predicted Time Taken here: " + tags[i][1] + " hrs\n\n";
                timePerAtt.add(tags[i][1]);
            }

            else{
                temp += (i+1) + ". " + destinations.get(i) + "...\n Predicted Time Taken here: " + tags[i][1] + " (Auto-assumed this attraction takes an hour)\n\n";
                timePerAtt.add(1.0);
            }


        }

        //pasted
        intent.putExtra("destTime", timePerAtt);
        intent.putExtra("destTags", destinationsTags);
        //pasted end


        temp += factorial(destinations.size()) + " routing combinations.\n" + distance.length + " possible routes.\n" + places + " destinations.\n\n";
        for (int i = 0; i < distance.length; i++)
        {
            temp += threeDP.format(distance[i][0]) + "km, pt " + distance[i][1] + " to pt " + distance[i][2] + "\n";
        }


        //Shortest path starts here
        //Plan route
        double[][] plan = new double[places - 1][3];
        Boolean[] covered = new Boolean[places];
        double totalDistancePlanned = 0;

        //Create comparator to hold shortest value
        int shortestTry = Integer.MAX_VALUE;
        double shortestDistance = Double.MAX_VALUE;
        double[][] shortestPlan = new double[places-1][3];
        String path = "";

        //loop start
        for(int tries = 0; tries < distance.length; tries++) {
            Arrays.fill(covered, Boolean.TRUE);
            totalDistancePlanned = 0;

            int current = (int)distance[tries][1];
            int destination = (int)distance[tries][2];
            for (int i = 0; i < distance.length; i++){
                if(i == tries) i++;
                if (distance[i][1] == distance[tries][1] || distance[i][2] == distance[tries][1])
                {
                    current = (int)distance[tries][2];
                    destination = (int)distance[tries][1];
                    break;
                }
                else if(distance[i][1] == distance[tries][2] || distance[i][2] == distance[tries][2])
                {
                    current = (int)distance[tries][1];
                    destination = (int)distance[tries][2];
                    break;
                }

            }

            plan[0][0] = distance[tries][0];
            plan[0][1] = current;
            plan[0][2] = destination;
            totalDistancePlanned += plan[0][0];
            covered[(int)current-1] = false;
            covered[(int)destination-1] = false;
            current = destination;


            int currentRowPlanned = 1;
            for (int count = 0;Arrays.asList(covered).contains(true); count++)
            {
                for (int i = 0; i < distance.length; i++)
                {
                    if (current==distance[i][1] && covered[(int)distance[i][2]-1]){
                        plan[currentRowPlanned][0] = distance[i][0];
                        plan[currentRowPlanned][1] = current;
                        plan[currentRowPlanned][2] = distance[i][2];
                        totalDistancePlanned += distance[i][0];
                        covered[(int)distance[i][2]-1] = false;
                        current = (int)plan[currentRowPlanned][2];
                        currentRowPlanned++;
                        break;
                    }
                    else if (current==distance[i][2] && covered[(int)distance[i][1]-1]){
                        plan[currentRowPlanned][0] = distance[i][0];
                        plan[currentRowPlanned][1] = current;
                        plan[currentRowPlanned][2] = distance[i][1];
                        totalDistancePlanned += distance[i][0];
                        covered[(int)distance[i][1]-1] = false;
                        current = (int)plan[currentRowPlanned][2];
                        currentRowPlanned++;
                        break;
                    }
                }
                //In case of infinite loop, below will run
                if (count >= 100){
                    temp += "\n\nERROR: LOOP RAN FOR " + count +" TIMES. BREAKING INFINITE LOOP NOW.";
                    Toast.makeText(TimelineActivity.this, "Error has been encountered. Error code: infinite loop", Toast.LENGTH_LONG).show();
                    break;
                }
            }


            if (shortestDistance > totalDistancePlanned)
            {
                shortestTry = tries;
                shortestDistance = totalDistancePlanned;
                for (int i = 0; i < shortestPlan.length; i++){
                    shortestPlan[i][0] = plan[i][0];
                    shortestPlan[i][1] = plan[i][1];
                    shortestPlan[i][2] = plan[i][2];
                    path += (int)shortestPlan[i][1];
                }
                path += (int)shortestPlan[shortestPlan.length-1][2];
            }

        }
        //loop end
        temp += "Shortest route is " + path + " with a distance of " + shortestDistance + "km.\n";
        temp += "\nArray keeping shortest plan:\n" + Arrays.deepToString(shortestPlan);
        //Shortest path ends here




        //Clustering start
        //Mean
        double mean = shortestDistance / shortestPlan.length;
        //Standard Deviation
        double variance = 0;
        for (int i = 0; i < shortestPlan.length; i++) {
            {
                variance += ((shortestPlan[i][0] - mean) * (shortestPlan[i][0] - mean)) / (shortestPlan.length);
            }
        }
        double SD = Math.sqrt(variance);
        temp += "\n\nTD = " + threeDP.format(shortestDistance) + "km\nMean = " + threeDP.format(mean)+ "km\nSD = " + threeDP.format(SD) + "km";

        //Find no. of clusters
        int noOfClusters = 1;
        for (int i = 0; i < shortestPlan.length; i++) {
            if (shortestPlan[i][0] >= mean) {
                noOfClusters++;
            }
        }
        temp+="\n\nClusters generated: " + noOfClusters +"\n\n";

        //Find clusters, their distance, attractions in cluster
        int clusterFocus = 0;
        double clusterDistance = 0;
        double[][] cluster = new double[noOfClusters][5];
        String[] clusterOrder = new String[noOfClusters];

        cluster[0][1] = shortestPlan[0][1];                                     //Manually input first attraction
        clusterOrder[0] = String.valueOf(zeroDP.format(shortestPlan[0][1]));    //Manually input first attraction
        for (int i = 0; i < shortestPlan.length; i++) {
            if (shortestPlan[i][0] >= mean) {
                cluster[clusterFocus][0] = clusterDistance;
                cluster[clusterFocus][2] = shortestPlan[i][1];
                cluster[clusterFocus][3] = shortestPlan[i][0];

                clusterFocus++;
                clusterDistance = 0;
                cluster[clusterFocus][0] = clusterDistance;
                cluster[clusterFocus][1] = shortestPlan[i][2];
                clusterOrder[clusterFocus] = String.valueOf(zeroDP.format(shortestPlan[i][2]));
            } else {
                clusterOrder[clusterFocus] += "," + String.valueOf(zeroDP.format(shortestPlan[i][2]));
                clusterDistance += shortestPlan[i][0];
            }

        }
        cluster[clusterFocus][0] = clusterDistance;
        cluster[clusterFocus][2] = shortestPlan[shortestPlan.length-1][2];

        //Find cluster time taken
        for (int i = 0; i < cluster.length; i++) {
            String[] clusterPlaces = clusterOrder[i].split(",");
            double clusterTime = 0;
            for (int j = 0; j < clusterPlaces.length; j++){
                int currentPoint = Integer.parseInt(clusterPlaces[j]) - 1;
                if (tags[currentPoint][1] == "NA")
                    clusterTime += 1;
                else
                    clusterTime += Double.parseDouble(tags[currentPoint][1]);
            }
            cluster[i][4] = clusterTime;
        }

        //Output part 2 (clusters, etc.)
        for (int i = 0; i < cluster.length; i++){
            temp += "From " + (int)cluster[i][1] + " to " + (int)cluster[i][2] + ", with an inter-cluster distance of  " + threeDP.format(cluster[i][0]) + "km. Distance to next cluster is " + threeDP.format(cluster[i][3]) + "km.";
            temp += "\nPoints in cluster (in order): " + clusterOrder[i];
            temp += "\nTotal time required in cluster: " + cluster[i][4] + " hours.";
            temp += "\n\n";
        }

        temp += "Disclaimer:\nThis plan does not include travelling time between places (need to find transportation method first)\n";
        temp += "and eating time yet.";
        //code end
        tv.setText(temp);


        //Below unimportant
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }






    //Creating functions
    public static void sort(double m[][]){
        for (int i = 0; i < m.length - 1; i++)
            for (int j = i + 1; j < m.length; j++)
                if (m[i][0] > m[j][0])
                {
                    //copy the whole of row i into "tempArray"
                    double[] tempArray = m[i];
                    //do a switcheroo
                    m[i] = m[j];
                    m[j] = tempArray;
                }
    }

    public static long factorial(long n) {
        if (n <= 1)
            return 1;
        else
            return n * factorial(n - 1);
    }



    private void permute(String str, int l, int r)
    {
        if (l == r)
            TimelineActivity.combination += str + ",";
        else
        {
            for (int i = l; i <= r; i++)
            {
                str = swap(str,l,i);
                permute(str, l+1, r);
                str = swap(str,l,i);
            }
        }

    }
    public String swap(String a, int i, int j)
    {
        char temp;
        char[] charArray = a.toCharArray();
        temp = charArray[i] ;
        charArray[i] = charArray[j];
        charArray[j] = temp;
        return String.valueOf(charArray);
    }


    //End of functions
}