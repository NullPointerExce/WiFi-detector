package com.example.wifidetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private WifiManager wifiManager;
    //private WifiInfo wifiInformation; // get connected AP information
    private ListView listView;
    private Button buttonScan;
    private int size=0;
    private List<ScanResult> results;
    private ArrayList<String> arraylist = new ArrayList<>();
    private ArrayAdapter adapter;
    private MyBroadcast wifiReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startscan();
            }
        });
        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);



        if(!wifiManager.isWifiEnabled()){
            //Toast.makeText(this, "wifi is disabled. atuo enable it.",Toast.LENGTH_LONG).show();
            Toast.makeText(this, "wifi is disabled. Please turn on wifi",Toast.LENGTH_LONG).show();
            //wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arraylist);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, detailsActivity.class);
                startActivity(intent);
            }
        });

        //scanwifi();

    }

    private void startscan(){
        arraylist.clear();
        IntentFilter intentFilter = new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiReceiver = new MyBroadcast();
        registerReceiver( wifiReceiver , intentFilter);
        wifiManager.startScan();
        //Toast.makeText(this, "scanning wifi...",Toast.LENGTH_LONG).show();

    }


    private class MyBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for(ScanResult scanResult : results){
                if(scanResult.SSID==null||scanResult.SSID.length()==0){
                    continue;
                }
                arraylist.add(scanResult.SSID + " - " + scanResult.capabilities);

            }

            adapter.notifyDataSetChanged();
        }
    }
//    protected void onDestroy(){
//        super.onDestroy();
//        unregisterReceiver(wifiReceiver);
//    }

//    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            results = wifiManager.getScanResults();
//
//            for(ScanResult scanResult : results){
//                arraylist.add(scanResult.SSID + " - " + scanResult.frequency);
//            }
//            adapter.notifyDataSetChanged();
//            unregisterReceiver(this);
//        }
//    };


}