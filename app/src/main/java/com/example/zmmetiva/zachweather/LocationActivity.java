package com.example.zmmetiva.zachweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by zmmetiva on 12/10/15.
 */
public class LocationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
    }

    public void setLocationClick(View view) {
        EditText editText = (EditText) findViewById(R.id.editZipCode);

        if (editText.getText().length() == 0) {
            Toast.makeText(this.getApplicationContext(),"Enter a Zip Code!", Toast.LENGTH_SHORT).show();
        }

        else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ZIP_CODE", editText.getText());
            startActivity(intent);
            finish();
        }

    }
}
