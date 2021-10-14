package travelarchitect.com.travelarchitect;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

public class PlaceActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    final String TAG = "PlaceActivity";
    String coor = "";

    GoogleMap map;

    ArrayList<String> destinations;
    ArrayList<String> destinationsCoor;
    ArrayList<String> destinationsTags;
    ArrayList<Event> events;

    RecyclerView.Adapter adapter;
    RecyclerView.Adapter eventAdapter;

    EditText placeField;
    ListView placeList;
    ArrayList<String> places;
    ArrayList<String> tags;
    ArrayAdapter<String> placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        Toast.makeText(PlaceActivity.this, "Version 2 weeks left", Toast.LENGTH_SHORT).show();

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.rgb(0, 0, 0));

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinations = new ArrayList<>();
        destinationsCoor = new ArrayList<>();
        destinationsTags = new ArrayList<>();

        events = new ArrayList<>();

        adapter = new MyAdapter(destinations, destinationsTags, destinationsCoor);
        eventAdapter = new EventAdapter(this, events);
        loadEventData();

        RecyclerView eventsList = (RecyclerView) findViewById(R.id.events_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        eventsList.setLayoutManager(layoutManager);
        eventsList.setAdapter(eventAdapter);

        placeField = (EditText) findViewById(R.id.place_field);
        placeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //When search button is pressed
                if (i == EditorInfo.IME_ACTION_SEARCH) {
//                    String place = placeField.getText().toString();
//                    searchPlace(place);
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        placeList = (ListView) findViewById(R.id.lv);
        placeList.setNestedScrollingEnabled(true);
        places = new ArrayList<>();
        tags = new ArrayList<>();
        placeAdapter = new ArrayAdapter<String>(PlaceActivity.this, android.R.layout.simple_list_item_1, places);
        placeList.setAdapter(placeAdapter);

        findViewById(R.id.add_destination).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        findViewById(R.id.fab).setOnClickListener(this);
        findViewById(R.id.place_field).setOnClickListener(this);
        //this is left as null so that it will always reset to blue
        checkIfInList("null");

        ListView lv = (ListView) findViewById(R.id.lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String newDest = (String) parent.getItemAtPosition(position);
                String newTag = tags.get(position);
                //Toast.makeText(PlaceActivity.this, "This part of the code has been reached.", Toast.LENGTH_SHORT).show();
                if (destinations.contains(newDest))
                    Toast.makeText(PlaceActivity.this, newDest + " is already in your destinations list.", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(PlaceActivity.this, "Destination added!", Toast.LENGTH_SHORT).show();
                    destinations.add(newDest);
                    destinationsTags.add(newTag);

                    String newCoor = "placeholder postal code";
                    destinationsCoor.add(newCoor);
                    Toast.makeText(PlaceActivity.this, "coordinate added: " + newCoor, Toast.LENGTH_SHORT).show();
//Used jsoup to get postal code from yelp. Not needed anymore now with google maps
//                    //This is to give permission to jsoup to access website data
//                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//                    String newCoor = "";
////                    String url = "https://www.google.com/search?q=" + newDest + " coordinates";
//                    String url = "https://www.yelp.com.sg/search?find_desc=" + newDest;
//                    try {
//                        Document doc = Jsoup.connect(url).userAgent("mozilla/17.0").get();
//                        Elements ele = doc.select("div.secondary-attributes");
//                        for (Element extractSpan:ele){
//                            newCoor = extractSpan.text();
//                            String[] splits = newCoor.split("Singapore");
//                            for(String s : splits)
//                                newCoor = s;
//                            newCoor = newCoor.substring(1,7);
//                        }
//
//                        String qwer = doc.toString();
//                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clip = ClipData.newPlainText("", qwer);
//                        clipboard.setPrimaryClip(clip);
//                    }
//                    catch (IOException e){
//                        e.printStackTrace();
//                    }

                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void loadEventData() {

        String description = "Dive deeper into Singapore’s diverse culture and heritage through various engaging activities such as specially-curated exhibitions, heritage trails, competitions, cultural performances, community activities and more.";
        String imageUrl = "http://www.visitsingapore.com/content/dam/desktop/global/tourism-editorials/honeycombers/top-events-singapore/apr17-update/heritage-1670x940.jpg";
        Event event = new Event("Singapore Heritage Festival", "28 Apr-14 May", "Culture", description, imageUrl);
        events.add(event);

        String description1 = "Singapore Philatelic Museum puts together a unique exhibition where you can explore more about the different celebrations of world culture through eggs.";
        String imageUrl1 = "http://www.visitsingapore.com/content/dam/desktop/global/tourism-editorials/honeycombers/top-events-singapore/apr17-update/laojiu-1670x940.jpg";
        Event event1 = new Event("Lao Jiu: The Musical", "28 Apr-14 May", "Culture", description1, imageUrl1);
        events.add(event1);

        DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference("events");
        eventDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // loop through events on the database and store the events' details into events ArrayList
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String name = (String) eventSnapshot.child("name").getValue();
                    String description = (String) eventSnapshot.child("description").getValue();
                    String imageUrl = (String) eventSnapshot.child("imageUrl").getValue();
                    String date = (String) eventSnapshot.child("date").getValue();
                    String category = (String) eventSnapshot.child("category").getValue();

                    Event event = new Event(name, date, category, description, imageUrl);
                    events.add(event);
                }

                // notify the adapter on data change, so the recycler view will be updated
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sg = new LatLng(1.369643, 103.818853);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sg, 8);
        googleMap.addMarker(new MarkerOptions().position(sg)
                .title("Welcome to Singapore!"));
        googleMap.animateCamera(cameraUpdate);
        map = googleMap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.place_field:
                hideInfo(true);
                //this is left as null so that it will always reset to blue
                checkIfInList("null");
                launchPlaceAutocomplete();
                break;
            case R.id.add_destination:

                String newDest = (String) ((TextView) findViewById(R.id.place_name)).getText();

                if (destinations.contains(newDest)){
                    //When destination is already added before
                    Toast.makeText(PlaceActivity.this, "This destination is already in your destinations list.", Toast.LENGTH_SHORT).show();
                }
                else{
                    destinations.add(newDest);
                    destinationsCoor.add(coor.substring(coor.indexOf("(") + 1, coor.indexOf(")")));
                    searchPlace(newDest);
                }

                adapter.notifyDataSetChanged();
                break;
            case R.id.submit:
                if(destinations.size()>=3 && destinationsCoor.size()>=3){
                    Intent intent = new Intent(PlaceActivity.this, TimelineActivity.class);
                    intent.putExtra("dest", destinations);
                    intent.putExtra("destCoor", destinationsCoor);
                    intent.putExtra("destTags", destinationsTags);
                    startActivity(intent);
                    //Clear list before starting
//                    destinations.clear();
//                    destinationsCoor.clear();
//                    destinationsTags.clear();

                    Toast.makeText(PlaceActivity.this, "tags array:\n" + Arrays.toString(destinationsTags.toArray()) + "\n\ncoor array:\n" + Arrays.toString(destinationsCoor.toArray()), Toast.LENGTH_LONG).show();


                    adapter.notifyDataSetChanged();
                }
                else    Toast.makeText(PlaceActivity.this, "Please select at least 3 attractions.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab:
                Toast.makeText(PlaceActivity.this, "tags array:\n" + Arrays.toString(destinationsTags.toArray()) + "\n\ncoor array:\n" + Arrays.toString(destinationsCoor.toArray()), Toast.LENGTH_LONG).show();
                showDestinationDialog();
                break;
        }
    }

    private void showDestinationDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_destination);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        RecyclerView destinationsList = (RecyclerView) dialog.findViewById(R.id.destinations_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        destinationsList.setLayoutManager(layoutManager);
        destinationsList.setAdapter(adapter);

        if (destinations.size() < 1) {
            dialog.findViewById(R.id.feedback).setVisibility(View.VISIBLE);
            destinationsList.setVisibility(View.GONE);
        } else {
            dialog.findViewById(R.id.feedback).setVisibility(View.GONE);
            destinationsList.setVisibility(View.VISIBLE);
        }

        ImageView closeButton = (ImageView) dialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void launchPlaceAutocomplete() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("SG")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                findViewById(R.id.add_destination).setVisibility(View.VISIBLE);

                Place place = PlaceAutocomplete.getPlace(this, data);
                String placeName = (String) place.getName();
                String placeAddress = (String) place.getAddress();
                Log.i("place address", placeAddress);

                LatLng lat = place.getLatLng();
                Log.i("place latlng", lat.toString());
                coor = lat.toString();


                String placeWebsite = "";
                if (place.getWebsiteUri() != null) placeWebsite = place.getWebsiteUri().toString();

                String placeRating = "";
                if (place.getRating() != -1.0f) placeRating = String.valueOf(place.getRating());

                updateMapLocation(placeName, placeAddress, placeWebsite, placeRating, place.getLatLng());

                    checkIfInList(placeName);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void updateMapLocation(String placeName, String placeAddress, String placeWebsite, String placeRating, LatLng latLng) {
        findViewById(R.id.place_name).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.place_name)).setText(placeName);

        findViewById(R.id.place_address).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.place_address)).setText(placeAddress);

        if (!placeWebsite.equals("")) {
            findViewById(R.id.place_website).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.place_website)).setText(placeWebsite);
        }else {
            findViewById(R.id.place_website).setVisibility(View.GONE);
        }

        if (!placeRating.equals("")) {
            findViewById(R.id.place_rating).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.place_rating)).setText("Rating: " + placeRating + "/5.0");
        }else {
            findViewById(R.id.place_rating).setVisibility(View.GONE);
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng)
                .title(placeName));
        map.animateCamera(cameraUpdate);
    }

    public void hideInfo(Boolean hidden) {
        if (hidden == true){
            findViewById(R.id.place_name).setVisibility(View.GONE);
            findViewById(R.id.place_address).setVisibility(View.GONE);
            findViewById(R.id.add_destination).setVisibility(View.GONE);
            findViewById(R.id.place_website).setVisibility(View.GONE);
            findViewById(R.id.place_rating).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.place_name).setVisibility(View.VISIBLE);
            findViewById(R.id.place_address).setVisibility(View.VISIBLE);
            findViewById(R.id.add_destination).setVisibility(View.VISIBLE);
            findViewById(R.id.place_website).setVisibility(View.VISIBLE);
            findViewById(R.id.place_rating).setVisibility(View.VISIBLE);
        }
    }

    public void checkIfInList(String placeName) {
        if (destinations.contains(placeName)){
            //When destination IS in the list of places
            final Button button = (Button) findViewById(R.id.add_destination);
            button.setText("Added!");
            button.setBackgroundColor(Color.GRAY);
        }
        else {
            //When destination IS NOT in the list of places
            final Button button = (Button) findViewById(R.id.add_destination);
            button.setText("Add to list");
            button.setBackgroundColor(Color.BLUE);
        }
    }

    public void searchPlace(final String place) {
//        Query query = FirebaseDatabase.getInstance().getReference("attractionInfo").orderByKey().equalTo(place);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getChildrenCount() > 0) {
//                    // place found
//                    for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
//                        String destination = placeSnapshot.getKey();
//                        Toast.makeText(PlaceActivity.this, destination, Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    // place not found
//                    Toast.makeText(PlaceActivity.this, "Destination not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        //Toast.makeText(PlaceActivity.this, "Search in progress...", Toast.LENGTH_SHORT).show();
        places.clear();
        tags.clear();
        placeAdapter.notifyDataSetChanged();



        DatabaseReference placeDatabase = FirebaseDatabase.getInstance().getReference("attractionInfo");
        placeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int noOfResults = 0;
                Toast.makeText(PlaceActivity.this, "Search in progress...", Toast.LENGTH_SHORT).show();
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    String destination = placeSnapshot.getKey();
                    String categories = (String) placeSnapshot.getValue();
                    //For normal search(contains word in name or tags), change if statement to:
                    // "destination.equalsIgnoreCase(place) || destination.contains(place) || categories.equalsIgnoreCase(place) || categories.contains(place)"
                    if (destination.equalsIgnoreCase(place)) {
                        //When tag is found in Firebase
                        Log.i("Place", destination);
                        places.add(destination);
                        tags.add(categories);
                            destinationsTags.add(categories);
                            noOfResults++;
                            Toast.makeText(PlaceActivity.this, "Tag has been added.", Toast.LENGTH_SHORT).show();
                    }
                }
                if(destinations.size()!=destinationsTags.size()){
                    //When tag is not found, add a log to Firebase
                    DatabaseReference tagNotFound = FirebaseDatabase.getInstance().getReference("tagNotFound");
                    tagNotFound.child(place).setValue(place);
                    //Add this to tag array
                    destinationsTags.add(place + " has no tags...");
                    //Toast
                    Toast.makeText(PlaceActivity.this, "Tag not found. Added to Firebase for consideration.", Toast.LENGTH_SHORT).show();
                }
                checkIfInList(place);

                if (places.isEmpty()) {
                    ((TextView) findViewById(R.id.destination_found)).setText("No results found for ''" + place + "''.");
                }else {
                    ((TextView) findViewById(R.id.destination_found)).setText(noOfResults + " results found for ''" + place + "''.");
                }
                placeAdapter.notifyDataSetChanged();
                //Don't show results of Firebase
                findViewById(R.id.destination_found).setVisibility(View.GONE);
                findViewById(R.id.lv).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
