package nl.hr.cmtprg037.poopthoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public final static String LOG_TAG = "poep";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //Start volgende activity
    public void startClicked(View v) {
        Log.d(LOG_TAG, "Start button clicked.");

        //Checks if name has already been set or not.
        if (SettingsActivity.getName(this).equals("Your name")) {
            Intent i = new Intent(MainActivity.this, NameActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(i);
        }

    }

    //Exit app
    public void exitClicked(View v) {
        Log.d(LOG_TAG, "Exit button clicked.");
        finish();
    }
}