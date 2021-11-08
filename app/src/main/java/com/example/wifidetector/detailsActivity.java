package com.example.wifidetector;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class detailsActivity extends AppCompatActivity {

    private String bssidname ="";
    private StringBuffer wifiDetail;
    private TextView detailTextView;
    private TextView levelTextView;
    //need change name.

    private Timer mTimer;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    int count =0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        bssidname =getIntent().getExtras().getString("bssidname");
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        detailTextView = findViewById(R.id.details);
        wifiDetail = new StringBuffer();
        results = wifiManager.getScanResults();
        for(ScanResult scanResult : results){
            if(bssidname.equals(scanResult.BSSID)){
                wifiDetail.append("SSID: ").append(scanResult.SSID).append("\n");
                wifiDetail.append("MAC Address: ").append(bssidname).append("\n");
                wifiDetail.append("Frequency: ").append(scanResult.frequency).append("\n");
                wifiDetail.append("ChannelWidth: ").append(scanResult.channelWidth).append("\n");
                wifiDetail.append("Authentication/Encryption : ").append(scanResult.capabilities).append("\n");
            }
        }
        detailTextView.setText(wifiDetail);

        //For real time line graph
        levelTextView = findViewById(R.id.leveldetails);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startscan();

            }
        }, 0, 31000); //refresh per  31s

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startscan(){
        wifiManager.startScan();
        wifiDetail = new StringBuffer();
        results = wifiManager.getScanResults();
        for(ScanResult scanResult : results){
            if(bssidname.equals(scanResult.BSSID)){
                wifiDetail.append("real time line graph: ").append("\n");
                wifiDetail.append("Signal Strength: ").append(scanResult.level).append("\n");
                wifiDetail.append("Count(every 31 second): ").append(count++);
            }
        }
        levelTextView.setText(wifiDetail);
    }

}