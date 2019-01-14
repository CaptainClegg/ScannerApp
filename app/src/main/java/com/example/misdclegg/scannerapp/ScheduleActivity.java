package com.example.misdclegg.scannerapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ScheduleActivity extends AppCompatActivity {

    private ListView mListView;

    private EditText mProdRunInput;

    private RadioGroup mShiftRadio;
    private RadioGroup mPadPoleRadio;
    private RadioButton mShift1;
    private RadioButton mShift2;
    private RadioButton mShift3;
    private RadioButton mPoleRadio;
    private RadioButton mPadRadio;

    private AlertDialog alertDialog;
    private ProgressBar mProgressBar;

    private SoundPool mSoundPool;
    private int mSoundId;
    private int mSoundConfirm;

    private String un;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mListView = (ListView) findViewById(R.id.schedule_list);
        mProdRunInput = (EditText) findViewById(R.id.wo_input);
        mShiftRadio = (RadioGroup) findViewById(R.id.radio_shift_group);
        mPadPoleRadio = (RadioGroup) findViewById(R.id.radio_pad_group);

        mShift1 = (RadioButton) findViewById(R.id.radio_shift_one);
        mShift2 = (RadioButton) findViewById(R.id.radio_shift_two);
        mShift3 = (RadioButton) findViewById(R.id.radio_shift_three);
        mPoleRadio = (RadioButton) findViewById(R.id.radio_pole_pole);
        mPadRadio = (RadioButton) findViewById(R.id.radio_pad_pad);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        mSoundId = mSoundPool.load(this, R.raw.error, 1);
        mSoundConfirm = mSoundPool.load(this, R.raw.confirmation, 1);

        //mprogressBar = (ProgressBar) findViewById(R.id.progress_loader);

        //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        try {
            Bundle myBundle = getIntent().getExtras();
            un = myBundle.getString("USERNAME", "");
            password = myBundle.getString("PASSWORD", "");
        }
        catch (Exception e){}
        //searchSchedule("181101-pl", "2nd");

        mProdRunInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (mProdRunInput.getText().toString().length() == 0
                            || mShiftRadio.getCheckedRadioButtonId() == -1
                            || mPadPoleRadio.getCheckedRadioButtonId() == -1)
                        alertUser("Make sure you have checked the radio buttons and entered a Production Run number like 'yymmdd'!");
                    else
                    checkInput();
                    return true;
                }
                return false;
            }
        });

        mShiftRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mProdRunInput.getText().toString().length() > 0
                        || mShiftRadio.getCheckedRadioButtonId() != -1
                        || mPadPoleRadio.getCheckedRadioButtonId() != -1)
                    checkInput();
            }
        });

        mPadPoleRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mProdRunInput.getText().toString().length() > 0
                        || mShiftRadio.getCheckedRadioButtonId() != -1
                        || mPadPoleRadio.getCheckedRadioButtonId() != -1)
                    checkInput();
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        System.out.println("onpause was called");
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("PRODRUN", mProdRunInput.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("onResume Called");
        try{
            SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            String serialString = sharedPref.getString("PRODRUN", "");
            if (sharedPref.getString("PRODRUN", "") == "")
                throw new Exception("no string to recover");
            //checkInput();
        }
        catch (Exception e) {
            //todo enter the current date
            //checkInput();
        }
    }

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(getString(R.string.required_jdbc));
            ConnURL = "jdbc:jtds:sqlserver://" + getString(R.string.ip_address) + ":1433;"
                    + "databaseName=" + getString(R.string.database) + ";user=" + un + ";password="
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

    public void checkInput(){

        mProgressBar.setVisibility(View.VISIBLE);

        String shift = "";
        String padPole = "";
        String prodRun = mProdRunInput.getText().toString();

        if(mShift1.isChecked())
            shift = "1st";
        else if(mShift2.isChecked())
            shift = "2nd";
        else if(mShift3.isChecked())
            shift = "3rd";

        if(mPadRadio.isChecked())
            padPole = "PD";
        else if(mPoleRadio.isChecked())
            padPole = "PL";

        prodRun = prodRun.concat("-" + padPole);

        searchSchedule(prodRun, shift);
        mProgressBar.setVisibility(View.GONE);

    }

    Thread t = new Thread(new Runnable() {
        public void run() {
            /*
             * Do something
             */
        }
    });





    public void searchSchedule(String prodRun, String shift){
        String query = "SELECT *" +
                "FROM [schedule]" +
                "WHERE [SHIFT] = ?" +
                "AND [PRDRUN] = ?";
        try{
            ArrayList<ScheduleClass> arrayOfSchedules = new ArrayList<ScheduleClass>();

            Connection conn = CONN();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, shift);
            ps.setString(2, prodRun);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                int inStock = 0;
                try {
                    String query1 = "SELECT *" +
                            "FROM [WrappingTable]" +
                            "WHERE [serial] = ?";
                    Connection conn1 = CONN();
                    PreparedStatement ps1 = conn1.prepareStatement(query1);
                    ps1.setString(1, rs.getString("COILWO"));
                    ResultSet rs1 = ps1.executeQuery();

                    while (rs1.next()) {
                        inStock = inStock + rs1.getInt("quantity");
                    }
                    conn1.close();
                }
                catch (Exception e){                }
                ScheduleClass newRecord = new ScheduleClass(rs.getString("SEQ"), rs.getString("COILWO"), rs.getFloat("QTY"), inStock);
                arrayOfSchedules.add(newRecord);
                System.out.println("retrieved from database" + newRecord.getSequence() + newRecord.getWorkOrder() + newRecord.getQuantity());
            }
            ScheduleAdapter adapter = new ScheduleAdapter(this, arrayOfSchedules);
            mListView.setAdapter(adapter);

            conn.close();
            System.out.println("success.........");
        }
        catch (Exception e){
            System.out.println(e);
            alertUser("Make sure you have entered a Production Run number like 'yymmdd'!");
        }
    }

    private void alertUser(String content){
        alertDialog = new AlertDialog.Builder(ScheduleActivity.this).create();
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
}
