package nl.hr.cmtprg037.poopthoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {


    //final static vars
    public final static String OPT_SOUND = "key_sound";
    public final static boolean OPT_SOUND_DEFAULT = true;

    public final static String OPT_DARKMODE = "key_darkmode";
    public final static boolean OPT_DARKMODE_DEFAULT = false;

    public final static String OPT_NAME = "key_name";
    public final static String OPT_NAME_DEFAULT = "Your name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection deprecation
        addPreferencesFromResource(R.xml.settings);

        //Log current name
        Log.d(MainActivity.LOG_TAG, "Current name: " + PreferenceManager.getDefaultSharedPreferences(this).getString(OPT_NAME, OPT_NAME_DEFAULT));

    }

    //Check if sound is on
    public static boolean isSoundOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SOUND, OPT_SOUND_DEFAULT);
    }

    //Check if darkmode is on
    public static boolean isDarkModeOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_DARKMODE, OPT_DARKMODE_DEFAULT);
    }

    //Check name
    public static String getName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_NAME, OPT_NAME_DEFAULT);
    }

    //Change name
    public static void changeName(Context context, String name) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(OPT_NAME,name);
        editor.apply();
    }
}