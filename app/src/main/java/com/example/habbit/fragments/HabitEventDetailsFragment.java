package com.example.habbit.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.habbit.R;
import com.example.habbit.activities.MapActivity;
import com.example.habbit.handlers.HabitEventInteractionHandler;
import com.example.habbit.handlers.HabitInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.util.List;

public class HabitEventDetailsFragment extends DialogFragment {

    String city;
    String province;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    public HabitEventDetailsFragment(){
        // required empty class constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param habitEvent The HabitEvent whose details we want to view, of type {@link Habit}.
     * @return A new instance of fragment {@link HabitEventDetailsFragment}.
     */
    public static HabitEventDetailsFragment newInstance(HabitEvent habitEvent, Habit habit){
        Bundle args = new Bundle();
        args.putSerializable("habitEvent", habitEvent);
        args.putSerializable("habit", habit);

        HabitEventDetailsFragment fragment = new HabitEventDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        /* Inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_habit_event_details,null);

        /* connect Views to xml text fields */
        TextView commentField = view.findViewById(R.id.habit_event_comment_field);
//        TextView completedOnTimeField = view.findViewById(R.id.completed_on_time_field);

        ImageView eventPhoto = view.findViewById(R.id.eventPhoto);

        Button viewLocationBtn = view.findViewById(R.id.view_location_link);

        TextView locText = view.findViewById(R.id.location_text_v);

        /* get the details of the habit, if there are any to get */
        final HabitEvent selectedHabitEvent = (HabitEvent) (getArguments() != null ?
                getArguments().getSerializable("habitEvent") : null);

        final Habit selectedHabit = (Habit) (getArguments() != null ?
                getArguments().getSerializable("habit") : null);

        HabitEventInteractionHandler handler = new HabitEventInteractionHandler(selectedHabit);


        /* set the text for the TextViews (null if habit is null) */
        commentField.setText(selectedHabitEvent != null ? selectedHabitEvent.getComment(): null);
//        completedOnTimeField.setText(selectedHabitEvent != null ? String.valueOf(selectedHabitEvent.isCompletedOnTime()):null);

        // get the photo into ImageView
        assert selectedHabitEvent != null;
        handler.getHabitEventPhoto(selectedHabitEvent, eventPhoto);

        if(selectedHabitEvent != null) {
            if (selectedHabitEvent.getLongitude() != 0 && selectedHabitEvent.getLatitude() != 0) {
                Thread thread1 = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        GeocoderNominatim geocoder = new GeocoderNominatim(getContext().toString());
                        String theAddress;
                        try {
                            List<Address> addresses = geocoder.getFromLocation(selectedHabitEvent.getLatitude(),
                                    selectedHabitEvent.getLongitude(), 1);
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


        //view map
        viewLocationBtn.setOnClickListener(view3 ->{
            if(selectedHabitEvent.getLatitude() == 0 && selectedHabitEvent.getLongitude() == 0){
                Toast.makeText(getActivity(), "No Location Selected yet, Hop to it!", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(getContext(), MapActivity.class);
                intent.putExtra("justView",1);
                intent.putExtra("habitEvent", selectedHabitEvent);
                intent.putExtra("habit", selectedHabit);

                startActivity(intent);
            }


        });

        /* initialize the "View HabitEvent" dialog */
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setNeutralButton("Close",null)
                .setNegativeButton("Delete", (dialogInterface, i) -> {
                    handler.deleteHabitEvent(selectedHabitEvent);
                    assert selectedHabit != null;
                    int progress = selectedHabit.getProgress();
                    progress--;
                    selectedHabit.setProgress(progress);
                    System.out.println("DEC = "+selectedHabit.getProgress());
                })
                .setPositiveButton("Edit",(dialogInterface, i) ->
                        HabitEventEntryFragment.newInstance(selectedHabitEvent, selectedHabit)
                        .show(requireActivity().getSupportFragmentManager(), "ADD_HABIT_EVENT"))
                .create();
    }

}
