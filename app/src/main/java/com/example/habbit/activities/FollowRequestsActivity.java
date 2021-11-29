package com.example.habbit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ListView;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabbitorList;
import com.example.habbit.adapters.CustomRequestList;
import com.example.habbit.models.Habbitor;
import com.example.habbit.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FollowRequestsActivity extends AppCompatActivity {

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

        DocumentReference docRef = userCollectionReference.document(User.getUserID());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                User.clearRequests();
                ArrayList<String> requests;
                if (documentSnapshot.get("Requests") == null) {
                    requests = new ArrayList<String>();
                } else {
                    requests = (ArrayList<String>) documentSnapshot.get("Requests");
                }
                User.setRequests(requests);
            }
        });

    }
}