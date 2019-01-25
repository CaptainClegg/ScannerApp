package com.example.misdclegg.scannerapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CoilingActivity extends AppCompatActivity {

    private EditText mProductInput;
    private EditText mShelfInput;
    private EditText mQuantityInput;

    //private Button mSignout;
    private Button mUpload;
    private Button mViewLocations;
    private Button mViewSchedule;
    private Button mViewEmpty;
    private Button mViewFullList;

    //private RadioGroup mRadioGroup;
    //private RadioButton mRadioEmpty;

    Handler handler = new Handler();

    //private TextView mTopUser;
    //private TextView mTopDb;

    private SoundPool mSoundPool;
    private int mSoundId;
    private int mSoundConfirm;

    DatabaseHelper mDatabaseHelper;
    private AlertDialog alertDialog;


    private int i, j, k;

    private String mMesage;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coiling);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mProductInput = (EditText) findViewById(R.id.wrapping_serial);
        mShelfInput = (EditText) findViewById(R.id.wrapping_shelf);
        mQuantityInput = (EditText) findViewById(R.id.wrapping_quantity);

        //mSignout = (Button) findViewById(R.id.sign_out);
        mUpload = (Button) findViewById(R.id.wrapping_upload);
        mViewLocations = (Button) findViewById(R.id.location_view);
        mViewSchedule = (Button) findViewById(R.id.bottom_view);
        mViewEmpty = (Button) findViewById(R.id.empty_view);
        mViewFullList = (Button) findViewById(R.id.full_view);

        mDatabaseHelper = new DatabaseHelper(this);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            Bundle myBundle = getIntent().getExtras();
            String bundleSerial = myBundle.getString("SERIAL", "");
            String bundleLocation = myBundle.getString("LOCATION", "");
            String bundleQuantity = myBundle.getString("QUANTITY", "");
            SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            if(bundleSerial.length() > 0) {
                mProductInput.setText(bundleSerial);

                (new Thread(){
                    @Override
                    public void run(){
                        for( i=0; i < 255; i++){
                            handler.post(new Runnable(){
                                public void run(){
                                    mProductInput.getBackground().setColorFilter(Color.argb(100, i, i, 255), PorterDuff.Mode.SRC_OVER);
                                }
                            });
                            // next will pause the thread for some time
                            try{ sleep(10); }
                            catch(Exception e){ }
                        }
                        handler.post(new Runnable(){
                            public void run(){
                                mProductInput.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.DST);
                            }
                        });
                    }
                }).start();
            }
            else
                mProductInput.setText(sharedPref.getString("START_COILING_SERIAL", ""));
            if(bundleLocation.length() > 0) {
                mShelfInput.setText(bundleLocation);

                (new Thread(){
                    @Override
                    public void run(){
                        for( j=0; j < 255; j++){
                            handler.post(new Runnable(){
                                public void run(){
                                    mShelfInput.getBackground().setColorFilter(Color.argb(100, j, j, 255), PorterDuff.Mode.SRC_OVER);
                                }
                            });
                            // next will pause the thread for some time
                            try{ sleep(10); }
                            catch(Exception e){ }
                        }
                        handler.post(new Runnable(){
                            public void run(){
                                mShelfInput.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.DST);
                            }
                        });
                    }
                }).start();

            }
            else
                mShelfInput.setText(sharedPref.getString("START_COILING_LOCATION", ""));
            if(bundleQuantity.length() > 0) {
                mQuantityInput.setText(bundleQuantity);

                (new Thread(){
                    @Override
                    public void run(){
                        for( k=0; k < 255; k++){
                            handler.post(new Runnable(){
                                public void run(){
                                    mQuantityInput.getBackground().setColorFilter(Color.argb(100, k, k, 255), PorterDuff.Mode.SRC_OVER);
                                }
                            });
                            // next will pause the thread for some time
                            try{ sleep(10); }
                            catch(Exception e){ }
                        }
                        handler.post(new Runnable(){
                            public void run(){
                                mQuantityInput.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.DST);
                            }
                        });
                    }
                }).start();

            }
            else
                mQuantityInput.setText(sharedPref.getString("START_COILING_QUANTITY", ""));
        }
        catch (Exception e){

        }

        //mTopUser.setText(un);
        //mTopDb.setText(db);

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
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
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

        /*mSignout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CoilingActivity.this, LoginActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("USERNAME", un);
                intent.putExtras(myBundle);
                startActivity(intent);
            }
        });*/

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProductInput.getText().toString().length() > 0 && mShelfInput.getText().toString().length() > 0
                        && mQuantityInput.getText().toString().length() > 0)
                        //&& mRadioGroup.getCheckedRadioButtonId() != -1
                    submitClear();
                else {
                    alertUser("Check to make sure you have entered all the values, and try again");
                }
            }
        });

        mViewLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoilingActivity.this, LocationActivity.class);
                startActivity(intent);
            }
        });

        mViewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoilingActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        mViewEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoilingActivity.this, EmptyLocationsActivity.class);
                startActivity(intent);
            }
        });

        mViewFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoilingActivity.this, FullActivity.class);
                //Bundle myBundle = new Bundle();
                //myBundle.putString("USERNAME", un);
                //myBundle.putString("PASSWORD", password);
                //intent.putExtras(myBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("START_COILING_SERIAL", mProductInput.getText().toString());
        editor.putString("START_COILING_LOCATION", mShelfInput.getText().toString());
        editor.putString("START_COILING_QUANTITY", mQuantityInput.getText().toString());
        editor.apply();

        mSoundPool.release();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        mSoundId = mSoundPool.load(this, R.raw.error, 1);
        mSoundConfirm = mSoundPool.load(this, R.raw.confirmation, 1);

        try{
            Connection conn = new ConnectionHelper().getConnection();
            if (conn == null)
                alertUser("You are not connected to the database. Make sure you are connected to the wifi: ERMCOmfg.");
            conn.close();
        }
        catch (Exception e){
            alertUser("You are not connected to the database. Make sure you are connected to the wifi: ERMCOmfg.");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
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
    }

    public void submitClear() {
            updateBin(mProductInput.getText().toString(), mShelfInput.getText().toString(), mQuantityInput.getText().toString());
            updateFlags(mProductInput.getText().toString());
    }

    private void resetFields() {
        mSoundPool.play(mSoundConfirm, 1, 1, 1, 0, 1);
        mProductInput.getText().clear();
        mShelfInput.getText().clear();
        mQuantityInput.getText().clear();
        mProductInput.requestFocus();
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void updateBin(String newSerial, String newShelf, String updateQuantity){


        try {
            int oldQuantity = 0;

            Connection conn1 = new ConnectionHelper().getConnection();
            String query1 = "select quantity from WrappingTable where serial = ? and shelf = ?";
            PreparedStatement ps = conn1.prepareStatement(query1);
            ps.setString(1, newSerial);
            ps.setString(2, newShelf);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                oldQuantity = resultSet.getInt("quantity");

                conn1.close();

                if(Integer.parseInt(updateQuantity) > oldQuantity){
                    String query2 = " update WrappingTable" +
                            " set quantity = ?" +
                            " where serial = ? and shelf = ?";
                    Connection conn = new ConnectionHelper().getConnection();
                    PreparedStatement preparedStmt = conn.prepareStatement(query2);
                    preparedStmt.setString (1, updateQuantity);
                    preparedStmt.setString (2, newSerial);
                    preparedStmt.setString (3, newShelf);
                    preparedStmt.execute();
                    conn.close();
                }
                else if(Integer.parseInt(updateQuantity) <= oldQuantity){
                    if(Integer.parseInt(updateQuantity) > 0){
                        String query3 = " update WrappingTable" +
                                " set quantity = ?" +
                                " where serial = ? and shelf = ?";
                        Connection conn = new ConnectionHelper().getConnection();
                        PreparedStatement preparedStmt = conn.prepareStatement(query3);
                        preparedStmt.setString (1, updateQuantity);
                        preparedStmt.setString (2, newSerial);
                        preparedStmt.setString (3, newShelf);
                        preparedStmt.execute();
                        conn.close();
                    }
                    else if(Integer.parseInt(updateQuantity) == 0){
                        String query4 = "delete from WrappingTable where serial = ? and shelf = ?";
                        Connection conn2 = new ConnectionHelper().getConnection();
                        PreparedStatement preparedStmt = conn2.prepareStatement(query4);
                        preparedStmt.setString (1, newSerial);
                        preparedStmt.setString (2, newShelf);
                        preparedStmt.execute();
                        conn2.close();
                        //if(mRadioEmpty.isChecked()){
                        //    checkForEmptyShelf(newShelf);
                        //}
                    }
                }
            }
            else if (Integer.parseInt(updateQuantity) >= 0){
                conn1.close();
                Connection conn = new ConnectionHelper().getConnection();
                String query5 = " insert into WrappingTable (serial, shelf, quantity)" + "values (?, ?, ?)";
                PreparedStatement preparedStmt = conn.prepareStatement(query5);
                preparedStmt.setString (1, newSerial);
                preparedStmt.setString (2, newShelf);
                preparedStmt.setString (3, updateQuantity);
                preparedStmt.execute();
                conn.close();
            }
        }
        catch (Exception e){
            alertUser("Your entry may not have been submitted.");
            return;
        }

    }

    public void updateFlags(String woNumber){
        //while (rs.next()){
            int inStock = 0;
            int orderQuantity = 0;
            String placedFlag = "";
            String removedFlag = "";

            try {


                String query1 = "SELECT *" +
                        "FROM [WrappingTable]" +
                        "WHERE [serial] = ?";
                Connection conn1 = new ConnectionHelper().getConnection();
                PreparedStatement ps1 = conn1.prepareStatement(query1);
                ps1.setString(1, woNumber);
                ResultSet rs1 = ps1.executeQuery();

                while (rs1.next()) {
                    inStock = inStock + rs1.getInt("quantity");
                }
                conn1.close();

                String query = "SELECT *" +
                        "FROM [schedule]" +
                        "WHERE [COILWO] = ?";
                Connection conn = new ConnectionHelper().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, woNumber);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    placedFlag = rs.getString("Flag_CoilsDONE");
                    //removedFlag = rs.getString("Flag_SchedDONE");


                    orderQuantity = (int)rs.getFloat("QTY");
                }
                conn.close();


                if(orderQuantity == inStock){
                    String query6 = "UPDATE [schedule] SET [Flag_CoilsDONE] = ? WHERE [COILWO] = ?";
                    Connection conn6 = new ConnectionHelper().getConnection();
                    PreparedStatement ps6 = conn6.prepareStatement(query6);
                    ps6.setString(1, "Y");
                    ps6.setString(2, woNumber);
                    ps6.executeUpdate();
                    conn6.close();

                }
                if(placedFlag != null && inStock == 0){
                    String query2 = "UPDATE [schedule] SET [Flag_SchedDONE] = ? WHERE [COILWO] = ?";
                    Connection conn2 = new ConnectionHelper().getConnection();
                    PreparedStatement ps2 = conn2.prepareStatement(query2);
                    ps2.setString(1, "Y");
                    ps2.setString(2, woNumber);
                    ps2.executeUpdate();
                    conn2.close();
                }
            }
            catch (Exception e){

            }
        resetFields();
    }

    /*public void checkForEmptyShelf(String shelf){
        try {
            Connection conn1 = CONN();
            String query = "select * from WrappingTable where shelf = ?";
            PreparedStatement ps = conn1.prepareStatement(query);
            ps.setString(1, shelf);
            ResultSet resultSet = ps.executeQuery();
            String[] stringArray = new String[30];
            String missingParts = "";
            int i = 0;
            while (resultSet.next()) {
                stringArray[i] = resultSet.getString("serial");
                //stringArray[i]);
                missingParts = missingParts.concat(stringArray[i] + "  ");
                //missingParts);
                i++;
            }
            conn1.close();
            if(i != 0){
                //"the if statement was called");
                mMesage = "Are you sure the bin is empty? " + missingParts + " was believed to be here.";
                questionUser(mMesage, stringArray[0]);
            }
        }
        catch (Exception e){
        }
    }*/

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

    /*private void questionUser(String content, final String firstSerial){
        //"the allert was called");
        alertDialog = new AlertDialog.Builder(CoilingActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(content);
        //alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ignore",
          //      new DialogInterface.OnClickListener() {
            //        public void onClick(DialogInterface dialog, int which) {
              //          dialog.dismiss();
                //
                  //  }
                //});
        //alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Shelf not Empty",
          //      new DialogInterface.OnClickListener() {
            //        public void onClick(DialogInterface dialog, int which) {
              //          dialog.dismiss();
                //    }
                //});
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Show Locations of Missing Item",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //"the user has time");
                        Intent intent = new Intent(CoilingActivity.this, LocationActivity.class);
                        Bundle myBundle = new Bundle();
                        myBundle.putString("USERNAME", un);
                        myBundle.putString("PASSWORD", password);
                        myBundle.putString("MISSING", firstSerial);
                        intent.putExtras(myBundle);
                        startActivity(intent);
                    }
                });
        alertDialog.show();
        mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
    }*/

}
