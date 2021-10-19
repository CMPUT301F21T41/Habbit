package com.example.habbit;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //private final DocumentReference myDocRef = FirebaseFirestore.getInstance().document("users");
    //db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void saveQuote(View v){
        EditText nameView = (EditText) findViewById(R.id.edit_username);
        EditText passView = (EditText) findViewById(R.id.edit_password);
        String nameText = nameView.getText().toString();
        String passText = passView.getText().toString();

        HashMap<String, String> data = new HashMap<>();

        data.put("Username",nameText);
        data.put("Password",passText);

        collectionReference.document(nameText).set(data);


    }
}