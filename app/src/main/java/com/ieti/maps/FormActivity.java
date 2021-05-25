package com.ieti.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FormActivity extends AppCompatActivity {

    public static final String NAME = "com.ieti.maps.NAME";
    public static final String DESCRIPTION = "com.ieti.maps.DESCRIPTION";
    public static final String LATITUDE = "com.ieti.maps.LATITUDE";
    public static final String LONGITUDE = "com.ieti.maps.LONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
    }

    public void onSubmitClicked(View view){
        Boolean submit = true;
        EditText name = (EditText) findViewById(R.id.name);
        EditText description = (EditText) findViewById(R.id.description);
        EditText longitude = (EditText) findViewById(R.id.longitude);
        EditText latitude = (EditText) findViewById(R.id.latitude);
        if (name.getText().toString().length() == 0){
            submit = false;
            name.setError("Name expected");
        }
        if (description.getText().toString().length() == 0){
            submit = false;
            description.setError("Description expected");
        }
        try{
            Double.parseDouble(latitude.getText().toString());
        } catch(NumberFormatException e) {
            submit = false;
            latitude.setError("Double expected");
        }
        try{
            Double.parseDouble(longitude.getText().toString());
        } catch(NumberFormatException e) {
            submit = false;
            longitude.setError("Double expected");
        }
        if (submit){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(NAME, name.getText().toString());
            intent.putExtra(DESCRIPTION, description.getText().toString());
            intent.putExtra(LATITUDE, latitude.getText().toString());
            intent.putExtra(LONGITUDE, longitude.getText().toString());
            startActivity(intent);
        }
    }
}