package nl.hr.cmtprg037.poopthoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowAllActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //INI
    private ArrayList<Post> allPosts;
    private ArrayAdapter<Post> adapter;


    //Constants
    public static final String ARRAY_LIST = "ARRAYLIST";
    public static final String LAT_EXTRA = "1";
    public static final String LONG_EXTRA = "2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        Log.d(MainActivity.LOG_TAG, "Show all activity created!");


        //Get arraylist from previous activity.
        Intent i = getIntent();
        Bundle args = i.getBundleExtra("BUNDLE");
        allPosts = (ArrayList<Post>) args.getSerializable(ARRAY_LIST);


        Log.d(MainActivity.LOG_TAG, "all posts from showAllActivity: " + allPosts);


        //Init listview
        ListView lv = findViewById(R.id.listView_allPosts);
        lv.setOnItemClickListener(this);

        //Link adapter
        adapter = new ArrayAdapter<Post>(this,
                android.R.layout.simple_list_item_1, allPosts);
        lv.setAdapter(adapter);
    }

    //On item click, start new activity with location sent
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(MainActivity.LOG_TAG, "er is geklikt op post: "+ position);

        Log.d(MainActivity.LOG_TAG, "Naam: "+ allPosts.get(position).name);

        //Start maps activity met long en lat
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra(LAT_EXTRA, Double.toString(allPosts.get(position).latitude));
        i.putExtra(LONG_EXTRA, Double.toString(allPosts.get(position).longitude));
        startActivity(i);
    }
}