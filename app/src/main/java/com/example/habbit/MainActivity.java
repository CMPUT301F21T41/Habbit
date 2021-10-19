package com.example.habbit;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //private final DocumentReference myDocRef = FirebaseFirestore.getInstance().document("users");
    //db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void saveQuote(View v){
        EditText usernameView = (EditText) findViewById(R.id.edit_username);
        EditText passView = (EditText) findViewById(R.id.edit_password);
        EditText nameView = (EditText) findViewById(R.id.edit_name);
        EditText habitTitleView = (EditText) findViewById(R.id.edit_habit_title);
        EditText habitReasonView = (EditText) findViewById(R.id.edit_habit_reason);
        String usernameText = usernameView.getText().toString();
        String passText = passView.getText().toString();
        String nameText = nameView.getText().toString();
        String habitTitleText = habitTitleView.getText().toString();
        String habitReasonText = habitReasonView.getText().toString();

        HashMap<String, Object> userData = new HashMap<>();
        HashMap<String, Object> habitData = new HashMap<>();

        userData.put("Username",usernameText);
        userData.put("Password",passText);
        userData.put("Name",nameText);

        if(!habitReasonText.isEmpty() && !habitTitleText.isEmpty() ){
            Date date = new Date();
            Habit newHabit = new Habit(habitTitleText,habitReasonText,date);
            habitData.put(habitTitleText,newHabit);
            collectionReference.document(usernameText).collection("Habits").document(habitTitleText).set(habitData);
        }

        collectionReference.document(usernameText).set(userData);


    }
}