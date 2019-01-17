package com.example.misdclegg.scannerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOGIN = 0;

    private EditText mSerialInput;
    private Button mSubmit;
    private Button mSignout;
    private Button mDownload;
    private Button mUpload;
    private Button mView;

    private ToggleButton mAutomatic;
    private TextView mTopUser;
    private TextView mTopDb;
    private SoundPool mSoundPool;
    private int mSoundId;
    private int mSoundConfirm;

    DatabaseHelper mDatabaseHelper;

    private String db;
    private String un;
    private String password;


    @Override
    public void onBackPressed(){
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
    }*/

    public void insertSerial(String newSerial){
        String query = " insert into TestTable (serialNumber)" + "values (?)";

        try {
            Connection conn = new ConnectionHelper().getConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, newSerial);
            preparedStmt.execute();
            conn.close();

        }
        catch (Exception e){
            System.out.println("error after connection and before close");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSerialInput = (EditText) findViewById(R.id.serial_input);
        mSubmit = (Button) findViewById(R.id.submit_serial);
        mSignout = (Button) findViewById(R.id.sign_out);
        mDownload = (Button) findViewById(R.id.bottom_download);
        mUpload = (Button) findViewById(R.id.bottom_upload);
        mView = (Button) findViewById(R.id.bottom_view);

        mAutomatic = (ToggleButton) findViewById(R.id.manual);
        mSubmit.setVisibility(View.INVISIBLE);

        mTopUser = (TextView) findViewById(R.id.top_user);
        mTopDb = (TextView) findViewById(R.id.top_db);

        mDatabaseHelper = new DatabaseHelper(this);

        try {
            Bundle myBundle = getIntent().getExtras();
            un = myBundle.getString("USERNAME", "");
            db = myBundle.getString("DATABASE", "");
            password = myBundle.getString("PASSWORD", "");
            mTopDb.setTextColor(Color.BLACK);
            mTopUser.setTextColor(Color.BLACK);
            Connection con = new ConnectionHelper().getConnection();
            if(con == null)
                throw new Exception("the db did not connect");
        }
        catch (Exception e){
            un = "";
            db = "You are not connected to a database";
            mTopUser.setVisibility(View.GONE);
            mTopDb.setTextColor(Color.RED);
            password = "";
        }

        mTopUser.setText(un);
        mTopDb.setText(db);


        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        mSoundId = mSoundPool.load(this, R.raw.error, 1);
        mSoundConfirm = mSoundPool.load(this, R.raw.confirmation, 1);

        mAutomatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (mAutomatic.isChecked())
                    mSubmit.setVisibility(View.GONE);
                else
                    mSubmit.setVisibility(View.VISIBLE);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSerialInput.getText().length() == 13
                        && !mAutomatic.isChecked() && mSerialInput.getText().charAt(0) == 'E'
                        && mSerialInput.getText().charAt(1) == 'R'){
                    submitClear();
                }
                else{
                    //todo please enter a valid code
                    mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
                }
            }


        });


        mSerialInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if ( count == 13 && mAutomatic.isChecked() && mSerialInput.getText().charAt(0) == 'E'
                        && mSerialInput.getText().charAt(1) == 'R'){
                    submitClear();
                }
                else if (count > 0 && mAutomatic.isChecked()){
                    mSerialInput.getText().clear();
                    mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSignout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("USERNAME", un);
                myBundle.putString("DATABASE", db);
                intent.putExtras(myBundle);
                startActivity(intent);
            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Connection conn = new ConnectionHelper().getConnection();
                    String query = "SELECT serial, descript, classification, condition, reporter FROM InventoryTable";
                    //TODO transfer microsoft sql to sqlite
                }
                catch (Exception e){

                }
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = mDatabaseHelper.getData();
                ArrayList<String> serialList = new ArrayList<>();
                ArrayList<String> descriptionList = new ArrayList<>();
                ArrayList<String> classList = new ArrayList<>();
                ArrayList<String> conditionList = new ArrayList<>();
                ArrayList<String> userList = new ArrayList<>();
                while (data.moveToNext()) {
                    //get the value from the database in column 1
                    //then add it to the ArrayList
                    serialList.add(data.getString(0));
                    descriptionList.add(data.getString(1));
                    classList.add(data.getString(2));
                    conditionList.add(data.getString(3));
                    userList.add(data.getString(4));
                }
                //TODO transfer sqlite to misql
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    public void submitClear() {
        insertSerial(mSerialInput.getText().toString());
        mSoundPool.play(mSoundConfirm, 1, 1, 1, 0, 1);
        mSerialInput.getText().clear();
    }

    /*public void sqlConnect() {
        DB db = new DB();
        String port = "1433";
        String dbTitle = "testDB";
        String userId = "sa";
        String password= "admin123";
        db.dbConnect("jdbc:sqlserver://:"+port+";DatabaseName="+dbTitle,userId,password);
    }

    class DB{

        public void dbConnect(  String db_connect_string,
                                String db_userid,
                                String db_password){
            try{
                Connection conn = DriverManager.getConnection(
                        db_connect_string,
                        db_userid,
                        db_password);
                System.out.println( "connected" );
            }
            catch( SQLException e ){
                e.printStackTrace();
            }
        }
    };*/

}