package com.project.first.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String JSON_STRING;
    private GoogleMap mMap;
    private Marker m;
    private final static LatLng NEW_YORK = new LatLng(40.7127, -74.0059);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Context context = getApplicationContext();
        CharSequence text = "Hello World!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        new BackendGetJSON().execute();

        // Call google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        // Attach fragment by Java
//        MyFragment myFragment = new MyFragment();
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content_activity_my, myFragment, "someTag");
//        fragmentTransaction.commit();

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NEW_YORK, 12));
        m = mMap.addMarker(new MarkerOptions()
                .title("N train")
                .snippet("The best train:)")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_arow))
                .position(NEW_YORK));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                updatePosition();
            } finally {
                handler.postDelayed(runnable, 1000);
            }
        }
    };

    // Click ENTER to post a message
    public void enterPostMessage(View view) {
        handler.removeCallbacks(runnable);

        startActivity(new Intent(this, PostMessage.class));
    }

    // Click LOAD to load JSON data
    public void getJSON(View view) {
        runnable.run();

        new BackendGetJSON().execute();
    }

    // Update marker position in map
    public void updatePosition() {
        m.setPosition(new LatLng(m.getPosition().latitude - 0.0003, m.getPosition().longitude + 0.0003));
    }

    // Get JSON from get_json.php
    class BackendGetJSON extends AsyncTask<Void, Void, String> {
        String json_url;
        String result = "";

        @Override
        protected void onPreExecute() {
            json_url = "http://neoskywalker7.com/projectx/get_json.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                try {
                    JSONObject jo = new JSONObject(stringBuilder.toString());
                    JSONArray ja = jo.getJSONArray("result");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject ja_jo = ja.getJSONObject(i);
                        result = result + "id " + ja_jo.getString("id") + ":    " + ja_jo.getString("message") + "\n";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            TextView textView = (TextView) findViewById(R.id.textView_getjson);
            textView.setText(result);

            Context context = getApplicationContext();
            CharSequence text = "Load Data Success!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


}

