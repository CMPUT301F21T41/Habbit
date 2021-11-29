// Created by plusminus on 00:23:14 - 03.10.2008
package com.example.habbit.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.example.habbit.BuildConfig;
import com.example.habbit.R;
import com.example.habbit.fragments.MapFragment;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.squareup.picasso.Picasso;

import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;

import java.util.ArrayList;

/**
 * Default map view activity.
 *
 * @author Manuel Stahl
 */
public class MapActivity extends AppCompatActivity {
    private static final String MAP_FRAGMENT_TAG = "org.osmdroid.MAP_FRAGMENT_TAG";
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private double lat;
    private double lon;
    String city;
    String province;

    /**
     * The idea behind that is to force a MapView refresh when switching from offline to online.
     * If you don't do that, the map may display - when online - approximated tiles
     * * that were computed when offline
     * * that could be replaced by downloaded tiles
     * * but as the display is not refreshed there's no try to get better tiles
     *
     * @since 6.0
     */
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                starterMapFragment.invalidateMapView();
            } catch (NullPointerException e) {
                // lazy handling of an improbable NPE
            }
        }
    };

    private MapFragment starterMapFragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_map);

        //custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));




        //get habitevent
        Intent intent = getIntent();
        int justView = (int) intent.getSerializableExtra("justView");
        HabitEvent habitEvent = (HabitEvent) intent.getSerializableExtra("habitEvent");
        Habit habit = (Habit) intent.getSerializableExtra("habit");

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //MainActivity.updateStoragePreferences(this);    //needed for unit tests

        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        /*requestPermissionsIfNecessary(new String[] {
                // if you need to show the current location, uncomment the line below

                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                //Manifest.permission.WRITE_EXTERNAL_STORAGE
        });*/

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                         != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Allow location access for habbit in settings!", Toast.LENGTH_LONG).show();
            finish();

        }

        FragmentManager fm = this.getSupportFragmentManager();
        //FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fm.setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                lat = bundle.getDouble("latKey");
                lon = bundle.getDouble("lonKey");
                city = bundle.getString("city");
                province = bundle.getString("province");
                Log.d("mapreturn","LAT: "+lat+"LON: "+lon);
                Log.d("mapreturn","city: "+city+"Province: " +province);
                Intent intentB = new Intent();
                intentB.putExtra("lat",lat);
                intentB.putExtra("lon",lon);
                intentB.putExtra("city",city);
                intentB.putExtra("province",province);
                setResult(RESULT_OK,intentB);
                finish();

                /*Uri uri = Uri.parse(result);
                Picasso.get().load(uri).into(imageView);*/
            }
        });
        if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("habitEvent",habitEvent);
            bundle.putSerializable("habit",habit);
            bundle.putSerializable("justView",justView);
            MapFragment starterMapFragment = MapFragment.newInstance();
            starterMapFragment.setArguments(bundle);
            fm.beginTransaction().add(R.id.map_container, starterMapFragment, MAP_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * small example of keyboard events on the mapview
     * page up = zoom out
     * page down = zoom in
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
                starterMapFragment.zoomIn();
                return true;
            case KeyEvent.KEYCODE_PAGE_UP:
                starterMapFragment.zoomOut();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }




    /**
     * @since 6.0
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(networkReceiver);
        super.onDestroy();
    }
}