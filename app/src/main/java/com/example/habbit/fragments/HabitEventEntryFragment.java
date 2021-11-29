package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.example.habbit.R;
import com.example.habbit.activities.HabitEventsActivity;
import com.example.habbit.activities.MapActivity;
import com.example.habbit.activities.ProfileActivity;
import com.example.habbit.adapters.CustomHabitEventList;
import com.example.habbit.handlers.HabitEventInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.google.api.Distribution;
import com.squareup.picasso.Picasso;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HabitEventEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HabitEventEntryFragment extends DialogFragment {

    HabitEvent existingHabitEvent;
    private double lat;
    private double lon;
    String city = "none";
    String province = "none";
    private Handler mHandler = new Handler(Looper.getMainLooper());

    TextView locText;

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Log.d("latlon","inOnres");
            if(result != null && result.getResultCode() == RESULT_OK){
                Log.d("latlon","inOnresif1");

                if(result.getData() != null){
                    Log.d("latlon","inOnresif2");

                    lat = result.getData().getDoubleExtra("lat",0);
                    lon = result.getData().getDoubleExtra("lon",0);
                    Log.d("latslons","lat: "+lat+"lon: "+lon);

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            GeocoderNominatim geocoder = new GeocoderNominatim(getContext().toString());
                            String theAddress;
                            try  {
                                List<Address> addresses = geocoder.getFromLocation(lat,
                                        lon, 1);
                                StringBuilder sb = new StringBuilder();
                                if (addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    int n = address.getMaxAddressLineIndex();
                                    Log.d("Test", "CountryName: " + address.getCountryName());
                                    Log.d("Test", "CountryCode: " + address.getCountryCode());
                                    Log.d("Test", "PostalCode " + address.getPostalCode());
//                        Log.d("Test", "FeatureName " + address.getFeatureName()); //null
                                    Log.d("Test", "City: " + address.getAdminArea());
                                    Log.d("Test", "Locality: " + address.getLocality());
                                    //bundle.putString("province", address.getAdminArea());
                                    province = address.getAdminArea();
                                    city = address.getLocality();
                                    //TextView textView = (TextView) get().findViewById(R.id.location_text);
                                    mHandler.post(new Runnable() {
                                        @SuppressLint("ClickableViewAccessibility")
                                        @Override
                                        public void run() {
                                            locText.setCompoundDrawablesWithIntrinsicBounds(
                                                    R.drawable.ic_baseline_pin_drop_24,//left
                                                    0, //top
                                                    0, //right
                                                    0);//bottom
                                            //locText.setText("hello");
                                            locText.setText(city + ", " + province);
                                            locText.setGravity(Gravity.CENTER_VERTICAL);
                                        }
                                    });
                                    //bundle.putString("city", address.getLocality());
                                    Log.d("Test", "Premises: " + address.getPremises()); //null
                                    Log.d("Test", "SubAdminArea: " + address.getSubAdminArea());
                                    Log.d("Test", "SubLocality: " + address.getSubLocality());
//                        Log.d("Test", "SubThoroughfare: " + address.getSubThoroughfare()); //null
//                        Log.d("Test", "getThoroughfare: " + address.getThoroughfare()); //null
                                    Log.d("Test", "Locale: " + address.getLocale());
                                    for (int i=0; i<=n; i++) {
                                        if (i!=0)
                                            sb.append(", ");
                                        sb.append(address.getAddressLine(i));
                                    }
                                    theAddress = sb.toString();
                                } else {
                                    theAddress = null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                    Log.d("latlon","LAT: "+lat+"LON: "+lon);
                    Log.d("latlon","city: "+city+"province: "+province);

                }


            }
        }
    });

    public HabitEventEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param habitEvent HabitEvent
     * @return A new instance of fragment HabitEventEntryFragment.
     */
    public static HabitEventEntryFragment newInstance(HabitEvent habitEvent, Habit habit) {
        HabitEventEntryFragment fragment = new HabitEventEntryFragment();
        Bundle args = new Bundle();
        args.putSerializable("habitEvent", habitEvent);
        args.putSerializable("habit", habit);
        fragment.setArguments(args);
        return fragment;
    }

    public void incrementProgress(Habit habit) {
        int progress = habit.getProgress();
        progress++;
        habit.setProgress(progress);
        System.out.println("THE PROGRESS IS " + progress);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /* inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_habit_event_entry, null);

        Button addPhotoBtn = view.findViewById(R.id.add_photo_link);
        Button addLocationBtn = view.findViewById(R.id.add_location_link);
        locText = view.findViewById(R.id.location_text);

        /* get the habit event and habit details from args if exists */
        existingHabitEvent = (HabitEvent) (getArguments() !=null ?
                getArguments().getSerializable("habitEvent") : null);
        assert getArguments() != null;
        Habit habit = (Habit) getArguments().getSerializable("habit");

        HabitEvent habitEvent = new HabitEvent("","",0,0);

        EditText commentField = view.findViewById(R.id.edit_habit_event_comment);

        ImageView imageView = view.findViewById(R.id.image_habit_event);

        HabitEventInteractionHandler handler = new HabitEventInteractionHandler(habit);

        if(existingHabitEvent != null) {
            if (existingHabitEvent.getLongitude() != 0 && existingHabitEvent.getLatitude() != 0) {
                lat = existingHabitEvent.getLatitude();
                lon = existingHabitEvent.getLongitude();
                Thread thread1 = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        GeocoderNominatim geocoder = new GeocoderNominatim(getContext().toString());
                        String theAddress;
                        try {
                            List<Address> addresses = geocoder.getFromLocation(existingHabitEvent.getLatitude(),
                                    existingHabitEvent.getLongitude(), 1);
                            StringBuilder sb = new StringBuilder();
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                int n = address.getMaxAddressLineIndex();
                                Log.d("Test", "CountryName: " + address.getCountryName());
                                Log.d("Test", "CountryCode: " + address.getCountryCode());
                                Log.d("Test", "PostalCode " + address.getPostalCode());
//                        Log.d("Test", "FeatureName " + address.getFeatureName()); //null
                                Log.d("Test", "City: " + address.getAdminArea());
                                Log.d("Test", "Locality: " + address.getLocality());
                                //bundle.putString("province", address.getAdminArea());
                                province = address.getAdminArea();
                                city = address.getLocality();
                                //TextView textView = (TextView) get().findViewById(R.id.location_text);
                                mHandler.post(new Runnable() {
                                    @SuppressLint("ClickableViewAccessibility")
                                    @Override
                                    public void run() {
                                        locText.setCompoundDrawablesWithIntrinsicBounds(
                                                R.drawable.ic_baseline_pin_drop_24,//left
                                                0, //top
                                                0, //right
                                                0);//bottom
                                        //locText.setText("hello");
                                        locText.setText(city + ", " + province);
                                        locText.setGravity(Gravity.CENTER_VERTICAL);
                                    }
                                });
                                //bundle.putString("city", address.getLocality());
                                Log.d("Test", "Premises: " + address.getPremises()); //null
                                Log.d("Test", "SubAdminArea: " + address.getSubAdminArea());
                                Log.d("Test", "SubLocality: " + address.getSubLocality());
//                        Log.d("Test", "SubThoroughfare: " + address.getSubThoroughfare()); //null
//                        Log.d("Test", "getThoroughfare: " + address.getThoroughfare()); //null
                                Log.d("Test", "Locale: " + address.getLocale());
                                for (int i = 0; i <= n; i++) {
                                    if (i != 0)
                                        sb.append(", ");
                                    sb.append(address.getAddressLine(i));
                                }
                                theAddress = sb.toString();
                            } else {
                                theAddress = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread1.start();
            }
        }

        /* initialize add habit event dialog */
        AlertDialog addDialog;
        if (existingHabitEvent != null) {
            System.out.println("HEY WE MADE IT HERE");
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Edit Habit Event")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", null)
                    .show();
            commentField.setText(existingHabitEvent.getComment());
            handler.getHabitEventPhoto(existingHabitEvent, imageView);
        } else {
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Log Habit Event")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", null)
                    .show();
        }

        addLocationBtn.setOnClickListener(view3 ->{
            if(existingHabitEvent == null){
                Intent intent = new Intent(getContext(), MapActivity.class);
                intent.putExtra("justView", 0);
                intent.putExtra("habitEvent", habitEvent);
                intent.putExtra("habit", habit);
                startForResult.launch(intent);

            }
            else{
                Intent intent = new Intent(getContext(), MapActivity.class);
                intent.putExtra("justView", 0);
                intent.putExtra("habitEvent", existingHabitEvent);
                intent.putExtra("habit", habit);
                startForResult.launch(intent);
            }

        });

        addPhotoBtn.setOnClickListener(view2 -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.setFragmentResultListener("requestKey", this, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                    String result = bundle.getString("bundleKey");
                    System.out.println("Image URL is: "+result);
                    Uri uri = Uri.parse(result);
                    Picasso.get().load(uri).into(imageView);
                }
            });
            HabitEventPhotoFragment.newInstance(existingHabitEvent, habit).show(fragmentManager, "ADD_PHOTO_EVENT");
        });

        // when a user confirms adding a habit event, we should add it to the list of habit events
        // associated with that Habit
        Button positiveButton = addDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view1 -> {
            String comment = commentField.getText().toString();

            boolean errorFlag = false;

            /* validate each field (photo and geolocation too once we get that working */
            if (comment.length() > 20) {
                commentField.setError("Comment cannot be greater than 20 characters");
                errorFlag = true;
            }

            /* If there are any errors, do not add the habit to the lis yet and do not dismiss dialog */
            if (errorFlag) {
                Toast.makeText(getActivity(), "Please fix errors", Toast.LENGTH_SHORT).show();
            } else {
                /* part where either create a new habit event OR adjusting an existing one */
                if (existingHabitEvent == null) {
                    Log.d("WOW","went here");
                    existingHabitEvent = new HabitEvent(comment, "", lat, lon);
                    existingHabitEvent.setCity(city);
                    existingHabitEvent.setProvince(province);
                    // increment progress
                    incrementProgress(habit);
                    handler.addHabitEvent(existingHabitEvent, imageView);
                } else {
                    Log.d("latslons","lats: "+ lat+"lons: "+lon);
                    existingHabitEvent.setComment(comment);
                    existingHabitEvent.setLatitude(lat);
                    existingHabitEvent.setLongitude(lon);
                    existingHabitEvent.setCity(city);
                    existingHabitEvent.setProvince(province);
                    handler.updateHabitEvent(existingHabitEvent, imageView);
                }
                System.out.println("habit event ="+existingHabitEvent.getId());
                addDialog.dismiss();
            }
        });

        return addDialog;
    }
}