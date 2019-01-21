package com.example.misdclegg.scannerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper extends AppCompatActivity {
    //private String password, un;

    public ConnectionHelper(){}

    public Connection getConnection() {

        String un = "sa";
        String password = "admin123";

        Connection conn;
        String ConnURL;

        try {

            //Class.forName(getString(R.string.required_jdbc));
            ConnURL = "jdbc:jtds:sqlserver://" + "dyr09" + ":1433/"
                    + "WindingCoilInv;" + "instance=" + "SQLEXPRESS"
                    + ";user=" + "winding" + ";password=" + "coil123" + ";";
            conn = DriverManager.getConnection(ConnURL);

        } catch (SQLException se) {
            //Log.e("ERRO1", se.getMessage());
            conn = null;
        } //catch (ClassNotFoundException e) {
           // Log.e("ERRO2", e.getMessage());
            //conn = null;}
          catch (Exception e) {
            //Log.e("ERRO3", e.getMessage());
            //try{
            ////i);}
            //catch (Exception f){
                //"not even i was created");
            //}
            conn = null;
        }
        return conn;
    }

}
