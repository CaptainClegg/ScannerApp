package com.example.misdclegg.scannerapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FullActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private Context mContext;

    private ListView mListView;

    private AlertDialog alertDialog;

    private SoundPool mSoundPool;
    private int mSoundId;
    private int mSoundConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mListView = (ListView) findViewById(R.id.full_list);
        mContext = this;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout clickedRow = (LinearLayout) view;
                TextView woNumber = (TextView) clickedRow.getChildAt(0);
                TextView location = (TextView) clickedRow.getChildAt(2);
                TextView quantity = (TextView) clickedRow.getChildAt(4);
                //woNumber.getText().toString());
                returnToActivity(woNumber.getText().toString(), location.getText().toString(), quantity.getText().toString());
            }
        });


    }

    @Override
    protected void onResume(){
        super.onResume();
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        mSoundId = mSoundPool.load(this, R.raw.error, 1);
        mSoundConfirm = mSoundPool.load(this, R.raw.confirmation, 1);
        new FileAsync().execute();
    }
    @Override
    protected void onPause(){
        super.onPause();
        mSoundPool.release();
    }

    private void returnToActivity(String serial, String location, String quantity){
        Intent intent = new Intent(FullActivity.this, CoilingActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("LOCATION", location);
        myBundle.putString("SERIAL", serial);
        myBundle.putString("QUANTITY", quantity);
        intent.putExtras(myBundle);
        startActivity(intent);
    }

    class FileAsync extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<FullClass> doInBackground(Void... params) {

            String query = "SELECT *" +
                    "FROM [WrappingTable]";
            try{
                ArrayList<FullClass> arrayOfEverything = new ArrayList<FullClass>();

                Connection conn = new ConnectionHelper().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()){
                    FullClass newRecord = new FullClass(rs.getString("serial"), rs.getString("shelf"), rs.getInt("quantity"));
                    arrayOfEverything.add(newRecord);
                    //"retrieved from database" + rs.getString("serial") + newRecord.getShelf() + newRecord.getQuantity());
                }
                conn.close();
                //"success.........");
                //inflateList(arrayOfSchedules);
                return arrayOfEverything;
            }
            catch (Exception e){
                //e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList arrayList){
            try {
                FullAdapter adapter = new FullAdapter(mContext, arrayList);
                mListView.setAdapter(adapter);
            }
            catch (Exception e){
                alertUser("You are not connected to the database. Make sure you are connected to the wifi: ERMCOmfg.");
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void alertUser(String content){
        alertDialog = new AlertDialog.Builder(FullActivity.this).create();
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
}
