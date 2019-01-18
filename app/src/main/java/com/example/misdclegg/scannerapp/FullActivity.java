package com.example.misdclegg.scannerapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
                System.out.println(woNumber.getText().toString());
                returnToActivity(woNumber.getText().toString(), location.getText().toString(), quantity.getText().toString());
            }
        });

        new FileAsync().execute();
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

                Connection conn = CONN();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()){
                    FullClass newRecord = new FullClass(rs.getString("serial"), rs.getString("shelf"), rs.getInt("quantity"));
                    arrayOfEverything.add(newRecord);
                    System.out.println("retrieved from database" + rs.getString("serial") + newRecord.getShelf() + newRecord.getQuantity());
                }
                conn.close();
                System.out.println("success.........");
                //inflateList(arrayOfSchedules);
                return arrayOfEverything;
            }
            catch (Exception e){
                System.out.println(e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList arrayList){
            FullAdapter adapter = new FullAdapter(mContext, arrayList);
            mListView.setAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
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
            ConnURL = "jdbc:jtds:sqlserver://" + "dyr09" + ":1433/"
                    + "WindingCoilInv;" + "instance=" + "SQLEXPRESS"
                    + ";user=" + "winding" + ";password=" + "coil123" + ";";
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
}
