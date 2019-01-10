package com.example.misdclegg.scannerapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.view.inputmethod.InputMethodManager;
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
    //private String ip = "dyr05";
    private String required = "net.sourceforge.jtds.jdbc.Driver";
    private String mRealDataBase = "testDB";
    //private String mRealDataBase = "INV";
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
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        mWorkOrderInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before + 1 < count) {
                    searchLocation(mWorkOrderInput.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TableLayout clickedRow = (TableLayout) view;
                TableRow rowTable = (TableRow) clickedRow.getChildAt(0);
                TextView location = (TextView) rowTable.getChildAt(0);
                TextView quantity = (TextView) rowTable.getChildAt(2);
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

    @Override
    protected void onPause(){
        super.onPause();
        System.out.println("onpause was called");
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("WONUMBER", mWorkOrderInput.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("onRestoreIn stanceCalled");
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String serialString = sharedPref.getString("WONUMBER", "");
        mWorkOrderInput.setText(serialString);
        searchLocation(serialString);
        mWorkOrderInput.setEnabled(false);
        mWorkOrderInput.setEnabled(true);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

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
            System.out.print("success");
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
