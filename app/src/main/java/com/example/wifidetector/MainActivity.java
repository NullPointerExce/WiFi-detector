package com.example.wifidetector;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private List<ScanResult> results;
    private ArrayList<String> stringList = new ArrayList<>();
    private ArrayList<Result> resultList = new ArrayList<>();
    private ArrayAdapter adapter;
    private MyBroadcast wifiReceiver;


    public class Result{
        String ssid;
        String bssid;//for distinguish same name
        int frequency;
        int level;

        public Result(String name){
            this.ssid=name;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });

        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //check location Permission and open it.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,stringList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, detailsActivity.class);
                intent.putExtra("bssidname",resultList.get(i).bssid);
                startActivity(intent);
            }
        });
    }


    private void scan(){
        stringList.clear();
        resultList.clear();
        IntentFilter intentFilter = new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiReceiver = new MyBroadcast();
        registerReceiver( wifiReceiver , intentFilter);
        wifiManager.startScan();
    }


    private class MyBroadcast extends BroadcastReceiver{

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {

            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for(ScanResult scanResult : results){
                if(scanResult.SSID==null||scanResult.SSID.length()==0){
                    Result result = new Result("(Hidden SSID)");
                    result.frequency = scanResult.frequency;
                    result.level= scanResult.level;
                    result.bssid=scanResult.BSSID;
                    resultList.add(result);
                    continue;
                }

                Result result = new Result(scanResult.SSID);
                result.frequency = scanResult.frequency;
                result.level= scanResult.level;
                result.bssid=scanResult.BSSID;
                resultList.add(result);

            }

            Collections.sort(resultList, new Comparator<Result>() {
                @Override public int compare(Result r1, Result r2) {
                    return r2.level- r1.level;
                }
            });

            for(Result result : resultList){
                stringList.add(result.ssid +"  CH "+FrequencyToChannel(result.frequency)
                        + "   " +result.level +" dBm");
            }
            adapter.notifyDataSetChanged();
        }
    }
    
    private int FrequencyToChannel(int frequency) {
        if (frequency >= 2412 && frequency <= 2484) {
            return (frequency - 2412) / 5 + 1;
        } else if (frequency >= 5170 && frequency <= 5825) {
            return (frequency - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

}