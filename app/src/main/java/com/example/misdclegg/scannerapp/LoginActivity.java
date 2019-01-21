package com.example.misdclegg.scannerapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserView;
    private EditText mPassView;
    private Spinner mDbView;
    private Button mLogButton;

    private AlertDialog alertDialog;

    private String mUserName;
    private String mPassword;

    private SoundPool mSoundPool;
    private int mSoundId;

    Connection connect;

    //dont allow back button
    @Override
    public void onBackPressed(){
        mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart(){
        super.onStart();

        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        mSoundId = mSoundPool.load(this, R.raw.error, 1);

        mUserView = (EditText) findViewById(R.id.user_name);
        mPassView = (EditText) findViewById(R.id.password);
        mDbView = (Spinner) findViewById(R.id.db_select);
        mLogButton = (Button) findViewById(R.id.login);

        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("WONUMBER");
        editor.remove("PRODRUN");
        editor.remove("USERNAME");
        editor.remove("PASSWORD");
        editor.remove("SHIFT_RADIO");
        editor.remove("PADPOLE_RADIO");
        editor.remove("START_COILING_SERIAL");
        editor.remove("START_COILING_LOCATION");
        editor.remove("START_COILING_QUANTITY");
        editor.apply();

        try {
            Bundle myBundle = getIntent().getExtras();
            mUserName = myBundle.getString("USERNAME", "");
        }
        catch (Exception e){
            mUserName = "";
        }

        //auto fill from the activity that called it

         mDbView.setSelection(2);
         mUserView.setText(mUserName);

         mPassView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    mLogButton.performClick();
                    return true;
                }
                return false;
            }
        });

         mLogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mUserName = mUserView.getText().toString();
                mPassword = mPassView.getText().toString();

                connect = CONN(mUserName, mPassword);
                if(connect == null) {
                    alertUser();
                    return;
                }

                Bundle myBundle = new Bundle();
                myBundle.putString("USERNAME", mUserName);
                myBundle.putString("PASSWORD", mPassword);


                if (mDbView.getSelectedItemPosition() == 0) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtras(myBundle);
                    startActivity(intent);
                }
                else if(mDbView.getSelectedItemPosition() == 2){
                    Intent intent = new Intent(LoginActivity.this, CoilingActivity.class);
                    intent.putExtras(myBundle);
                    startActivity(intent);
                }
                else
                    alertUser();
            }
        });


    }

    public Connection CONN(String un, String password) {
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
            conn = null;
        } catch (ClassNotFoundException e) {
            conn = null;
        } catch (Exception e) {
            conn = null;
        }
        return conn;
    }

    private void alertUser(){
        alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Username or password is not recognized for the selected activity!");
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
