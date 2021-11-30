package com.example.habbit.activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomRequestList;
import com.example.habbit.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * Class that handles the display behaviour of the follow requests page
 */
public class FollowRequestsActivity extends AppCompatActivity {

    // get instance of firestore
    final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    // adapters and arraylist for friend list
    CustomRequestList requestAdapter;
    ArrayList<String> requests;
    ListView requestListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_requests);

        // custom top toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());

        // get references to UI elements and attach custom adapter
        requests = User.getRequests();
        requestAdapter = new CustomRequestList(this, requests);
        requestListView = findViewById(R.id.requests_list);
        requestListView.setAdapter(requestAdapter);

        // initialize/update the list of requests every time there is a change made
        userCollectionReference.document(User.getUserID()).addSnapshotListener((value, error) -> {
            User.clearRequests();
            if (value.get("Requests") == null) {
                requests = new ArrayList<>();
            } else {
                requests = (ArrayList<String>) value.get("Requests");
            }
            for (String habbitorID: requests) {
                User.addRequest(habbitorID);
            }
            requestAdapter.notifyDataSetChanged();
        });

    }
}