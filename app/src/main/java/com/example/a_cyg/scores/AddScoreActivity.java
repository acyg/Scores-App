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

import org.json.JSONArray;
import org.json.JSONObject;

public class AddScoreActivity extends AppCompatActivity {

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
                        JSONObject inputObj = new JSONObject();
                        inputObj.put("name", name);
                        inputObj.put("score", Integer.parseInt(score));

                        final View fView = view;
                        new DatabaseTask(DatabaseTask.METHOD_POST, new MainActivity.dataCallback() {
                            @Override
                            public void onResult(JSONArray jsonArray) {
                                try {
                                    JSONObject result = (JSONObject) ((JSONObject) jsonArray.get(0)).get("result");
                                    Snackbar.make(fView, (String) result.get("message"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } catch(Exception e) {

                                }
                            }
                        }, inputObj).execute();
                    } catch (Exception e) {
                        Log.e("AddScoreActivity", e.getMessage());
                    }

                }
            }
        });
    }

}
