package com.example.a_cyg.scores;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AddScoreActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference dataRef;
    private final int MAX_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.upload_score);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText nameField = ((EditText) findViewById(R.id.player_name_input));
                EditText scoreField = ((EditText) findViewById(R.id.player_score_input));
                final String name = nameField.getText().toString();
                final String score = scoreField.getText().toString();
                if(name.isEmpty() || score.isEmpty()) {
                    Snackbar.make(view, "Please enter both player name and score.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    //Get reference to tetris scores database
                    database = FirebaseDatabase.getInstance();
                    dataRef = database.getReference("scores/tetris");
                    final int scoreVal = Integer.parseInt(score);

                    //Use SingleValueEvent to read data once.
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getChildrenCount() < MAX_SIZE) {

                                //If number of scores in database is less than max specific high score number,
                                //simply push score to data base.
                                postData(name, scoreVal, "");

                            } else {

                                //Query for data snapshot with lowest score on data base
                                dataRef.orderByChild("score").limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        //Fetch data snapshot with lowest score and score value.
                                        DataSnapshot currentDataSnapshot = dataSnapshot.getChildren().iterator().next();
                                        Long highScore = (Long) currentDataSnapshot.child("score").getValue();

                                        if(scoreVal > highScore) {

                                            //If score being push is higher than lowest score, replace data with lowest score's key.
                                            String key = currentDataSnapshot.getKey();
                                            postData(name, scoreVal, key);

                                        } else {
                                            Snackbar.make(findViewById(R.id.upload_score), "Not a high score.", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.", databaseError.toException());
                        }
                    });
                }
            }
        });
    }

    public void postData(String name, int score, String key) {

        HashMap<String, Object> data = generateMap(name, score);

        if(key.isEmpty())
            key = dataRef.push().getKey();
        dataRef.child(key).setValue(data);

        Snackbar.make(findViewById(R.id.upload_score), "Score uploaded.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public HashMap<String, Object> generateMap(String name, int score) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("name", name);
        data.put("score", score);
        data.put("ip-address", Utils.getIPAddress(true));

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = (cal.get(Calendar.MONTH) + 1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        data.put("date", year + "-"
                + (month > 9 ? month : "0" + month) + "-"
                + day);

        return data;
    }
}
