package com.example.misdclegg.scannerapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper{
    //private String password, un;

    public ConnectionHelper(){}

    public Connection getConnection() {

        Connection conn;
        String ConnURL;

        try {

            //Class.forName(getString(R.string.required_jdbc));
            ConnURL = "jdbc:jtds:sqlserver://" + "dyr09" + ":1433/"
                    + "WindingCoilInv;" + "instance=" + "SQLEXPRESS"
                    + ";user=" + "winding" + ";password=" + "coil123" + ";";
            conn = DriverManager.getConnection(ConnURL);

        }
        catch (Exception e) {
            conn = null;
        }
        return conn;
    }

}
