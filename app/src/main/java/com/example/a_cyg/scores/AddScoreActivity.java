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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class AddScoreActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText nameField = ((EditText) findViewById(R.id.player_name_input));
                EditText scoreField = ((EditText) findViewById(R.id.player_score_input));
                String name = nameField.getText().toString();
                String score = scoreField.getText().toString();
                if(name.isEmpty() || score.isEmpty()) {
                    Snackbar.make(view, "Please enter both player name and score.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    try {
                        HashMap<String, Object> data = new HashMap<String, Object>();
                        data.put("name", name);
                        data.put("score", Integer.parseInt(score));
                        data.put("ip-address", Utils.getIPAddress(true));

                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = (cal.get(Calendar.MONTH) + 1);
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        data.put("date", year + "-"
                                + (month > 9 ? month : "0" + month) + "-"
                                + day);

                        database = FirebaseDatabase.getInstance();
                        dataRef = database.getReference("scores/tetris");

                        String key = dataRef.push().getKey();
                        dataRef.child(key).setValue(data);

                        Snackbar.make(view, "Score uploaded.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (Exception e) {
                        Log.e("AddScoreActivity", e.getMessage());
                    }

                }
            }
        });
    }

}
