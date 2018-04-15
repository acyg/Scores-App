package com.example.a_cyg.scores;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton upload = (FloatingActionButton) findViewById(R.id.upload_activity);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddScoreActivity.class));
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.content_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        database = FirebaseDatabase.getInstance();
        dataRef = database.getReference("scores/tetris");

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setupView(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }

    public void setupView(DataSnapshot dataSnapshot) {
        try {
            Object value = dataSnapshot.getValue();
            JSONArray dataSet;

            if(value != null) {
                JSONObject dataObj = new JSONObject(value.toString());

                Iterator<String> keys = dataObj.keys();
                ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();

                while (keys.hasNext()) {
                    dataList.add((JSONObject) dataObj.get(keys.next()));
                }

                JSONObject[] dataArray = new JSONObject[dataList.size()];
                dataList.toArray(dataArray);

                Comparator<JSONObject> cmp = new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        try {
                            return (Integer) o2.get("score") - (Integer) o1.get("score");
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                };

                Arrays.sort(dataArray, cmp);
                dataSet = new JSONArray(dataArray);

            } else dataSet = new JSONArray();

            mAdapter = new ScoreAdapter(dataSet);
            mRecyclerView.setAdapter(mAdapter);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}