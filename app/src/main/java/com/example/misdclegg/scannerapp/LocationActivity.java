package com.example.misdclegg.scannerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {

    private EditText mWorkOrderInput;

    private ListView mListView;

    private String ip = "10.100.18.125";
    private String required = "net.sourceforge.jtds.jdbc.Driver";
    private String mRealDataBase = "testDB";
    private String un;
    private String password;

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(required);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":1433;"
                    + "databaseName=" + mRealDataBase + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO1", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO2", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO3", e.getMessage());
        }
        return conn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        System.out.println("logcat works");

        mListView = (ListView) findViewById(R.id.location_list);

        mWorkOrderInput = (EditText) findViewById(R.id.wo_input);   //todo add text listener for enter

        mWorkOrderInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                System.out.println("the enter was called");
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    searchLocation(mWorkOrderInput.getText().toString());
                    System.out.println("the enter worked");
                    return true;
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TableLayout clickedRow = (TableLayout) view;
                TableRow rowTable = (TableRow) clickedRow.getChildAt(0);
                TextView location = (TextView) rowTable.getChildAt(0);
                TextView quantity = (TextView) rowTable.getChildAt(1);
                System.out.println(location.getText().toString());
                System.out.println(quantity.getText().toString());
                returnToActivity(location.getText().toString(), quantity.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("logcat from start");

        try {
            Bundle myBundle = getIntent().getExtras();
            un = myBundle.getString("USERNAME", "");
            password = myBundle.getString("PASSWORD", "");
            Connection con = CONN();
            if(con == null)
                throw new Exception("the db did not connect");
            System.out.println(myBundle);
        }
        catch (Exception e){
            un = "";
            password = "";
            System.out.println("error retrieving bundle");
        }
    }

    public void searchLocation(String woNumber){
        String query = "Select * from WrappingTable WHERE serial = ?";
        try{
            ArrayList<WrappingClass> arrayOfLocations = new ArrayList<WrappingClass>();

            Connection conn = CONN();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, woNumber);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                WrappingClass newRecord = new WrappingClass(rs.getString("shelf"), rs.getInt("quantity"));
                arrayOfLocations.add(newRecord);
                System.out.println("retrieved from database" + newRecord.getShelf() + newRecord.getQuantity());
            }

            LocationAdapter adapter = new LocationAdapter(this, arrayOfLocations);
            mListView.setAdapter(adapter);

            conn.close();
            System.out.print("succuessss");
        }
        catch (Exception e){
            System.out.println(e);
            System.out.println("there was an error that was caught");
        }
    }

    private void returnToActivity(String location, String quantity){
        Intent intent = new Intent(LocationActivity.this, CoilingActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("USERNAME", un);
        myBundle.putString("PASSWORD", password);
        myBundle.putString("LOCATION", location);
        myBundle.putString("SERIAL", mWorkOrderInput.getText().toString());
        myBundle.putString("QUANTITY", quantity);
        intent.putExtras(myBundle);
        startActivity(intent);
    }

}



/*String query1 = "select quantity from WrappingTable where serial = ? and shelf = ?";

        int oldQuantity = 0;
        int newQuantity;
        try {
            Connection conn1 = CONN();
            PreparedStatement ps = conn1.prepareStatement(query1);
            ps.setString (1, newSerial);
            ps.setString (2, newShelf);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                oldQuantity = resultSet.getInt("quantity");
            }
            conn1.close();
            newQuantity = oldQuantity - Integer.parseInt(removeQuantity);*/
