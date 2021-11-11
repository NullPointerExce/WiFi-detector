package com.example.wifidetector;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class detailsActivity extends AppCompatActivity {

    private String bssidname ="";
    private StringBuffer wifiDetail;
    private TextView detailTextView;
    private TextView levelTextView;

    private Timer timer;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    int count =0;

    private LineChart mLineChart;
    private List<Entry> entries = new ArrayList<Entry>();

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

        //line chart
        mLineChart = findViewById(R.id.linchart);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(false);


        //For real time details information and line chart.
        levelTextView = findViewById(R.id.leveldetails);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                drawGraph(scan());
            }
        }, 0, 3000); //refresh per  3s
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private int scan(){
        int level = 0;
        wifiManager.startScan();
        wifiDetail = new StringBuffer();
        results = wifiManager.getScanResults();
        for(ScanResult scanResult : results){
            if(bssidname.equals(scanResult.BSSID)){
                wifiDetail.append("Real time line graph: ").append("\n");
                wifiDetail.append("Signal Strength: ").append(scanResult.level).append("\n");
                count++;
                level = scanResult.level;
            }
        }
        levelTextView.setText(wifiDetail);
        return level;
    }

    private void drawGraph(int level){
        if(entries.size()>5){
            entries.remove(0);
        }
        entries.add(new Entry(count, level));
        LineDataSet dataSet = new LineDataSet(entries, "Wi-Fi Signal Strength (dBm)");
        dataSet.setColor(Color.GREEN);
        dataSet.setLineWidth(2);
        LineData lineData = new LineData(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }
}