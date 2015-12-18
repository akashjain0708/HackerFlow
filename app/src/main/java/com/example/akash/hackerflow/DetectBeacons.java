package com.example.akash.hackerflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DetectBeacons extends Activity {

    private BeaconManager beaconManager;
    ListView beaconsList;
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    BeaconsListAdapter arrayAdapter;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_beacons);
        beaconsList = (ListView) findViewById(R.id.beaconsList);
        region = new Region("regionId", UUID.fromString(ESTIMOTE_PROXIMITY_UUID), null, null);

        beaconManager = new BeaconManager(getApplicationContext());

        // We want the beacons heartbeat to be set at one second.
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(5),
                0);

        // Method called when a beacon gets...
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            // ... close to us.
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {

            }

            // ... far away from us.
            @Override
            public void onExitedRegion(Region region) {

            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                //Log.d("Hi", "Hi");
                //Log.d("Hi", Integer.toString(beacons.size()));
                if (!beacons.isEmpty()) {
                    ArrayList<BeaconDetails> beaconList = new ArrayList<BeaconDetails>();
                    for (int i = 0; i < beacons.size(); i++) {
                        Beacon nearest = beacons.get(i);
                        BeaconDetails bd = new BeaconDetails();
                        bd.setBeaconName("Beacon " + Integer.toString(i + 1));
                        bd.setBeaconDistance(Utils.computeProximity(nearest).toString());
                        beaconList.add(bd);
                    }

                    arrayAdapter = new BeaconsListAdapter(beaconList, getApplicationContext()) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            return super.getView(position, convertView, parent);
                        }
                    };
                    beaconsList.setAdapter(arrayAdapter);
                }

            }
        });

        beaconsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), hackerApplication.class);
                //Bundle extras = new Bundle();
                //extras.putString("Bus Data", s);
                //intent.putExtras(extras);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Log.d("Inside", stop.getText().toString().trim());
                //intent.putExtra("Stop Name", stopName);
                //progressDialog.dismiss();
                startActivity(intent);

            }
        });



        // Connect to the beacon manager...
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    // ... and start the monitoring
                    beaconManager.startMonitoring(region);
                    beaconManager.startRanging(region);
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detect_beacons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }
}
