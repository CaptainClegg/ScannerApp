package com.example.misdclegg.scannerapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoilingActivity extends AppCompatActivity {

    private EditText mProductInput;
    private EditText mShelfInput;
    private EditText mQuantityInput;

    private Button mSignout;
    private Button mUpload;
    private Button mViewLocations;
    private Button mView;
    private RadioGroup mRadioGroup;

    private Spinner mWrappingSpinner;

    private TextView mTopUser;
    private TextView mTopDb;
    private TextView mTitle;

    private SoundPool mSoundPool;
    private int mSoundId;
    private int mSoundConfirm;

    DatabaseHelper mDatabaseHelper;
    private AlertDialog alertDialog;

    private String ip = "10.100.18.125";
    //private String ip = "dyr05";
    private String required = "net.sourceforge.jtds.jdbc.Driver";
    private String mRealDataBase = "testDB";
    //private String mRealDataBase = "INV";
    private String un;
    private String password;

    private String mMesage;
    private int mSpinnerIndex;

    private final String mNewLine = System.getProperty("line.separator");


    @Override
    public void onBackPressed(){
        mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
    }

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
        setContentView(R.layout.activity_coiling);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mProductInput = (EditText) findViewById(R.id.wrapping_serial);
        mShelfInput = (EditText) findViewById(R.id.wrapping_shelf);
        mQuantityInput = (EditText) findViewById(R.id.wrapping_quantity);

        mSignout = (Button) findViewById(R.id.sign_out);
        mUpload = (Button) findViewById(R.id.wrapping_upload);
        mViewLocations = (Button) findViewById(R.id.location_view);
        mView = (Button) findViewById(R.id.bottom_view);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mTopUser = (TextView) findViewById(R.id.top_user);
        mTopDb = (TextView) findViewById(R.id.top_db);
        mTitle = (TextView) findViewById(R.id.wrapping_title);

        mDatabaseHelper = new DatabaseHelper(this);
        mWrappingSpinner = (Spinner) findViewById(R.id.wrapping_select);


        //Used to keep the spinner title in view but not in selection
        List<String> list = new ArrayList<String>();
        list.add("Add Units");
        list.add("Remove Units");
        list.add("Adjust Units");
        list.add("Change Activity");
        final int listsize = list.size() - 1;
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list) {
            @Override
            public int getCount() {
                return(listsize); // Truncate the list
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWrappingSpinner.setAdapter(dataAdapter);
        mWrappingSpinner.setSelection(0);
        //end of spinner action

        try {
            Bundle myBundle = getIntent().getExtras();
            un = myBundle.getString("USERNAME", "");
            password = myBundle.getString("PASSWORD", "");
            mTopUser.setTextColor(Color.BLACK);
            mTitle.setTextColor(Color.BLACK);
            Connection con = CONN();
            if(con == null)
                throw new Exception("the db did not connect");
        }
        catch (Exception e){
            un = "You are not connected to a database";
            mTopDb.setVisibility(View.GONE);
            mTopUser.setTextColor(Color.RED);
            password = "";
        }

        try{
            Bundle myBundle = getIntent().getExtras();
            mProductInput.setText(myBundle.getString("SERIAL", ""));
            mShelfInput.setText(myBundle.getString("LOCATION", ""));
            mQuantityInput.setText(myBundle.getString("QUANTITY", ""));
        }
        catch (Exception e){

        }

        mTopUser.setText(un);
        //mTopDb.setText(db);


        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        mSoundId = mSoundPool.load(this, R.raw.error, 1);
        mSoundConfirm = mSoundPool.load(this, R.raw.confirmation, 1);

        mWrappingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mWrappingSpinner.getSelectedItemPosition() == 0) {
                    mUpload.setText("Add Units");
                    mTitle.setText("Add Items");
                }
                else if (mWrappingSpinner.getSelectedItemPosition() == 1){
                    mUpload.setText("Remove Units");
                    mTitle.setText("Remove Items");
                }
                else if (mWrappingSpinner.getSelectedItemPosition() == 2){
                    mUpload.setText("Make Adjustment");
                    mTitle.setText("Adjust Items");
                }
                if (mWrappingSpinner.getSelectedItemPosition() < 3) {
                    mSpinnerIndex = mWrappingSpinner.getSelectedItemPosition();
                    mWrappingSpinner.setSelection(listsize);
                }

            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        mProductInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (before + 1 < count){
                    mShelfInput.requestFocus();
                    mSoundPool.play(mSoundConfirm, 1, 1, 1, 0, 1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mShelfInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before + 1 < count){
                    mQuantityInput.requestFocus();
                    mSoundPool.play(mSoundConfirm, 1, 1, 1, 0, 1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mQuantityInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    mUpload.performClick();
                    return true;
                }
                return false;
            }
        });

        mSignout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CoilingActivity.this, LoginActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("USERNAME", un);
                intent.putExtras(myBundle);
                startActivity(intent);
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProductInput.getText().toString().length() > 0 && mShelfInput.getText().toString().length() > 0
                        && mQuantityInput.getText().toString().length() > 0 && mQuantityInput.getText().toString().length() < 6
                        && mRadioGroup.getCheckedRadioButtonId() != -1)
                    submitClear();
                else {
                    mMesage = "Check to make sure you have entered all the values, and try again";
                    alertUser(mMesage);
                    View radioView = mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId());
                    int radioIndex = mRadioGroup.indexOfChild(radioView);
                    System.out.println(radioIndex);
                }
            }
        });

        mViewLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoilingActivity.this, LocationActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("USERNAME", un);
                myBundle.putString("PASSWORD", password);
                intent.putExtras(myBundle);
                startActivity(intent);
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO display schedule from dashboard
            }
        });


    }

    @Override
    protected void onPause(){
        super.onPause();
        System.out.println("onpause was called");
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SPINNERINDEX", mSpinnerIndex);
        editor.apply();
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("onRestoreIn stanceCalled");
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        mSpinnerIndex = sharedPref.getInt("SPINNERINDEX", 0);
        mWrappingSpinner.setSelection(mSpinnerIndex);
    }

    public void submitClear() {
        if (mSpinnerIndex == 0)
            insertPallet(mProductInput.getText().toString(), mShelfInput.getText().toString(), mQuantityInput.getText().toString());
        else if (mSpinnerIndex == 1)
            removeUnits(mProductInput.getText().toString(), mShelfInput.getText().toString(), mQuantityInput.getText().toString());
        else if (mSpinnerIndex == 2)
            ;//todo modify units method
        else {
            mMesage = "Please select an activity";
            alertUser(mMesage);
        }
    }

    private void resetFields() {
        mSoundPool.play(mSoundConfirm, 1, 1, 1, 0, 1);
        mProductInput.getText().clear();
        mShelfInput.getText().clear();
        mQuantityInput.getText().clear();
        mProductInput.requestFocus();
    }

    private void alertUser(String content){
        alertDialog = new AlertDialog.Builder(CoilingActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(content);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
    }

    public void insertPallet(String newSerial, String newShelf, String addQuantity){


        try {
            int oldQuantity = 0;
            int newQuantity;

            Connection conn1 = CONN();
            String query1 = "select quantity from WrappingTable where serial = ? and shelf = ?";
            PreparedStatement ps = conn1.prepareStatement(query1);
            ps.setString(1, newSerial);
            ps.setString(2, newShelf);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                oldQuantity = resultSet.getInt("quantity");
                System.out.print(oldQuantity);
                conn1.close();
                newQuantity = oldQuantity + Integer.parseInt(addQuantity);

                if(newQuantity > 0){
                    String query = " update WrappingTable" +
                            " set quantity = ?" +
                            " where serial = ? and shelf = ?";
                    Connection conn = CONN();
                    PreparedStatement preparedStmt = conn.prepareStatement(query);
                    preparedStmt.setString (1, String.valueOf(newQuantity));
                    preparedStmt.setString (2, newSerial);
                    preparedStmt.setString (3, newShelf);
                    preparedStmt.execute();
                    conn.close();
                }
                else {
                    mMesage = "You were not able to add the items because you entered a negative number!";
                    alertUser(mMesage);
                    return;
                }
            }
            else {
                Connection conn = CONN();
                String query = " insert into WrappingTable (serial, shelf, quantity)" + "values (?, ?, ?)";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString (1, newSerial);
                preparedStmt.setString (2, newShelf);
                preparedStmt.setString (3, addQuantity);
                preparedStmt.execute();
                conn.close();
            }
        }
        catch (Exception e){
            mMesage = "You were not able to add the items because the connection failed!";
            alertUser(mMesage);
            return;
        }
        resetFields();

    }

    public void removeUnits(String newSerial, String newShelf, String removeQuantity){
        String query1 = "select quantity from WrappingTable where serial = ? and shelf = ?";

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
            newQuantity = oldQuantity - Integer.parseInt(removeQuantity);

            if(newQuantity > 0){
                String query = " update WrappingTable" +
                        " set quantity = ?" +
                        " where serial = ? and shelf = ?";
                Connection conn = CONN();
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString (1, String.valueOf(newQuantity));
                preparedStmt.setString (2, newSerial);
                preparedStmt.setString (3, newShelf);
                preparedStmt.execute();
                conn.close();
            }
            else if(newQuantity == 0){
                String query = "delete from WrappingTable where serial = ? and shelf = ?";
                Connection conn2 = CONN();
                PreparedStatement preparedStmt = conn2.prepareStatement(query);
                preparedStmt.setString (1, newSerial);
                preparedStmt.setString (2, newShelf);
                preparedStmt.execute();
                conn2.close();
            }
            else {
                mMesage = "You were not able to remove the items because you entered more items than there are on record for this pallet!";
                alertUser(mMesage);
                return;
            }
            resetFields();

        }
        catch (Exception e){
            mMesage = "the database connection did not operate correctly";
            alertUser(mMesage);
        }
    }
}
