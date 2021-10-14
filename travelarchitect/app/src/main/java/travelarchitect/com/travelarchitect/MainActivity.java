package travelarchitect.com.travelarchitect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference uDatabase;

    private final String TAG = "MainActivity";

    EditText countryField;
    EditText dayField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Check if app is updated
        Toast.makeText(MainActivity.this, "version qwer", Toast.LENGTH_LONG).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    uDatabase = FirebaseDatabase.getInstance().getReference("user").child(user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };

        countryField = (EditText) findViewById(R.id.country_field);
        dayField = (EditText)  findViewById(R.id.day_field);

        findViewById(R.id.checkbox0).setOnClickListener(this);
        findViewById(R.id.checkbox1).setOnClickListener(this);
        findViewById(R.id.checkbox2).setOnClickListener(this);
        findViewById(R.id.checkbox3).setOnClickListener(this);
        findViewById(R.id.checkbox4).setOnClickListener(this);
        findViewById(R.id.checkbox5).setOnClickListener(this);
        findViewById(R.id.checkbox6).setOnClickListener(this);
        findViewById(R.id.checkbox7).setOnClickListener(this);
        findViewById(R.id.checkbox8).setOnClickListener(this);
        findViewById(R.id.checkbox9).setOnClickListener(this);
        findViewById(R.id.checkbox10).setOnClickListener(this);
        findViewById(R.id.checkbox_child_friendly).setOnClickListener(this);
        findViewById(R.id.next_button).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_button:
                saveTripInfo();
                break;
            case R.id.checkbox_child_friendly:
                boolean checked = ((CheckBox) view).isChecked();
                if (checked)
                    uDatabase.child("newTrip").child("extra").child("child friendly").setValue(true);
                else
                    uDatabase.child("newTrip").child("extra").child("child friendly").setValue(false);
                break;

            // for checkbox
            default:
                selectInterests(view);
                break;
        }
    }

    public void saveTripInfo() {
        boolean cancel = false;
        View focusView = null;

        DatabaseReference tripDb = uDatabase.child("newTrip");

        String day = dayField.getText().toString();

        if (day.isEmpty()) {
            dayField.setError("How many days you will spend?");
            cancel = true;
            focusView = dayField;
        }

        String country = countryField.getText().toString();

        if (country.isEmpty()) {
            countryField.setError("Where you want to visit?");
            cancel = true;
            focusView = countryField;
        }

        if (cancel) {
            focusView.requestFocus();
        }else {
            tripDb.child("country").setValue(country);
            tripDb.child("day").setValue(day);

            Intent intent = new Intent(this, PlaceActivity.class);
            startActivity(intent);
        }

    }

    public void selectInterests(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        DatabaseReference interestsDb = uDatabase.child("newTrip").child("interest");

        switch (view.getId()) {
            case R.id.checkbox0:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_0)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_0)).setValue(false);
                break;
            case R.id.checkbox1:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_1)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_1)).setValue(false);
                break;
            case R.id.checkbox2:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_2)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_2)).setValue(false);
                break;
            case R.id.checkbox3:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_3)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_3)).setValue(false);
                break;
            case R.id.checkbox4:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_4)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_4)).setValue(false);
                break;
            case R.id.checkbox5:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_5)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_5)).setValue(false);
                break;
            case R.id.checkbox6:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_6)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_6)).setValue(false);
                break;
            case R.id.checkbox7:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_7)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_7)).setValue(false);
                break;
            case R.id.checkbox8:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_8)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_8)).setValue(false);
                break;
            case R.id.checkbox9:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_9)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_9)).setValue(false);
                break;
            case R.id.checkbox10:
                if (checked)
                    interestsDb.child(getResources().getString(R.string.interest_10)).setValue(true);
                else
                    interestsDb.child(getResources().getString(R.string.interest_10)).setValue(false);
                break;

        }
    }
}
