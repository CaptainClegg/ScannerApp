package com.example.misdclegg.scannerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper extends AppCompatActivity {
    //private String password, un;

    public ConnectionHelper(){}

    public Connection getConnection(String un, String password) {
        //SharedPreferences sharedPref = context.getSharedPreferences("PREFERENCE", Context.MODE_NO_LOCALIZED_COLLATORS);
        //String un = sharedPref.getString("USERNAME", "");
        //String password = sharedPref.getString("PASSWORD", "");
        int i = 0;

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
          //      .permitAll().build();
        i = -3;
        //StrictMode.setThreadPolicy(policy);
        i = -2;
        Connection conn;
        i = -1;
        String ConnURL;
        i = 1;

        try {

            //Class.forName(getString(R.string.required_jdbc));
            i = 2;
            ConnURL = "jdbc:jtds:sqlserver://" + "10.100.18.125" + ":1433;"
                    + "databaseName=" + "testDB" + ";user=" + un + ";password="
                    + password + ";";
            i = 3;
            conn = DriverManager.getConnection(ConnURL);
            i = 4;

        } catch (SQLException se) {
            //Log.e("ERRO1", se.getMessage());
            conn = null;
        } //catch (ClassNotFoundException e) {
           // Log.e("ERRO2", e.getMessage());
            //conn = null;}
          catch (Exception e) {
            //Log.e("ERRO3", e.getMessage());
            //try{
            //System.out.println(i);}
            //catch (Exception f){
                System.out.println("not even i was created");
            //}
            conn = null;
        }
        return conn;
    }

}
