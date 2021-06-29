package nl.hr.cmtprg037.poopthoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

    }

    public void clickedNext(View v) {

        EditText editTextName = (EditText) findViewById(R.id.editText_name);


        SettingsActivity.changeName(this, editTextName.getText().toString());
        Log.d(MainActivity.LOG_TAG, "Name edit: " + editTextName.getText());


        Intent i = new Intent(NameActivity.this, SecondActivity.class);
        startActivity(i);

    }

}