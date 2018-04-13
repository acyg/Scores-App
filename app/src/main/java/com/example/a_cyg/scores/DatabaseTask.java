package com.example.a_cyg.scores;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class DatabaseTask extends AsyncTask {
    private MainActivity.dataCallback mCallback;
    private String method;
    private JSONObject inputData;
    static final String METHOD_GET = "GET";
    static final String METHOD_POST = "POST";

    public DatabaseTask(String method, MainActivity.dataCallback callback){
        this.method = method;
        mCallback = callback;
    }

    public DatabaseTask(String method, MainActivity.dataCallback callback, JSONObject inputs){
        this.method = method;
        mCallback = callback;
        inputData = inputs;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        String link = "http://13.56.107.102/scores/public/api/tetris_scores";
        //String link = "http://13.56.107.102/scores/public/api/tetris_scores/fire";
        switch(method) {
            case METHOD_GET:
                try {
                    URL url = new URL(link);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(method);
                    urlConnection.connect();
                    if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }

                        return sb.toString();
                    } else Log.d("DatabaseTask - get", "Connection status: " + urlConnection.getResponseMessage());
                    urlConnection.disconnect();
                } catch (Exception e) {
                    Log.e("DatabaseTask - get", e.getMessage());
                }
                break;
            case METHOD_POST:
                link += "/add";
                try {
                    URL url = new URL(link);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());

                    outputStreamWriter.write(inputData.toString());
                    outputStreamWriter.flush();

                    if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }

                        return sb.toString();
                    } else Log.d("DatabaseTask - post", "Connection status: " + urlConnection.getResponseMessage());
                    urlConnection.disconnect();
                } catch (Exception e) {
                    Log.e("DatabaseTask - post", e.getMessage());
                }
                break;

        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result){
        switch (method) {
            case METHOD_GET:
                try {
                    mCallback.onResult(new JSONArray((String) result));
                } catch (Exception e) {
                    Log.e("DatabaseTask - get", e.getMessage());
                }
                break;
            case METHOD_POST:
                try {
                    JSONArray jary = new JSONArray();
                    jary.put(new JSONObject((String) result));
                    mCallback.onResult(jary);
                } catch (Exception e) {
                    Log.e("DatabaseTask - get", e.getMessage());
                }
                break;
        }
    }
}