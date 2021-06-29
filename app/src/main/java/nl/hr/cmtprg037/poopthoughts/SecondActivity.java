package nl.hr.cmtprg037.poopthoughts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    //Constants
    public static final String KEY_CONNECTIONS = "KEY_CONNECTIONS";
    private static final int REQ_CODE_LAST_KNOWN_LOCATION = 1;
    private static final int REQ_CODE_LOCATION_UPDATES = 2;

    //INI
    private ArrayList<Post> allPosts;
    private FusedLocationProviderClient fusedLocationClient;
    private MediaPlayer mp = null;
    LocationRequest locationRequest;
    boolean requestingLocationUpdates = false;
    private LocationCallback locationCallback;


    //API
    private final static String URI = "https://api.quotable.io/random";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        getQuote();

        //Get location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(MainActivity.LOG_TAG, "geen locatie gevonden");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.d(MainActivity.LOG_TAG, "Locatie update");

                }
            }
        };


        //Create posts list, and add existing posts
        allPosts = new ArrayList<Post>();
        allPosts = getPosts();


        //Convert to string (jsonAllPosts)
        Gson gson = new Gson();
        String jsonAllPosts = gson.toJson(allPosts);
        Log.d(MainActivity.LOG_TAG, "json posts : " + jsonAllPosts);


        //Convert json back to list
        Type type = new TypeToken<List<Post>>() {
        }.getType();
        List<Post> allPostsList = gson.fromJson(jsonAllPosts, type);
        Log.d(MainActivity.LOG_TAG, "list posts: " + allPostsList);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Update hello text
        TextView hello = (TextView) findViewById(R.id.textView_hello);
        hello.setText(String.format("Hello %s", SettingsActivity.getName(this)));

        //Start location updates
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    //Post poop thought
    public void post(View v) {

        //Get written post
        EditText editTextCreate = (EditText) findViewById(R.id.editText_create);
        int msgLength = editTextCreate.getText().length();
        Log.d(MainActivity.LOG_TAG, Integer.toString(msgLength));

        //Minimum length of thought + error handling
        int minThoughtLength = 6;
        if (editTextCreate.getText().length() < minThoughtLength) {
            Toast toast = Toast.makeText(getApplicationContext(), "Only " + Integer.toString(msgLength) + " characters. Please provide at least " + minThoughtLength + " characters.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }


        Log.d(MainActivity.LOG_TAG, "Post text: " + editTextCreate.getText());

        //Set name and message
        String name = SettingsActivity.getName(this);
        String message = editTextCreate.getText().toString();
        Post post = new Post(name, message);
        allPosts.add(post);


        //Get location access, set location on post, save post
        getCurrentLocation();

        //Play poop audio when sound is on in settings
        if (SettingsActivity.isSoundOn(this)) {
            playSound();
        }
    }

    private void playSound() {

        //turn off when already playing
        if (mp != null) {
            mp.release();
        }

        // Play sound
        mp = MediaPlayer.create(this, R.raw.poop);
        mp.start();

    }

    private void savePosts() {

        //Convert to string (jsonAllPosts)
        Gson gson = new Gson();
        String jsonAllPosts = gson.toJson(allPosts);
        Log.d(MainActivity.LOG_TAG, "json posts : " + jsonAllPosts);

        //Get, add post, and save shared preferences.
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = getPreferences(MODE_PRIVATE).edit();
        prefsEditor.putString("post", jsonAllPosts);
        prefsEditor.apply();

        Log.d(MainActivity.LOG_TAG, "Saved posts: " + jsonAllPosts);
    }

    private ArrayList<Post> getPosts() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

//        remove all posts
//        mPrefs.edit().remove("post").apply();


        //Retrieve data
        Gson retrieveGson = new Gson();
        String json2 = mPrefs.getString("post", "");

        if (!json2.equals("")) {

            //If data is not empty.
            Type type = new TypeToken<List<Post>>() {
            }.getType();
            ArrayList<Post> allPostsList = retrieveGson.fromJson(json2, type);


            Log.d(MainActivity.LOG_TAG, "Returning existing data");
            return allPostsList;

        } else {

            //If data is empty, add some test data.
            ArrayList<Post> testPostsList = new ArrayList<>();

            Post testPost1 = new Post("Henk", "This is a test msg.");
            Post testPost2 = new Post("Piet", "This is another test msg.");

            testPostsList.add(testPost1);
            testPostsList.add(testPost2);

            Log.d(MainActivity.LOG_TAG, "No existing data. Returning test data");
            return testPostsList;
        }

    }

    //Open settings activity
    public void openSettings(View v) {
        Intent i = new Intent(SecondActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    //Start new activity and send all posts with it as an arraylist.
    public void showAll(View v) {

        //Pass ArrayList allPosts to next activity.
        Intent i = new Intent(SecondActivity.this, ShowAllActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", (Serializable) allPosts);
        i.putExtra("BUNDLE", args);
        startActivity(i);
    }

    public void getCurrentLocation() {
        //get lat and long

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE_LAST_KNOWN_LOCATION);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            Log.d(MainActivity.LOG_TAG, "Locatie gevonden: " + location.getLatitude() + " " + location.getLongitude());
                            locationHandler(location.getLatitude(), location.getLongitude());
                        } else {
                            Log.d(MainActivity.LOG_TAG, "Geen last known locatie gevonden");
                        }

                        requestingLocationUpdates = true;
                        startLocationUpdates();
                    }
                });
    }

    //Add location to post
    private void locationHandler(double latitude, double longitude) {
        Post post = allPosts.get(allPosts.size() - 1);
        post.latitude = latitude;
        post.longitude = longitude;

        savePosts();
    }

    //Handle permissions before posting
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case REQ_CODE_LAST_KNOWN_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.
                    getCurrentLocation();

                } else {
                    //Permission is denied
                    finish();
                }

        }
        // Other 'case' lines to check for other
        // permissions this app might request.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    //API call to get a random quote.
    private void getQuote() {

        TextView quoteTextView = findViewById(R.id.textView_quote);


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, URI, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(response);

                            Log.d(MainActivity.LOG_TAG, "json array: " + jsonArray.toString());
                            String content = "";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject quote = (JSONObject) jsonArray.get(i);
                                content = (String) quote.get("content");
                                Log.d(MainActivity.LOG_TAG, "content: " + content);
                                quoteTextView.setText(content);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            quoteTextView.setText("Internet unavailable");

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(MainActivity.LOG_TAG, "That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(jsonRequest);

    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE_LOCATION_UPDATES);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}

