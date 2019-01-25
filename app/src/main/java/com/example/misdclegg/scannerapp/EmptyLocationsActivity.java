package com.example.misdclegg.scannerapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

public class EmptyLocationsActivity extends AppCompatActivity {

    private ListView mListView;

    private String un;
    private String password;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_locations);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mListView = (ListView) findViewById(R.id.empty_location_list);

        try {
            Bundle myBundle = getIntent().getExtras();
            un = myBundle.getString("USERNAME", "");
            password = myBundle.getString("PASSWORD", "");
            Connection con = new ConnectionHelper().getConnection();
            if(con == null)
                throw new Exception("the db did not connect");
            //myBundle);
        }
        catch (Exception e){
            un = "";
            password = "";
            //"error retrieving bundle");
        }

        populateList();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clickedRow = (TextView) view;
                //clickedRow.getText().toString());
                returnToActivity(clickedRow.getText().toString());
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        alertDialog = new AlertDialog.Builder(EmptyLocationsActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("You are not connected to the database. Make sure you are connected to the wifi: ERMCOmfg.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        try{
            Connection conn = new ConnectionHelper().getConnection();
            if (conn == null)
                alertDialog.show();
            conn.close();
        }
        catch (Exception e){
            alertDialog.show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        populateList();
    }

    private void populateList(){
        String query = "Select * from [EmptyBINs]";
        try{
            String[] arrayOfEmptyLocations = new String[1];

            Connection conn = new ConnectionHelper().getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next()){
                arrayOfEmptyLocations[i] = rs.getString("BINname");
                //"retrieved from database" + rs.getString("BINname"));
                arrayOfEmptyLocations = Arrays.copyOf(arrayOfEmptyLocations, arrayOfEmptyLocations.length + 1);
                i++;
            }

            ArrayAdapter<String> emptyLocationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayOfEmptyLocations);
            mListView.setAdapter(emptyLocationsAdapter);

            conn.close();
            //"success.........");
        }
        catch (Exception e){
            //e);
            //"there was an error that was caught");
        }
    }

    private void returnToActivity(String location){
        Intent intent = new Intent(EmptyLocationsActivity.this, CoilingActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("USERNAME", un);
        myBundle.putString("PASSWORD", password);
        myBundle.putString("LOCATION", location);
        intent.putExtras(myBundle);
        startActivity(intent);
    }
}
