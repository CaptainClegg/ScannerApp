package com.example.misdclegg.scannerapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ScheduleActivity extends AppCompatActivity{

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
    private Context mContext;

    private SoundPool mSoundPool;
    private int mSoundId;
    private int mSoundConfirm;

    private String un;
    private String password;

    Context context;

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
        //mProgressBar.setVisibility(View.GONE);

        context = this;
        //mprogressBar = (ProgressBar) findViewById(R.id.progress_loader);

        //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //mProgressBar.setVisibility(View.VISIBLE);


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
                        && mShiftRadio.getCheckedRadioButtonId() != -1
                        && mPadPoleRadio.getCheckedRadioButtonId() != -1)
                    checkInput();
            }
        });

        mPadPoleRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mProdRunInput.getText().toString().length() > 0
                        && mShiftRadio.getCheckedRadioButtonId() != -1
                        && mPadPoleRadio.getCheckedRadioButtonId() != -1)
                    checkInput();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout clickedRow = (LinearLayout) view;
                TextView woNumber = (TextView) clickedRow.getChildAt(2);
                TextView inStock = (TextView) clickedRow.getChildAt(6);
                //woNumber.getText().toString());
                returnToActivity(woNumber.getText().toString(), Integer.parseInt(inStock.getText().toString()));
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        //"onpause was called");
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("PRODRUN", mProdRunInput.getText().toString());
        editor.putInt("SHIFT_RADIO", mShiftRadio.getCheckedRadioButtonId());
        editor.putInt("PADPOLE_RADIO", mPadPoleRadio.getCheckedRadioButtonId());
        editor.apply();

        mSoundPool.release();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //"onResume Called");
        try{
            SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            mProdRunInput.setText(sharedPref.getString("PRODRUN", ""));
            if (sharedPref.getString("PRODRUN", "") == "")
                throw new Exception("no string to recover");
            mShiftRadio.check(sharedPref.getInt("SHIFT_RADIO", -1));
            mPadPoleRadio.check(sharedPref.getInt("PADPOLE_RADIO", -1));
            //checkInput();
        }
        catch (Exception e) {
            //todo enter the current date
            //checkInput();
        }

        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        mSoundId = mSoundPool.load(this, R.raw.error, 1);
        mSoundConfirm = mSoundPool.load(this, R.raw.confirmation, 1);

    }


    /*@SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(getString(R.string.required_jdbc));
            ConnURL = "jdbc:jtds:sqlserver://" + "dyr09" + ":1433/"
                    + "WindingCoilInv;" + "instance=" + "SQLEXPRESS"
                    + ";user=" + "winding" + ";password=" + "coil123" + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
        }
        return conn;
    }*/

    public void checkInput(){


        mProgressBar.bringToFront();

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

        new FileAsync().execute(prodRun, shift);


    }

    //public void searchSchedule(String prodRun, String shift){


///////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////
        /*
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

                String s, t;
                s = rs.getString("Flag_SchedDONE");
                t = rs.getString("Flag_CoilsDONE");
                if (s != null) {
                    //rs.getString("Flag_SchedDONE"));
                    s = (s.trim()).toUpperCase();
                    if(s.equals("Y")) {
                        continue;
                    }
                }

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
                ScheduleClass newRecord = new ScheduleClass(rs.getString("SEQ"), rs.getString("COILWO"), rs.getFloat("QTY"), inStock, t);
                arrayOfSchedules.add(newRecord);
                //"retrieved from database" + newRecord.getSequence() + newRecord.getWorkOrder() + newRecord.getQuantity());
            }
            ScheduleAdapter adapter = new ScheduleAdapter(this, arrayOfSchedules);
            mListView.setAdapter(adapter);

            conn.close();
            //"success.........");
        }
        catch (Exception e){
            //e);
            alertUser("Make sure you have entered a Production Run number like 'yymmdd'!");
        }
        */
    //}

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

    private void returnToActivity(String woNumber, int inStock){
        Bundle myBundle = new Bundle();
        //myBundle.putString("USERNAME", un);
        //myBundle.putString("PASSWORD", password);
        //",,,,,,,,,,,,,," + inStock);
        if(inStock > 0) {
            Intent intent = new Intent(ScheduleActivity.this, LocationActivity.class);
            myBundle.putString("MISSING", woNumber);
            myBundle.putString("ACTIVITY", "ScheduleActivity");
            intent.putExtras(myBundle);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(ScheduleActivity.this, CoilingActivity.class);
            myBundle.putString("SERIAL", woNumber);
            intent.putExtras(myBundle);
            startActivity(intent);
        }

    }

    public void inflateList(ArrayList<ScheduleClass> arrayOfSchedules){
        ScheduleAdapter adapter = new ScheduleAdapter(this, arrayOfSchedules);
        mListView.setAdapter(adapter);
    }


    class FileAsync extends AsyncTask<String, String, ArrayList> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<ScheduleClass> doInBackground(String... params) {
            String prodRun = params[0];
            String shift = params[1];
            String query = "SELECT *" +
                    "FROM [schedule]" +
                    "WHERE [SHIFT] = ?" +
                    "AND [PRDRUN] = ?";
            try{
                ArrayList<ScheduleClass> arrayOfSchedules = new ArrayList<ScheduleClass>();

                Connection conn = new ConnectionHelper().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, shift);
                ps.setString(2, prodRun);
                ResultSet rs = ps.executeQuery();

                while (rs.next()){

                    String s, t;
                    s = rs.getString("Flag_SchedDONE");
                    t = rs.getString("Flag_CoilsDONE");
                    if (s != null) {
                        //rs.getString("Flag_SchedDONE"));
                        s = (s.trim()).toUpperCase();
                        if(s.equals("Y")) {
                            continue;
                        }
                    }

                    int inStock = 0;
                    try {
                        String query1 = "SELECT *" +
                                "FROM [WrappingTable]" +
                                "WHERE [serial] = ?";
                        Connection conn1 = new ConnectionHelper().getConnection();
                        PreparedStatement ps1 = conn1.prepareStatement(query1);
                        ps1.setString(1, rs.getString("COILWO"));
                        ResultSet rs1 = ps1.executeQuery();

                        while (rs1.next()) {
                            inStock = inStock + rs1.getInt("quantity");
                        }
                        conn1.close();
                    }
                    catch (Exception e){                }
                    ScheduleClass newRecord = new ScheduleClass(rs.getString("SEQ"), rs.getString("COILWO"), rs.getFloat("QTY"), inStock, t);
                    arrayOfSchedules.add(newRecord);
                    //"retrieved from database" + newRecord.getSequence() + newRecord.getWorkOrder() + newRecord.getQuantity());
                }
                conn.close();
                //"success.........");
                //inflateList(arrayOfSchedules);
                return arrayOfSchedules;
            }
            catch (Exception e){
                //e);

            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {


        }
        @Override
        protected void onPostExecute(ArrayList arrayList){
            try {
                ScheduleAdapter adapter = new ScheduleAdapter(context, arrayList);
                mListView.setAdapter(adapter);
            }
            catch (Exception e){
                alertUser("You are not connected to the database. Make sure you are connected to the wifi: ERMCOmfg.");
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
